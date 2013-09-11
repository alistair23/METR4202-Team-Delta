% AUTO_SELECT_CB is the callback file for the autoselection process.
%
% AUTO_SELECT_CB extracts the calibration board lines from the laser scans.
% AUTO_SELECT_CB can also attempt to guess the initial estimate of the
% calibration parameters.
% 
% ASSUMPTIONS:
%     AUTO_SELECT_CB assumes that most of the laser scans contain the
%     calibration board. It also assumes that the calibration board is
%     moved around throughout the scans. If these assumptions are not
%     valid, please use the Manual select method.
% 
% Abdallah Kassir 1/3/2010


if ~exist('./Calib_Results.mat','file')
    disp('Calib_Results.mat is needed to proceed.');
    return;
end
if ~exist('rangeMatrix','var')
    disp('Range data needed, run Read data or Load.');
    return;
end

disp('Automatic board selection.');

%% get line clstrs
disp('Line extraction:');
getclstrsrf=0;
if ~exist('roughth','var')
    roughth=0.05; % default
end
roughthin=input(['Enter rough line threshold (in m) ([]=',num2str(roughth),'):']);
if ~isempty(roughthin)
    if roughth~=roughthin
        getclstrsrf=1;
    end
    roughth=roughthin;
end

getclstrsff=0;
if ~exist('fineth','var')
    fineth=0.02; % default
end
finethin=input(['Enter fine line threshold (in m) ([]=',num2str(fineth),'):']);
if ~isempty(finethin)
    if fineth~=finethin
        getclstrsff=1;
    end
    fineth=finethin;
end

if ~exist('clstrsr','var') || getclstrsrf
    disp('Extracting rough lines:');
    clstrsr=getedgelineclstrs(angleVector,rangeMatrix,roughth);
end

if ~exist('clstrsf','var') || getclstrsff
    disp('Extracting fine lines:');
    clstrsf=getedgelineclstrs(angleVector,rangeMatrix,fineth);
end

%% stage 1 Get initial estimate
disp('Initial estimate:');
autoinitest=input('Do you want the program to automatically find the initial estimate? (y/n, []=y)','s');

% get planes from camera calibration data
[Nci,BoardCorners]=GetCameraPlanes('Calib_Results.mat',size(rangeMatrix,1));


if autoinitest=='n'
    % Manual input of initial estimate
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
else
    % Automatic guess of initial estimate
    clear thresholds;
    % set thresholds
    thresholds.fthlo=0.9;
    thresholds.lenth=0.5;
    boardclstrs=findlaserboardpoints(angleVector,rangeMatrix,laserDivisor,clstrsr,[],[],Nci,BoardCorners,thresholds);
    [Lpts,Nc,Lptsnos] = GetCameraLaserCalibrationData(find(boardclstrs)',rangeMatrix,angleVector,clstrsr,boardclstrs,Nci);
    [deltaest,phiest] = getinitest(Lpts, Nc);
    rmserror=geterror(Lpts,Nc,deltaest,phiest);
    disp(['Initial estimate: delta:',mat2str(deltaest',3),', phi:',mat2str(rad2deg(dcm2angvec(phiest))',3),', rms error:',num2str(rmserror,3)]);
end



%% stage 2 optimise transformation
disp('Optimising estimate:');
boardclstrs=zeros(size(rangeMatrix,1),1);

for cntr=1:10
    boardclstrspre=boardclstrs;
    clear thresholds;
    thresholds.fthlo=0.8;
    thresholds.iestthlo=0.8;
    thresholds.lenth=0.5;

    boardclstrs=findlaserboardpoints(angleVector,rangeMatrix,laserDivisor,clstrsr,deltaest,phiest,Nci,BoardCorners,thresholds);
    [Lpts,Nc,Lptsnos] = GetCameraLaserCalibrationData(find(boardclstrs)',rangeMatrix,angleVector,clstrsr,boardclstrs,Nci);
    [deltaest,phiest] = getinitest(Lpts, Nc,deltaest,phiest);
    rmserror=geterror(Lpts,Nc,deltaest,phiest);
    disp(['Initial estimate: delta:',mat2str(deltaest',3),', phi:',mat2str(rad2deg(dcm2angvec(phiest))',3),', rms error:',num2str(rmserror,3)]);
    
    if isempty(find(boardclstrs~=boardclstrspre,1))
        break;
    end
end

%% stage 3 get final board points
disp('Final Stage:');
clear thresholds;
thresholds.fthlo=0.8;
thresholds.fthhi=0.9;
thresholds.iestthlo=0.8;
thresholds.iestthhi=0.9;
thresholds.lenth=0.5;
disp('Automatic board detection:');
% clstrs needs to be output since it adjusted by the manual selection
% inside findlaserboardpoints
[boardclstrs,clstrs]=findlaserboardpoints(angleVector,rangeMatrix,laserDivisor,clstrsf,deltaest,phiest,Nci,BoardCorners,thresholds,1);
selectionnumbers=find(boardclstrs)'; % set selection nmubers for calib
disp('Automatic board detection done.');


% display results
dispboardpts(angleVector,rangeMatrix,clstrs,boardclstrs,selectionnumbers);

