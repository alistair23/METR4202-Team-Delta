function main()
% %% Start Colour Calibration
% 
% %Get a picture from the kinect
% %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, 1);
% %im = imread('ColourPhoto.png');
% im = imread('sample.jpg');
% 
% %Crop the region of the colourchecker
% [squaresIm, cropRect] = imcrop(im);
% close;
% 
% % Convert chart image to black and white
% gray = rgb2gray(squaresIm);     % Convert to grayscale
% J = histeq(gray);               % Equalize the histogram
% threshold = graythresh(J);      % Threshold
% bw = im2bw(J, threshold);       % Convert to B&W
% imshow(bw); title('Noise Removed');
% 
% % Remove white pixels along the border, then dilate and erode to fill in
% % solids.
% bw2 = imclearborder(bw);
% se = strel('square', 25);   
% bw2 = imopen(bw2, se);
% 
% % Automatically find the centroid of all unique objects in the image.
% labeled = bwlabel(bw2);
% s = regionprops(labeled,'Centroid');
% centroids = cat(1, s.Centroid);
% 
% % Use custom algorithm to find missing squares on the chart.
% squareLocations = findAllChartSquares(centroids, squaresIm);
% RGB_Yellow = impixel(squaresIm, round(squareLocations{2}(6, 1)), round(squareLocations{2}(6, 2)));
% RGB_Silver = impixel(squaresIm, round(squareLocations{4}(3, 1)), round(squareLocations{4}(3, 2)));
% YCbCr_Yellow = rgb2ycbcr(RGB_Yellow);
% YCbCr_Silver = rgb2ycbcr(RGB_Silver);
% HSV_Yellow = rgb2hsv(RGB_Yellow);
% HSV_Silver = rgb2hsv(RGB_Silver);

%% Start Intrisic Calibration
for i=1:7
    %Get a picture from the kinect
    %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, i);
end

ima_read_calib();
add_suppress();
click_calib();
go_calib_optim();
ext_calib();

fprintf(1,'\n\nExtrinsic parameters:\n\n');
fprintf(1,'Translation vector: Tc_ext = [ %3.6f \t %3.6f \t %3.6f ]\n',Tckk);
fprintf(1,'Rotation vector:   omc_ext = [ %3.6f \t %3.6f \t %3.6f ]\n',omckk);
fprintf(1,'Rotation matrix:    Rc_ext = [ %3.6f \t %3.6f \t %3.6f\n',Rckk(1,:)');
fprintf(1,'                               %3.6f \t %3.6f \t %3.6f\n',Rckk(2,:)');
fprintf(1,'                               %3.6f \t %3.6f \t %3.6f ]\n',Rckk(3,:)');

%% Object segmentaion
%Get a picture from the kinect
%[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, 2);
im = imread('CoinPhoto.png');

% Convert to grey scale
imgrey = rgb2gray(im);

% Get a gaussian kernel for blurring
K = fspecial('gaussian');

% Blur the image
imgf = imfilter(imgrey, K);
imgf = imfilter(imgf, K);
imgf = imfilter(imgf, K);

% Detect edges
E = edge(imgrey, 'canny');

% Perform Hough Line transform
[H, T, R] = hough(E);

% Get top N line candidates from hough accumulator
N = 10;
P = houghpeaks(H, N);

% Get hough line parameters
lines = houghlines(imgf, T, R, P);

% Select four control points as shown in the figure,
% then select File > Export Points to Workspace
%cpselect(imgrey, checkerboard);

% Use the selected points to create a recover the projective transform
%tform = cp2tform(input_points, base_points, 'projective');
transformMat = Rckk(1,1:2);
transformMat = [transformMat; Rckk(2,1:2)];
transformMat = [transformMat [0; 0]];
transformMat = [transformMat; [0 0 1]];
tform = affine2d(transformMat);

% Transform the grayscale image
Igft = imwarp(imgrey, tform);

min_radius = 15;
max_radius = 20;

% Detect and show circles
houghcircles(Igft, min_radius, max_radius);
end

