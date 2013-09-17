% Copyright 2007 The MathWorks, Inc.
function final = findAllChartSquares(centroids, squaresIm)
% findAllChartSquares

% Please define inputs and outputs!

xDiffs = diff(centroids(:,1));

%sort the y values of the centroids and calculate the difference
sortedChartY = sort(centroids(:,2));
yDiffs = diff(sortedChartY);

tol = 10;
chartY = find(yDiffs > tol);
chartX = find(xDiffs > tol);

start = 1;
row = zeros(length(chartY),1);
for i = 1:length(chartY)
	row(i,1) = mean(sortedChartY(start:chartY(i)));
	start = chartY(i)+1;
end
row(4,1) = mean(sortedChartY(start:end));

%find the x-value for the centroid for each colum of the chart
start = 1;
col = zeros(1,length(chartX));
for i = 1:length(chartX)
	col(1,i) = mean(centroids(start:chartX(i), 1));
	start = chartX(i)+1;
end
col(1,6) = mean(centroids(start:end, 1));

%putting it all together, the final centroid values
final = cell(1,4);
for i = 1:4
	final{:,i} = [col' repmat(row(i,:), 6,1)];
end