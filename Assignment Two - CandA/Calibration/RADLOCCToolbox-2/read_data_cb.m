% READ_DATA_CB is the main file that reads the raw laser data.
%
% READ_DATA_CB is the main file that reads the raw laser data.
% This file is called by the GUI.
%
% Abdallah Kassir 1/3/2010


% check for file names
if ~exist('laserdatafname','var') || ~exist('videoframetimefname','var')
    % get file names
    laserdatafname=input('Enter Laser Data File Name:','s');
    videoframetimefname=input('Enter Video Timestamp File Name:','s');
else
    laserdatafnamein=input(['Enter Laser Data File Name ([]=',laserdatafname,'):'],'s');
    if ~isempty(laserdatafnamein)
        laserdatafname=laserdatafnamein;
    end
    videoframetimefnamein=input(['Enter Video Timestamp File Name ([]=',videoframetimefname,'):'],'s');
    if ~isempty(videoframetimefnamein)
        videoframetimefname=videoframetimefnamein;
    end
end


% load files
if ~exist(laserdatafname,'file') || ~exist(videoframetimefname,'file')
    disp('File does not exist.');
    return;
end
fprintf('Loading Data. Please Wait...');
rawlaserdata=load(laserdatafname);
videotimestamps=load(videoframetimefname);
videotimestamps=videotimestamps(:,1);
disp('Done.');

% get laser data closest to video timestamps
[lasertimestamps,imindices] = GetClosestTimeStamps(videotimestamps, rawlaserdata(:,1) );
laserdata = rawlaserdata(imindices,:);

% get range matrix and angle vector
[rangeMatrix, angleVector, laserDivisor] = QBuildLaserRangeAngle(laserdata);

rawrangeMatrix=QBuildLaserRangeAngle(rawlaserdata);