% Copyright 2007 The MathWorks, Inc.
function squareMeans = getMeanForEachSquare (squaresIm, final)
% Get the new values of the squares 
% convert finding the chart to a function and call it here to get the new,
% updated squares
pixRegion = 5;
squares = cell(4,6);
for i = 1:4
    for j = 1:6
        squares{i,j} = squaresIm(round(final{i}(j,2))-...
                    pixRegion:round(final{i}(j,2)+pixRegion), ...
                    round(final{i}(j,1)-pixRegion):round(final{i}(j,1)+pixRegion), :);
    end
end

cform = makecform('srgb2lab');
square = cellfun(@(x,c) applycform(x, cform), squares, 'UniformOutput',...
    false);
squareMeans = cellfun(@(x) mean(mean(lab2double(x))), square,...
    'UniformOutput', false);
