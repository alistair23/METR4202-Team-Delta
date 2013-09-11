function [deltaest,phiest] = getinitest(Pf, N,deltaest,phiest)
% GETINITEST gets an initial estimate for the final step of the laser-camera calibration optimisation.
% 
% GETINITEST gets an initial estimate of the laser-camera extrinsic
% parameters. The two inputs deltaest and phiest are optional.
% 
% Transformation equation:
%     Lpts=phi*(Lptsc-delta)
%     where:
%         Lpts: is the coordinates of the laser points in the sensor frame.
%         The first row is x, the second y and the last row is z. x is
%         pointing to the  right of the laser, y downwards and z outwards.
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
%     [deltaest,phies]=getinitest(Lpts,Nc);
% 
%     [deltaest,phies]=getinitest(Lpts,Nc,deltaest,phiest);
% 
% INPUTS:
%     Lpts: Calibration laser points.
% 
%     Nc: Normal vectors of laser points (in camera frame).
% 
%     deltaest: rough initial estimate of translation offest (optional).
% 
%     phiest: rough initial estimate of rotaion matrix (optional).
% 
% Fabio Tozeto Ramos 05/10/05 
% modified by Abdallah Kassir 15/1/2010

% optimisation on linear parameters
% input rotation vector is a vector representing Euler angles.
% The convention used is the 'xyz' convention
% Frame convention is the one used in the matlab camera calibration toolbox
% the offset is the location of the laser's origin in the camera's frame of
% references.

if exist('deltaest','var')
    % make sure offset is a column vectors
    deltaest=deltaest(:);
else
    deltaest=[0;0;0];
    phiest=angvec2dcm([0;0;0]);
end
% 
% % change to radians
% rotation=deg2rad(rotation);

phiestinv=inv(phiest);

% modified by Abdallah Kassir 15/1/10
H0 = [phiestinv(:,1),phiestinv(:,3),deltaest];

% turn off warning
warning('off','optim:fmincon:NLPAlgLargeScaleConflict');
Pfhat = [Pf(1,:); Pf(3,:); ones(1,length(Pf))];
options = optimset('LargeScale','on','Display','off');
options = optimset(options, 'MaxFunEvals', 10000000);
options = optimset(options, 'MaxIter', 1000);
H = lsqnonlin(@(H)calibFun0(H, Pfhat, N), H0,[],[],options);

% extract delta and phi from H
phiest=[H(:,1), cross(-H(:,1),H(:,2)), H(:,2)]'; % inverse is transpose
deltaest=H(:,3); % delta positive

% makes sure phi is a valid rotation matrix
options = optimset('LargeScale','off','Display','off');
phiest0=phiest;
phiest = fmincon(@(phiest)frobenius_norm(phiest,phiest0),phiest0,[],[],[],[],[],[],@constraint_phi,options);