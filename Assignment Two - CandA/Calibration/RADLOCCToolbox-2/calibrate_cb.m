% CALIBRATE_CB is the calibration callback file.
%
% CALIBRATE_CB is the calibration callback file for the GUI. It calls the
% final calibration function CAMLASERCALIB.


if ~exist('boardclstrs','var') || isempty(find(boardclstrs,1))
    disp('No Extracted Board Points.');
    return;
end
if ~exist('./Calib_Results.mat','file')
    disp('Calib_Results.mat is needed to proceed.');
    return;
end


if ~exist('selectionnumbers','var')
    selectionnumbers=input('Enter the scan numbers to be used for calibration ([]=all):');
    if isempty(selectionnumbers)
        selectionnumbers=1:size(rangeMatrix,1);
    end
end
disp('Laser-Camera Calibration.');
% adjust selection numbers
selectionnumbers(boardclstrs(selectionnumbers)==0)=[];
[Lpts,Nc,Lptsnos] = GetCameraLaserCalibrationData(selectionnumbers,rangeMatrix,angleVector,clstrs,boardclstrs,Nci);
% disp('Running optimsations. Please wait.'); % no need, fast
[delta,phi] = camlasercalib(Lpts,Nc,deltaest,phiest);
rmserror=geterror(Lpts,Nc,delta,phi);
[deltae,rote]=getuncert(Lpts,Lptsnos,Nc);
disp('Results:');
disp(['Delta:',mat2str(delta',3),'±',mat2str(deltae',3)]);
disp(['Phi (in degrees):',mat2str(rad2deg(dcm2angvec(phi))',3),'±',mat2str(rad2deg(rote)',3)]);
disp(['Total rms error:',num2str(rmserror,3)]);

% update initial estimate
deltaest=delta;
phiest=phi;