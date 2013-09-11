% LASER_ONTO_IMAGE_CB is the callback file for Laser onto image feature.
% 
% LASER_ONTO_IMAGE_CB is called to reproject laser points from a laser
% range finder onto an image. This can be useful for the verification of
% the calibration results.
% 
% Calib_Results.mat file needs to present in order to retrieve the camera
% calibration parameters.

if ~exist('delta','var')
    disp('No Calibration Data.');
    return;
end
if ~exist('./Calib_Results.mat','file')
    disp('Calib_Results.mat is needed to proceed.');
    return;
end

% load camera calibration results.
load Calib_Results.mat fc cc kc alpha_c fc_error cc_error kc_error alpha_c_error;

% adjust errors, divide by 3 to get standard deviation (the camera
% calibraiton toolbox outputs 3 times the standard deviation as an
% uncertainty).
fc_error=fc_error./3;
cc_error=cc_error./3;
kc_error=kc_error./3;
alpha_c_error=alpha_c_error./3;

% select images
loino=input('Select image numbers to display ([]=none):');

% display all images
for cntr=loino
    im=GetImage(cntr);
    if isempty(im)
        disp('File does not exist.');
        return;
    else
        InsertLaserIntoImage( GetImage(cntr), angleVector, rangeMatrix(cntr,:), delta, phi, fc, cc, kc, alpha_c,deltae,rote,fc_error,cc_error,kc_error,alpha_c_error);
    end
end
