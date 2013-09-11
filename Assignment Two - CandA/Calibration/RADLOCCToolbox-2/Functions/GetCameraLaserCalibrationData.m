function [laserPoints,cameraPlanes,scannos] = GetCameraLaserCalibrationData(selectionNumbers,rangeMatrix,angleVector,clstrs,boardclstrs,Nci,dflag)
% GETCAMERALASERCALIBRATIONDATA is an auxiliary function used to setup the data for calibration.
% 
% GETCAMERALASERCALIBRATIONDATA sets up the data for calibration and optionally
% displays the the calibration points all on one figure when dflag is
% passed as 1.
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     clstrs: MxN array. Each element in clstrs is an integer indicating
%     the line cluster the range to which reading belongs.
% 
%     boardclstrs: Mx1 vector with the selected cluster of each scan (0=none)
% 
%     Nci: 3xM array containing the normal vector of the
%     calibration plane of the corresponding laser scan.
% 
%     dflag: for debugging
% 
% OUTPUTS:
%     laserPoints: 3xN array containing all N points to be used for the
%     calibration process.
%     
%     cameraPlanes: 3xN array containing the corresponding normal vectors for the
%     points in Lpts.
%     
%     scannos: 3xN array containing the corresponding scan numbers of the
%     points in Lpts.
% 
% Abdallah Kassir 1/3/2010

if ~exist('dflag','var') || isempty(dflag)
    dflag=0;
end
cameraPlanes=[];
laserPoints=[];
scannos=[];
validscans=[];


% change to cartesian coordinates
[z,x]=pol2cart(repmat(angleVector,size(rangeMatrix,1),1),rangeMatrix);

%for each selection
for selection = selectionNumbers
    
    %Ask user to segment laser scan corresponding to plane
    ptsvec=[];
    if boardclstrs(selection)~=0
        ptsvec=find(clstrs(selection,:)==boardclstrs(selection));
    else
        continue;
    end
    
%     selectedRanges=laserRangeData(selection,ptsvec);
%     selectedAngles=laserAngleVector(ptsvec);


    newLaserPoints = [x(selection,ptsvec); zeros(size(ptsvec)); z(selection,ptsvec)];
    %For the current paired laser scan and camera plane, the one plane
    %describes all laser points - here the appropriate plane is copied N
    %times where N is the number of laser points
    [newCameraPlanes] = repmat(Nci(:,selection),1,size(newLaserPoints,2));
    cameraPlanes=[cameraPlanes,newCameraPlanes];
    laserPoints=[laserPoints,newLaserPoints];
    scannos=[scannos,repmat(selection,1,size(newLaserPoints,2))];
    validscans=[validscans,selection];
end

if dflag
    %Plot the selected laser scans from 'above' - the scans should all be
    %planar in the appropriate orientations
    figure;
    plot(laserPoints(1,:),laserPoints(3,:),'r.',0,0,'bo');axis equal;grid on
    legend('Laser Points','Laser Origin');
    title('Laser Points on Board Planes');
    xlabel('x');
    ylabel('z');
    % 
    % %Total amounts of data selected
    fprintf( 'Planes selected:');disp(validscans);
    fprintf( 'Total of %d planes selected, with a total of %d data points.\n', length(validscans),size(laserPoints,2));
    drawnow;
end
