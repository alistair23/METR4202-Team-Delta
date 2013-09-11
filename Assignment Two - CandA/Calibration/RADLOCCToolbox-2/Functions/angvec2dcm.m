function [phi,phix,phiy,phiz,dphix,dphiy,dphiz]=angvec2dcm(rot)
% ANGVEC2DCM gets the direction cosine matrix from the Euler angles.
%
% ANGVEC2DCM gets the direction cosine matrix from the Euler angles.
%
% CONVENTION:
%     Let Ri be the rotation matrix that rotates the 'axes' about axis i.
%     Then phi=Rz*Ry*Rx. See angvec2dcm for further details.
% 
% INPUTS:
%     rot: 3x1 vector of Euler angles.
% 
% OUTPUTS:
%     phi: 3x3 rotation matrix.
% 
%     phix: x rotation matrix.
% 
%     phiy: y rotation matrix.
% 
%     phiz: z rotation matrix.
% 
%     dphix: x differential rotation matrix.
% 
%     dphiy: y differential rotation matrix.
% 
%     dphiz: z differential rotation matrix.
% 
% Abdallah Kassir 1/3/2010

phix=[1,0,0;
      0,cos(rot(1)),sin(rot(1));
      0,-sin(rot(1)),cos(rot(1))];
phiy=[cos(rot(2)),0,-sin(rot(2));
      0,1,0;
      sin(rot(2)),0,cos(rot(2))];
phiz=[cos(rot(3)),sin(rot(3)),0;
      -sin(rot(3)),cos(rot(3)),0;
      0,0,1];

if nargout>1
    dphix=[0,0,0;
           0,-sin(rot(1)),cos(rot(1));
           0,-cos(rot(1)),-sin(rot(1))];
    dphiy=[-sin(rot(2)),0,-cos(rot(2));
           0,0,0;
           cos(rot(2)),0,-sin(rot(2))];
    dphiz=[-sin(rot(3)),cos(rot(3)),0;
           -cos(rot(3)),-sin(rot(3)),0;
           0,0,0];
end

% phi=angle2dcm(rot(1),rot(2),rot(3),'xyz'); % requires aerospace toolbox
phi=phiz*phiy*phix;
end