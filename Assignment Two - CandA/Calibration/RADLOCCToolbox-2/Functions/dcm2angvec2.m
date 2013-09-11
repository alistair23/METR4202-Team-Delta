function rot=dcm2angvec2(phi)
% DCM2ANGVEC facilitates the use of the function dcm2angle.
%
% DCM2ANGVEC facilitates the use of the function dcm2angle.
% The function outputs the Euler angles of the rotation matrix phi.
% 
% CONVENTION:
%     Let Ri be the rotation matrix that rotates the 'axes' about axis i.
%     Then phi=Rz*Ry*Rx. See angvec2dcm for further details.
% 
% 
% USAGE:
%     rot=dcm2angvec(phi);
% INPUTS:
%     phi: 3x3 rotation matrix.
% 
% OUTPUTS:
%     rot: 3x1 Euler angles vector
%
% Abdallah Kassir 1/3/2010

% [rx,ry,rz]=dcm2angle(phi,'xyz'); % requires aerospace toolbox
% rot=[rx;ry;rz];

ry=asin(phi(3,1));
rx=asin(-phi(3,2)/cos(ry));
rz=asin(-phi(2,1)/cos(ry));
rot=[rx;ry;rz];
end

