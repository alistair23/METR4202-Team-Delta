% Copyright 2007 The MathWorks, Inc.
%% Load in test image and select chart
im = imread('sample.jpg');
[squaresIm,cropRect] = imcrop(im);
close; subplot(2,2,1); imshow(squaresIm); title('Selected Region')

% Convert chart image to black and white
gray = rgb2gray(squaresIm);     % Convert to grayscale
J = histeq(gray);               % Equalize the histogram
threshold = graythresh(J);      % Threshold
bw = im2bw(J, threshold);       % Convert to B&W
subplot(2,2,2); imshow(bw); title('Thresholded Region');

% Remove white pixels along the border, then dilate and erode to fill in
% solids.
bw2 = imclearborder(bw);
se = strel('square', 25);   
bw2 = imopen(bw2, se);
subplot(2,2,3); imshow(bw2); title('Noise Removed');

%% Find all chart squares
% Automatically find the centroid of all unique objects in the image.
labeled = bwlabel(bw2);
s = regionprops(labeled,'Centroid');
centroids = cat(1, s.Centroid);

% Use custom algorithm to find missing squares on the chart.
squareLocations = findAllChartSquares(centroids, squaresIm);
subplot(2,2,4); displayChartSquares(squaresIm,squareLocations);shg


%% Compare with reference color chart
% Now that we've identified the color chart in our photograph, let's
% compare it to the reference color chart.  We can use datatips to get a
% quantitative reading of the RGB values.
squareMeans = getMeanForEachSquare(squaresIm, squareLocations);
chartSquares = getReferenceValues('chartValues.xls');

% Calculate the difference between the original image and reference values
RMS = calculateError(squareMeans, chartSquares);
figure; displayError(squareMeans, chartSquares, RMS);

figure;
subplot(2,1,1);imshow('colorChart.tif');title('Reference Chart')
subplot(2,1,2);imshow(squaresIm);title(['Original Image, RMS=',num2str(RMS)])

%% Reducing the error with a white point adjustment
% Adjust the whitepoint to reduce the error in the image.  This should
% show a little bit of improvement.

WP = adjustWP(squareMeans,chartSquares);

srgb2xyz = makecform('srgb2xyz');
xyz2srgb = makecform('xyz2srgb');

XYZin = applycform(squaresIm, srgb2xyz);
XYZout(:,:,1) = WP(1)* XYZin(:,:,1);    % scale the X plane
XYZout(:,:,2) = WP(2)* XYZin(:,:,2);    % scale the Y plane
XYZout(:,:,3) = WP(3)* XYZin(:,:,3);    % scale the Z plane

wp_squaresIm = im2uint8(applycform(XYZout, xyz2srgb));

wpSquareMeans = getMeanForEachSquare(wp_squaresIm, squareLocations);
wpRMS = calculateError(wpSquareMeans, chartSquares);
figure; displayError(wpSquareMeans, chartSquares, wpRMS);

%% Interactively fit a curve to the grayscale row of the chart
% Get the observed values for the grayscale row in RGB
[observedRed observedGreen observedBlue] = getObservedChannels(squareMeans);
[chartRed chartGreen chartBlue] = getChartRGBValues('grayRGB.xls');

% Create new curves
[redCurve greenCurve blueCurve] = createCurves(observedRed, observedGreen, observedBlue, ...
                                               chartRed, chartGreen, chartBlue);

figure;subplot(3,1,1); plot(redCurve,'color','r'); title('Red Channel Curve');
subplot(3,1,2); plot(greenCurve,'color','g'); title('Green Channel Curve');
subplot(3,1,3); plot(blueCurve,'color','b'); title('Blue Channel Curve');

%% apply curves to image
adjustedIm(:,:,1) = intlut(squaresIm(:,:,1), redCurve);
adjustedIm(:,:,2) = intlut(squaresIm(:,:,2), greenCurve);
adjustedIm(:,:,3) = intlut(squaresIm(:,:,3), blueCurve);

% Calculate error
adjustedSquareMeans = getMeanForEachSquare(adjustedIm, squareLocations);
adjustedRMS = calculateError(adjustedSquareMeans, chartSquares);

% Compare all results
figure;subplot(2,2,1);imshow('colorChart.tif');title('Reference Chart')
subplot(2,2,2);imshow(squaresIm);title(['Original Image, RMS=',num2str(RMS)])
subplot(2,2,3);imshow(wp_squaresIm);title(['Adjusted Whitepoint, RMS=',num2str(wpRMS)])
subplot(2,2,4);imshow(adjustedIm);title(['Custom Curve, RMS=',num2str(adjustedRMS)])

% Create display
figure; displayError(adjustedSquareMeans, chartSquares, adjustedRMS);
                   

