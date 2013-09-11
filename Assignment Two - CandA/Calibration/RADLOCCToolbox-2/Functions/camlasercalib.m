function [delta,phi]=camlasercalib(Lpts,Nc,deltaest,phiest)
% CAMLASERCALIB performs the final laser-camera calibration step.
% 
% CAMLASERCALIB runs the final optimisation of the calibration process.
% 
% Transformation equation:
%     Lpts=phi*(Lptsc-delta)
%     where:
%         Lpts: is the coordinates of the laser points in the sensor frame.
% 
%         Lptsc: is the coordinates of the laser points in the camera
%         frame.
% 
%         delta: represent the translation offset between the two
%         coordinate frames. delta represents the position of the laser
%         range finder origin in the camera coordinate frame.
% 
%         phi: is the rotation matrix which, when applied, aligns the
%         camera coordinate frame to the sensor coordinate frame.
% 
% USAGE:
%     [delta,phi]=camlasercalib(Lpts,Nc,deltaest,phiest);
% 
% INPUTS:
%     Lpts: Calibration laser points.
% 
%     Nc: Normal vectors of laser points (in camera frame).
% 
%     deltaest: initial estimate of translation offest.
% 
%     phiest: initial estimate of rotaion matrix.
% 
% OUTPUTS:
%     delta: 3x1 translation offset vector.
% 
%     phi: 3x3 rotation matrix.


if ~exist('deltaest','var') || isempty(deltaest)
    deltaest=[0;0;0];
    phiest=angvec2dcm([0;0;0]);
end

% make sure delta is a col vec
deltaest=deltaest(:);

phideltaest0=[deltaest,rodrigues(phiest)];

%Last optimisation uses the Levenberg Marquardt method
options = optimset('LevenbergMarquardt','on','Display','off');
options = optimset(options, 'MaxFunEvals', 10000000);
% phideltaest0=phideltaest;
phideltaest= lsqnonlin(@(phideltaest)geterroropt(phideltaest, Lpts, Nc), phideltaest0,[],[],options);
delta = phideltaest(:,1);
phi = rodrigues(phideltaest(:,2));