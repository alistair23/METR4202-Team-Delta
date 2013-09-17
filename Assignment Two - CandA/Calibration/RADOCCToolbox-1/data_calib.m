%%% This script alets the user enter the name of the images (base name, numbering scheme,...

      
% Checks that there are some images in the directory:

l_png = dir('*png');
s_png = size(l_png,1);

s_tot = s_png;

if s_tot < 1,
   fprintf(1,'No image in this directory in either ras, bmp, tif, pgm, ppm, png or jpg format. Change directory and try again.\n');
   break;
end;

Nima_valid = 0;

while (Nima_valid==0),

   %fprintf(1,'\n');
   %calib_name = input('Basename camera calibration images (without number nor suffix): ','s');
   calib_name = 'Photo';
   
   format_image = 'png';
      
   check_directory;
   
end;

if (Nima_valid~=0),
    % Reading images:
    ima_read_calib; % may be launched from the toolbox itself
    % Show all the calibration images:    
end;

