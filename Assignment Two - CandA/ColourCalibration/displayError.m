% Copyright 2007 The MathWorks, Inc.
function displayError(squareMeans, chartSquares, rms)
% This function will display the error between the actual color checker
% chart values and observed values.'
% 
% squareMeans - the mean observed Lab value for each square, 4-by-6 cell array
% chartSquares - the actual Lab values for each square, 4-by-6 cell array
% squares - the observed RGB value for each square, 4-by-6 cell array
% rms - the RMS error obtained by comparing the observed chart with the
% actual chart values.

showDifference(squareMeans, chartSquares);
title(['Modified image colors and chart colors. RMS error: '...
    num2str(rms)], 'FontWeight', 'Bold')
xlabel('a* (Magenta-Green)', 'FontWeight', 'Bold'); ylabel('b* (Yellow-Blue)', 'FontWeight', 'Bold')
zlabel('L (Brightness)', 'FontWeight', 'Bold')
view([-114 12])
axis vis3d
axisBackground = [235/256 235/256 235/256];
set(gca, 'color',axisBackground )