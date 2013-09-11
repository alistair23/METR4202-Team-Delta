% MANUAL_SELECT_CB is the callback file that starts the manual selection process.
%
% MANUAL_SELECT_CB sets up the manual selection process by which the user
% is asked to manually select the calibration board in each laser scan. If
% the calibration images exist in the same folder, they are displayed next
% to each image.
% 
% The user can skip a scan by pressing enter. Pressing the letter 'e' will
% interrupt the process.
% Abdallah Kassir 1/3/2010

% Make sure data is available

if ~exist('./Calib_Results.mat','file')
    disp('Calib_Results.mat is needed to proceed.');
    return;
end
if ~exist('rangeMatrix','var')
    disp('Range data needed, run Read data or Load.');
    return;
end

disp('Manual Selection.');

% Get intial estimates for the laser-camera transformation

if ~exist('deltaest','var')
    deltaest=[0;0;0];
end
deltaestin=input(['Enter initial estimate for translation vector ([]=',mat2str(deltaest',3),'):']);
if ~isempty(deltaestin)
    deltaest=deltaestin(:);  % col vector
end

if ~exist('phiest','var')
    phiest=angvec2dcm([0;0;0]);
end
phiestin=input(['Enter initial estimate for rotation vector ([]=',mat2str(rad2deg(dcm2angvec(phiest))',3),'):']);
if ~isempty(phiestin)
    % change into radians and rotation matrix
    phiest=angvec2dcm(deg2rad(phiestin(:))); % col vector
end

% get the planes from the Camera calibration results.
[Nci,BoardCorners]=GetCameraPlanes('Calib_Results.mat',size(rangeMatrix,1));


% reset clusters, boardclusters and selectionnumbers

clstrs=zeros(size(rangeMatrix));
boardclstrs=zeros(size(rangeMatrix,1),1);
selectionnumbers=1:size(rangeMatrix,1);

% adjust selection numbers to remove invalid scans
vscans=~isnan(Nci(1,:))';
selectionnumbers(selectionnumbers>length(vscans))=[];
selectionnumbers(~vscans(selectionnumbers))=[];

disp('Press Enter to skip or ''e'' to interrupt process.');
f=figure;
uisuspend(f); % suspend other features
for cntr=selectionnumbers
    fprintf('Select points from scan no %d.',cntr);
    img=GetImage(cntr);
    if ~isempty(img);
        maximize(f);
        subplot 122;
        imshow(img,[]);
        subplot 121;
    else
        fprintf(' Image does not exist.');
    end
    fprintf('\n');
    % adjust display orientation for user convenience
    orientation=dcm2angvec(phiest);
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
    boardclstrs(cntr)=2;
end
close(f);

manclstrs=clstrs;

% adjust selectionnumbers to remove user skipped ones
selectionnumbers(~boardclstrs(selectionnumbers))=[];

% display points
dispboardpts(angleVector,rangeMatrix,clstrs,boardclstrs,selectionnumbers);
