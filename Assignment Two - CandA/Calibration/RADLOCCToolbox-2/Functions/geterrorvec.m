function errorvec=geterrorvec(Lpts,Nc,delta,phi)
% GETERRORVEC returns the error for each point.
%
% GETERRORVEC contains the main error calculation function. The function is
% called by other function to analyse the error.
% 
% USAGE:
% 
% INPUTS:
%     Lpts: 3xN vector containing all the laser board points.
% 
%     Nc: 3xN vector containing the corresponding normal vector.
% 
%     delta: 3x1 translation vector output by the calibration.
% 
%     phi: 3x3 rotation matrix output by the calibration.
% 
% OUTPUTS:
%     errorvec: 1xN vector representing the errors for each point.


delta=delta(:); % make sure delta is a column vector
npts=size(Lpts,2);

% main error equation
errorvec=dot(Nc,inv(phi)*Lpts+repmat(delta,[1,npts]))./sqrt(sum(Nc.^2))-sqrt(sum(Nc.^2));