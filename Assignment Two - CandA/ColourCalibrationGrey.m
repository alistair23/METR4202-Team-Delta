function [ RGB, HSV, lab ] = ColourCalibrationGrey( picture )
im = imread(picture);

%Crop the region of the colourchecker
[squaresIm, cropRect] = imcrop(im);
close;

% Convert chart image to black and white
gray = rgb2gray(squaresIm);     % Convert to grayscale
J = histeq(gray);               % Equalize the histogram
threshold = graythresh(J);      % Threshold
bw = im2bw(J, threshold);       % Convert to B&W
imshow(bw); title('Noise Removed');

% Remove white pixels along the border, then dilate and erode to fill in
% solids.
bw2 = imclearborder(bw);
se = strel('square', 25);
bw2 = imopen(bw2, se);

% Automatically find the centroid of all unique objects in the image.
labeled = bwlabel(bw2);
s = regionprops(labeled,'Centroid');
centroids = cat(1, s.Centroid);

% Use custom algorithm to find missing squares on the chart.
squareLocations = findAllChartSquares(centroids, squaresIm);
RGB = impixel(squaresIm, round(squareLocations{4}(3, 1)), round(squareLocations{4}(3, 2)));
HSV = rgb2hsv(RGB);
lab = rgb2ycbcr(RGB);
end