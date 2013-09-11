function image=GetImage(imageno)
% GETIMAGE returns the image of a certain laser scan if it exists.
%
% GETIMAGE uses the camera calibration results to get the image of a
% certain laser scan. It returns an empty variable if the image does not
% exist.
% 
% USAGE:
%     image=GetImage(imageno);
% 
% INPUTS:
%     imageno: number of laser scan/image
% 
% OUTPUTS:
%     image: image of the laser scan.
% 
% Abdallah Kassir 1/3/2010

load Calib_Results.mat type_numbering calib_name N_slots format_image;

if ~type_numbering,   
    number_ext =  num2str(imageno);
else
    number_ext = sprintf(['%.' num2str(N_slots) 'd'],imageno);
end;

ima_name = [calib_name  number_ext '.' format_image];

if exist(ima_name,'file')
    if strcmp(format_image,'pgm')
        image = im2double(loadpgm(ima_name));
    elseif strcmp(format_image,'ppm')
        image = im2double(loadppm(ima_name));
    elseif strcmp(format_image,'ras')
        image = readras(ima_name);
    else
        image = im2double(imread(ima_name));
    end
else
    image=[];
end
