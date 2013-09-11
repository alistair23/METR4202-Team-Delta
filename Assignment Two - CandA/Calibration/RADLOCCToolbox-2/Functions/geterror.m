function [rmserror,errorvec1,errorvec2]=geterror(Lpts,Nc,delta,phi,Lptsnos)
% GETERROR returns the error of the calibration results.
%
% GETERROR returns the error of the transformation with the data in three
% different formats.
% 
% USAGE:
%     rmserror=geterror(Lpts,Nc,delta,phi,Lptsnos);
% 
%     [rmserror,errorvec]=geterror(Lpts,Nc,delta,phi,Lptsnos);
% 
%     [rmserror,errorvec1,errorvec2]=geterror(Lpts,Nc,delta,phi,Lptsnos);
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
%     Lptsnos: 3xN vector containing the scan numbers of the laser points.
% 
%
% OUTPUTS:
%     rmserror: root mean squares of errors.
% 
%     errorvec1: 1xN vector representing the errors for each point.
% 
%     errorvec2: 1xM vector representing the RMS error for each scan, where
%     M is the maximum value in Lptsnos.

errorvec1=geterrorvec(Lpts,Nc,delta,phi);

rmserror=sqrt(sum(errorvec1.^2)/length(errorvec1));

if exist('Lptsnos','var') && ~isempty(Lptsnos)
    uLptsnos=unique(Lptsnos);
    errorvec2=zeros(size(uLptsnos));
    for cntr=unique(Lptsnos)
        errorvec2(cntr)=sqrt(sum(errorvec1(Lptsnos==cntr).^2)/nnz(Lptsnos==cntr));
    end
end

