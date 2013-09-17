% Copyright 2007 The MathWorks, Inc.
function squares = getSquares(image,final)
pixRegion = 5;
squares = cell(4,6);
for i = 1:4
    for j = 1:6
        squares{i,j} = image(round(final{i}(j,2))-...
                    pixRegion:round(final{i}(j,2)+pixRegion), ...
                    round(final{i}(j,1)-pixRegion):round(final{i}(j,1)+pixRegion), :);
    end
end