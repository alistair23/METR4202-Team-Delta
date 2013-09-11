function errorvec=geterroropt(deltaphi,Lpts,Nc)
% GETERROROPT is the optimisation objective function.
%
% GETERROROPT simply finds the error vector for a certain transformation.
% The function differs from geterrorvec in that it takes the transformation
% as a 1x6 vector with the rotation vector in rodrigues notation (Not Euler
% angles).
% 
% INPUTS:
%     deltaphi: 1x6 vector. First 3 are the translation offset. Last 3 are
%     the rodrigues vector.
%
%     Lpts: 3xN vector containing all the laser board points.
% 
%     Nc: 3xN vector containing the corresponding normal vector.
% 
% OUTPUTS:
%     errorvec: 1xN vector representing the errors for each point.

delta=deltaphi(:,1);

phi=rodrigues(deltaphi(:,2));

errorvec=geterrorvec(Lpts,Nc,delta,phi);

