function [ output_args ] = start_calib()
%% Start Intrisic Calibration
for i=1:7
    %Get a picture from the kinect
    %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, i);
end

%ima_read_calib();
%add_suppress();
%click_calib();
%go_calib_optim();

%% Start Colour Calibration

%Get a picture from the kinect
%[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, i);
im = imread('sample.jpg');

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
RGB_Yellow = impixel(squaresIm, round(squareLocations{2}(6, 1)), round(squareLocations{2}(6, 2)));
RGB_Silver = impixel(squaresIm, round(squareLocations{4}(3, 1)), round(squareLocations{4}(3, 2)));
YCbCr_Yellow = rgb2ycbcr(RGB_Yellow);
YCbCr_Silver = rgb2ycbcr(RGB_Silver);
end

