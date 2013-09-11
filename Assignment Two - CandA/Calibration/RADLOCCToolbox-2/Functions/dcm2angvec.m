function rot=dcm2angvec(phi)
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

if phi(3,1)~=1 && phi(3,1)~=-1
    ry=asin(phi(3,1));
    rz=atan2(-phi(2,1)/cos(ry),phi(1,1)/cos(ry));
    rx=atan2(-phi(3,2)/cos(ry),phi(3,3)/cos(ry));
else
    rz=0;
    ry=asin(phi(3,1));
    if phi(3,1)==1
        rx=atan2(phi(1,2)/phi(3,1),-phi(1,3)/phi(3,1));
    elseif phi(3,1)==-1
        rx=-atan2(phi(1,2)/phi(3,1),-phi(1,3)/phi(3,1));
    end
end
rot=[rx;ry;rz];
end

