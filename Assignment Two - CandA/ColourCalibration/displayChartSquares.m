% Copyright 2007 The MathWorks, Inc.
function displayChartSquares(squaresIm,final)
% displayChartSquares Show the identified centroids on the original chart image
imshow(squaresIm)
hold on
for i = 1:4
	plot(final{i}(:,1), final{i}(:,2), 'o', 'markerEdgeColor', 'k', ...
            'markerFaceColor', 'w','markersize', 5, 'linewidth', 1);
end
title('Centroids of Squares')
