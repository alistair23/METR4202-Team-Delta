% Copyright 2007 The MathWorks, Inc.
function [observedRed observedGreen observedBlue] = getObservedChannels(squareMeans)
grayRow = squareMeans(4,:);
cform2 = makecform('lab2srgb');
grayRowRGB = cellfun(@(input,c) applycform(lab2uint8(input), cform2), grayRow, 'UniformOutput', false);

RGB = [];
for i = 1:length(grayRowRGB)
	RGB = [RGB; reshape(grayRowRGB{i}, 1,3,1)]; %#ok
end
%%
% *Separate the values needed to create the curves*
% These are the observed values from the image
observedRed = double(flipud(RGB(:,1)));
observedGreen = double(flipud(RGB(:,2)));
observedBlue = double(flipud(RGB(:,3)));