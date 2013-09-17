% Copyright 2007 The MathWorks, Inc.
function [chartRed chartGreen chartBlue] = getChartRGBValues(filename)
% This function gets the RGB values from the file.
% Originally written to get the RGB values for the color checker chart
% grayscale row (bottom row).

chartRed = xlsread(filename, 'Sheet1', 'A2:A7');
chartGreen = xlsread(filename, 'Sheet1', 'B2:B7');
chartBlue = xlsread(filename, 'Sheet1', 'C2:C7');