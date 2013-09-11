function [cameraPlanes,BoardCorners] = GetCameraPlanes(fname,noscans)
% GETCAMERAPLANES gets the calibration planes from the camera calibration results.
% 
% GETCAMERAPLANES retrieves the calibration planes from the camera
% calibration results in the form of normal vectors and chessboard corner
% coordinates.
% 
% USAGE:
%   [cameraPlanes] = GetCameraPlanes(fname,noscans)
%   After running the Camera Calibration Toolbox and saving Calib_Results.mat
%   This function will load that file (current directory assumed)
%   For every available Rc_n and Tc_n now in the workspace it will create a
%   normal vector N ~ 3x1 describing the plane.  Magnitude of vector is the
%   distance to the plane from the camera origin in metres
%
%   NOTE:   Magnitude describes distance to plane in METRES
%           Vector is from plane to camera origin
% 
% INPUTS:
%     fname: name of camera calibration file.
% 
%     noscans: the total number of laser scans.
% 
% OUTPUTS:
%     cameraPlanes: 3xnoscans array containing the normal vector of the
%     calibration plane of the corresponding laser scan.
% 
%     BoardCorners: is a 1xnoscans array of structures. Each structure has
%     the following elements:
% 
%         n_sq_x: number of squares of the calibration chessboard along the
%         x direction.
% 
%         n_sq_y: number of squares of the calibration chessboard along the
%         y direction.
% 
%         corners: 3x((n_sq_x+1)*(n_sq_y+1)) array with the coordinates of
%         the chessboard corners in the camera frame.
%
% Written by James Underwood 10/07/06
%
% Modified by Abdallah Kassir 1/3/2010


cameraPlanes=[];
load(fname);

stringRBase = 'Rc_';
stringTBase = 'Tc_';

base = 1;
%for n = selectionNumbers
while( exist([stringRBase,num2str(base)]) && exist([stringTBase,num2str(base)]) )
  
    rc = eval([stringRBase,num2str(base)]);
    tc = eval([stringTBase,num2str(base)]);
    
    plane = -rc(:,3) * dot(rc(:,3)', tc); % see cam/laser paper
    plane = -plane./1000; % in mm not m and from camera to plane not the other way around
    cameraPlanes=[cameraPlanes,plane];
    
    base = base + 1;
end

if isempty(cameraPlanes)
    error('No Rc_# or Tc_# variables found in Calib_Results.mat - check the camera calibration');
end

stringXBase = 'X_';
stringnsqxBase='n_sq_x_';
stringnsqyBase='n_sq_y_';

kk=1;
while exist([stringXBase,num2str(kk)],'var')
    rc = eval([stringRBase,num2str(kk)]);
    tc = eval([stringTBase,num2str(kk)]);
    x  = eval([stringXBase,num2str(kk)]);
    BoardCorners(kk).n_sq_x = eval([stringnsqxBase,num2str(kk)]);
    BoardCorners(kk).n_sq_y = eval([stringnsqyBase, num2str(kk)]);
    BoardCorners(kk).corners=(rc * x + tc * ones(1,size(x,2)))./1000; % in m
    kk=kk+1;
end

% fix sizes to noscans
if size(cameraPlanes,2)<noscans
    for cntr=size(cameraPlanes,2)+1:noscans
        cameraPlanes(:,cntr)=NaN;
        BoardCorners(cntr).n_sq_x=NaN;
        BoardCorners(cntr).n_sq_y=NaN;
        BoardCorners(cntr).corners=NaN;
    end
elseif size(cameraPlanes,2)>noscans
    % trim
    cameraPlanes=cameraPlanes(:,1:noscans);
    BoardCorners=BoardCorners(1:1:noscans);
end
