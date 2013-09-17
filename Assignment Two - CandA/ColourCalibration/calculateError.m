% Copyright 2007 The MathWorks, Inc.
function rms = calculateError(squares1, squares2)
% This function calculates the error between actual and observed values
% from a color checker chart using the deltaE equation and then calculating
% an overall RMS error.  The inputs should be cell arrays that are 4-by-6
% and contain values in the Lab colorspace.


deltaE = @(in1,in2) sqrt(sum((in1-in2).^2));
deltaEO = cellfun(@(in1,in2) deltaE(in1,in2), squares1, squares2);
rms = sqrt(sum(sum(deltaEO.^2))/length(squares1));