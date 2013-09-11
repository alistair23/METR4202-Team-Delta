function [selclstrs,clstrs]=findlaserboardpoints(angleVector,rangeMatrix,laserDivisor,clstrs,tiest,riest,Nci,BoardCorners,thresholds,manselen,debug,manclstrs)
% FINDLASERBOARDPOINTS is the main automatic boar detection function.
%
% FINDLASERBOARDPOINTS is used to automatically detect and extract laser
% points belonging to the calibration plane.
% 
% The measures relied on by the function are the frequency of occurence,
% the intial estimate and the matching of lengths.
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     laserDivisor: laserDivisor is a scalar representing the resolution
%     mode at which the SICK laser was operating.
% 
%     clstrs: MxN array. clstrs should be the same size as rangeMatrix.
%     Each element in clstrs is an integer indicating the line cluster the
%     range to which reading belongs.
% 
%     tiest: Initial estimate of translation vector. If tiest is not passed
%     as input or passed as empty, the intial estimate is not used for
%     board selection.
% 
%     riset: Initial estimate of rotation matrix.
% 
%     Nci: 3xM array containg the normal vector of the calibration plane in
%     the camera coordinate frame.
% 
%     BoardCorners: 1xM cell array with each cell containing 3 elements:
%         n_sq_x: number of squares of the calibration chessboard along the
%         x direction.
% 
%         n_sq_y: number of squares of the calibration chessboard along the
%         y direction.
% 
%         corners: 3x((n_sq_x+1)*(n_sq_y+1)) array with the coordinates of
%         the chessboard corners in the camera frame.
% 
%     thresholds: structure of one of the following forms:
%         1. {fthlo,lenth} initial estimate is not used
%         2. {fthlo,iesthlo,lenth} initial estimate is used
%         3. {fthlo,fthhi,iesthlo,iesthhi,lenth,} used when lines near the
%         borders of the classifier need to be user verified
%     
%     manselen: optional flag indicating wether the program should prompt the user
%     for manual selection, default is 0.
% 
%     debug: flag for debugging
% 
%     manclstrs: for debugging
% 
% OUTPUTS:
%     clstrs: MxN array. Each element in clstrs is an integer indicating
%     the line cluster the range to which reading belongs.
% 
%     selclstrs: Mx1 vector with the selected cluster of each scan (0=none)


% Input checking
if ~exist('debug','var') || isempty(debug)
    debug=0;
end

if ~exist('manclstrs','var') || isempty(manclstrs)
    getperf=0;
else
    getperf=1;
end

if ~exist('manselen','var') || isempty(manselen)
    manselen=0;
end

% check iest
if isempty(tiest)
    iestflag=0;
else
    iestflag=1;
end

% check thresholds for his
if isfield(thresholds,'fthhi')
    checksus=1;
else
    checksus=0;
end

% get freq threshold
fthlo=thresholds.fthlo;
if checksus
    fthhi=thresholds.fthhi;
end

% get iest threshold
if iestflag
    iestthlo=thresholds.iestthlo;
    if checksus
        iestthhi=thresholds.iestthhi;
    end
end

lenth=thresholds.lenth;

% get nos
noscans=size(rangeMatrix,1);

% frequency scores
fscores=getpecscores2(angleVector,rangeMatrix,laserDivisor);
clstrfscores=getclstrscores(clstrs,fscores);
clstrfscoresth=clstrfscores;
clstrfscoresth(clstrfscoresth<fthlo)=0;



% get mask for scans with no camcalib
vscans=~isnan(Nci(1,:))';

noccmask=zeros(size(rangeMatrix));
noccmask(vscans,:)=1;
noccmask=getclstrscores(clstrs,noccmask); % change into cluster information

if iestflag
    
    % debug initial estimate
    initestdebug=0;
    if debug
        debin=input('Do you want to display the boards in the laser scans? (y/n,[]=n)','s');
        if debin=='y'
            initestdebug=1;
        end
    end
    
    % get iestscores
    iestscores=getinitestscore(tiest,riest,Nci,BoardCorners,angleVector,rangeMatrix,initestdebug);
    clstriestscores=getclstrscores(clstrs,iestscores);
    clstriestscoresth=clstriestscores;
    clstriestscoresth(clstriestscoresth<iestthlo)=0;
else
    clstriestscores=noccmask;
    clstriestscoresth=clstriestscores;
end

% get length scores
lengths=getclstrlengths(angleVector,rangeMatrix,clstrs);
clstrscoresi=clstrfscoresth.*clstriestscoresth;

[m,mi]=max(clstrscoresi.*logical(lengths),[],2); % do not choose zero length clusters
lind=sub2ind(size(lengths),(1:noscans)',mi);
lind(m==0)=[]; % remove scans with all zero scores
lenvec=lengths(lind); % get lengths
% choose the 99th percentile length (safer than the maximum in removing
% outliers)
lenvec=sort(lenvec);
np=0.99*length(lenvec);
lenbar=interp1(lenvec,np);

lengthscores=1-abs(lengths-lenbar)./lenbar;
lengthscores(lengthscores<0)=0; % clip negative scores

% threshold length scores
lengthscoresth=lengthscores;
lengthscoresth(lengthscoresth<lenth)=0;


% get final scores
clstrscores=clstrfscoresth.*clstriestscoresth.*lengthscoresth;
[maxscores,selclstrs]=max(clstrscores,[],2);

% vscans=max(noccmask,[],2);
sucscans=logical(maxscores);
si=sub2ind(size(lengths),(1:noscans)',selclstrs);
if checksus
    if iestflag
        susscans=(clstrfscores(si)<fthhi | clstriestscores(si)<iestthhi) & sucscans;
    else
        susscans=clstrfscores(si)<fthhi & sucscans;
    end
end
fscans=vscans&(~sucscans);

% just check for rotation for SelectLaserPoints2 (guard)
if ~exist('riest','var') || isempty(riest)
    riest=angvec2dcm([0;0;0]);
end

if checksus && ~isempty(find(susscans,1))
    disp('Please validate the selected board lines in the following scans:');
    for cntr=find(susscans')
        figure;
        img=GetImage(cntr);
        if ~isempty(img);
            subplot 121;
        end
        orientation=dcm2angvec(riest);
        orientation=orientation(3);
        dispclstrscore(angleVector+orientation,rangeMatrix(cntr,:),clstrs(cntr,:));
        title(['Scan: ',num2str(cntr)]);
        if ~isempty(img)
            subplot 122;
            imshow(img,[]);
        end
        fprintf('The automatically selected cluster is %d.',selclstrs(cntr));
        if isempty(img)
            fprintf('Image does not exist.');
        end
        fprintf('\n');
        mansel=input('If necessary, select a different one ([]=no change,0=none):');
        close;
        if ~isempty(mansel)
            selclstrs(cntr)=mansel;
            sucscans(cntr)=logical(mansel);
        end
    end
end


if ~isempty(find(fscans,1)) && manselen
    manselyn=input('\nWould you like to manually select the failed scans? (y/n,[]=n)','s');
    if manselyn=='y'
        f=figure;
        uisuspend(f);
        for cntr=find(fscans')
            fprintf('Select points from scan no %d.',cntr);
            img=GetImage(cntr);
            if ~isempty(img);
                maximize(f);
                subplot 122;
                imshow(img,[]);
                subplot 121;
            else
                fprintf('Image does not exist.');
            end
            fprintf('\n');
            orientation=dcm2angvec(riest);
            orientation=orientation(3);
            selind = SelectLaserPoints(angleVector+orientation,rangeMatrix(cntr,:));
            clf;
            if isempty(selind)
                continue;
            end
            if ~selind
                break;
            end
            clstrs(cntr,:)=selind+1;
            selclstrs(cntr)=2;
            sucscans(cntr)=1;
        end
        close(f);
    end
end

selclstrs=selclstrs.*sucscans;

%
% Debugging
%

debugscans=1:noscans;

if getperf
    figure;
    manclstrscores=getclstrscores(clstrs,manclstrs);
    % threshold
    manclstrscoresth=manclstrscores;
    manclstrscoresth(manclstrscoresth<1.5)=0;
    [benchscores,benchvec]=max(manclstrscoresth,[],2);
    benchvec=benchvec.*logical(benchscores); % remove zeros
    scannos=(1:noscans)';
    bind=sub2ind(size(manclstrscores),scannos(benchvec>0),benchvec(benchvec>0));
    manclstrscoresb=zeros(size(manclstrscores));
    manclstrscoresb(bind)=1;
    scatter3(clstrfscores(:),clstriestscores(:),lengthscores(:),25,manclstrscoresb(:),'filled');
    xlabel('Frequency','fontsize',14);
    ylabel('InitEst','fontsize',14);
    zlabel('Length','fontsize',14);
    set(gcf,'renderer','opengl');
%     trainvec=trainvec.*logical(trainscores);
    fprintf('\n');
    notruepos=nnz((benchvec==selclstrs).*vscans.*selclstrs)
    notrueneg=nnz((benchvec==selclstrs).*vscans.*(~selclstrs))
    falsepos=find((benchvec~=selclstrs).*vscans.*selclstrs)
    falseneg=find((benchvec~=selclstrs).*vscans.*(~selclstrs))
    noinvscans=nnz(~vscans)
    fprintf('\n');
    falsescans=find((benchvec~=selclstrs).*vscans);
    nofalsescans=nnz((benchvec~=selclstrs).*vscans)
    debugscans=falsescans';
end

if debug
    if ~getperf
        debin=input('Do you want to display all scans? (y/n,[]=n)','s');
    else
        debin='y';
    end
    if debin=='y'
        figure;
        for cntr=debugscans
            clf;
            dispclstrscore(angleVector,rangeMatrix(cntr,:),clstrs(cntr,:));
            title(num2str(cntr));
            disp(['Selected cluster is ',num2str(selclstrs(cntr)),'.']);
        end
    end
end