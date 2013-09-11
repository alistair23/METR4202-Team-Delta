% SAVE_CB is the callback file that saves all the data available.
%
% SAVE_CB saves the data available, so to save time on subsequent usages of
% the program or to save the calibration results.

if exist('rangeMatrix','var')
    disp('Saving to LaserScans.mat');
    save('LaserScans.mat','rangeMatrix','angleVector','laserDivisor');
end

if exist('clstrs','var')
    disp('Saving to CalibPoints.mat');
    save('CalibPoints.mat','clstrs','boardclstrs','deltaest','phiest','Nci','BoardCorners');
end

if exist('clstrsf','var')
    disp('Saving to AutoClstrs.mat');
    save('AutoClstrs.mat','roughth','clstrsr','fineth','clstrsf');
end

if exist('manclstrs','var')
    disp('Saving to ManClstrs.mat');
    save('ManClstrs.mat','manclstrs');
end

if exist('delta','var')
    disp('Saving to LaserCalibResults.mat');
    save('LaserCalibResults.mat','Lpts','Nc','Lptsnos','delta','phi','deltae','rote','selectionnumbers');
end