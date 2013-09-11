% LOAD_CB is the callback file that loads available information from files.
%
% LOAD_CB loads files previously saved in the current directory.

if exist('./LaserScans.mat','file')
    disp('Loading LaserScans.mat');
    load ./LaserScans.mat;
end
if exist('./CalibPoints.mat','file')
    disp('Loading CalibPoints.mat');
    load CalibPoints.mat;
end
if exist('./AutoClstrs.mat','file')
    disp('Loading AutoClstrs.mat');
    load ./AutoClstrs.mat;
end
if exist('./ManClstrs.mat','file')
    disp('Loading ManClstrs.mat');
    load ./ManClstrs.mat
end
if exist('./LaserCalibResults.mat','file');
    disp('Loading LaserCalibResults.mat');
    load ./LaserCalibResults.mat;
end