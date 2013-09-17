% Copyright 2007 The MathWorks, Inc.
function WPout = adjustWP(squareMeans,chartSquares)
% The function ADJUSTWP will find the white point that will balance the 
% input white value in squareMeans with the reference white value in
% chartSquares.

lab2xyz = makecform('lab2xyz');

% Pull out white values
reference = chartSquares{4,1};
measured = squareMeans{4,1};

% Convert to XYZ space
ref_xyz = applycform(reference,lab2xyz);
meas_xyz = applycform(measured,lab2xyz);

WP = abs(ref_xyz./meas_xyz);
WPout = WP(:);