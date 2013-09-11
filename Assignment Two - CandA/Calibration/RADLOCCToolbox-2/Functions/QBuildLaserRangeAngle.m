function   [rangeMatrix, angleVector, laserDivisor] = QBuildLaserRangeAngle( laserLogData )
%   USAGE:
%       [rangeMatrix, angleVector] = QBuildLaserRangeAngle( laserLogData )
%       Loads a generic range bearing ascii log line (logged with Q library)
%       and produces a very generic range and angle format for the data
%
%   INPUT:
%       laserLogData - variable containing laser range data in Q generic rb format
%           size is R rows by C columns, R is the number of scans
%           format:
%           <timestamp> <initial angle> <angle increment> <final angle> <range type int>
%           <nscans> <range1> <range2> ... <range N>
%           all in the row
%
%   OUTPUT:
%       rangeMatrix - variable containing laser range data in rows
%           size is R rows by C columns, R is the number of scans and C is the points per scan
%               eg a typical SICK laser scan might have [39,181] if there were
%               39 scans and the mode was 180 degrees, 1 degree resolution
%           NOTE: The ranges are in METRES
%
%       angleVector - variable containing a single row of angles in radians corresponding
%           to all of the laserRangeData matrix
%           If laserRangeData is R by C, laserAngleVector must be 1 by C
%           NOTE:   The angles are in RADIANS
%                   0 angle points forwards out of the laser, positive angles
%                   are clockwise from top down
%
% Written by James Underwood 10/07/06
%

laserUnitsType = laserLogData(1,5);
if laserUnitsType == 0
    error( 'LaserUnitsType 0 is invalid' );
elseif laserUnitsType == 1 % mm
    laserDivisor = 1000;
elseif laserUnitsType == 2 % cm
    laserDivisor = 100;
elseif laserUnitsType == 3 % m
    laserDivisor = 1;
elseif laserUnitsType == 4 % km
    laserDivisor = 1e-3;
else
    error( 'LaserUnitsType not supported - supported values are [1-mm, 2-cm, 3-m, 4-km]' )
end

%angleVector = linspace( laserLogData(1,2), laserLogData(1,2)+laserLogData(1,3)*(laserLogData(1,5)-1), laserLogData(1,5) );
angleVector = linspace( laserLogData(1,2), laserLogData(1,4), laserLogData(1,6) );
rangeMatrix = laserLogData(:,7:end) ./ laserDivisor;