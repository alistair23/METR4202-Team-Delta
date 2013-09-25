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

%% Generate the sift detectors
% [NF(), NF_f(1), NF_d(1)] = sift_training('NoteCalibration/hundred_front.jpg');
% [NB(1), NB_f(1), NB_d(1)] = sift_training('NoteCalibration/hundred_back.jpg');

[NF(2,:,:), NF_f(2,:,:), NF_d(2,:,:)] = sift_training('NoteCalibration/fifty_front.jpg');
[NB(2,:,:), NB_f(2,:,:), NB_d(2,:,:)] = sift_training('NoteCalibration/fifty_back.jpg');

% [NF(3), NF_f(3), NF_d(3)] = sift_training('NoteCalibration/twenty_front.jpg');
% [NB(3), NB_f(3), NB_d(3)] = sift_training('NoteCalibration/twenty_back.jpg');
% 
% [NF(4), NF_f(4), NF_d(4)] = sift_training('NoteCalibration/ten_front.jpg');
% [NB(4), NB_f(4), NB_d(4)] = sift_training('NoteCalibration/ten_back.jpg');
% 
% [NF(5), NF_f(5), NF_d(5)] = sift_training('NoteCalibration/five_front.jpg');
% [NB(5), NB_f(5), NB_d(5)] = sift_training('NoteCalibration/five_back.jpg');
% 
% [CF(1), CF_f(1), CF_d(1)] = sift_training('NoteCalibration/two_front.jpg');
% [CB(1), CB_f(1), CB_d(1)] = sift_training('NoteCalibration/two_back.jpg');
% 
% [CF(2), CF_f(2), CF_d(2)] = sift_training('NoteCalibration/one_front.jpg');
% [CB(2), CB_f(2), CB_d(2)] = sift_training('NoteCalibration/one_back.jpg');
% 
% [CF(3), CF_f(3), CF_d(3)] = sift_training('NoteCalibration/fiftycents_front.jpg');
% [CB(3), CB_f(3), CB_d(3)] = sift_training('NoteCalibration/fiftycents_back.jpg');
% 
% [CF(4), CF_f(4), CF_d(4)] = sift_training('NoteCalibration/twentycents_front.jpg');
% [CB(4), CB_f(4), CB_d(4)] = sift_training('NoteCalibration/twentycents_back.jpg');
% 
% [CF(5), CF_f(5), CF_d(5)] = sift_training('NoteCalibration/tencents_front.jpg');
% [CB(5), CB_f(5), CB_d(5)] = sift_training('NoteCalibration/tencents_back.jpg');
% 
% [CF(6), CF_f(6), CF_d(6)] = sift_training('NoteCalibration/fivecents_front.jpg');
% [CB(6), CB_f(6), CB_d(6)] = sift_training('NoteCalibration/fivecents_back.jpg');

%% Capture the image of the scene
%Get a picture from the kinect
%[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, 2);
im = imread('test_2.jpg');
im = single(rgb2gray(im));
[f_im, d_im] = vl_sift(im);

%% Check every note against the image
i = 2;
%for i=1:5
    [matches, scores] = vl_ubcmatch(d_im, squeeze(NF_d(i,:,:)), 1.8);
    [input_points(i,:,:), base_points(i,:,:)] = visualise_sift_matches( im, squeeze(NF(i,:,:)), f_im, squeeze(NF_f(i,:,:)), matches );
%end

i = 2;
%for i=1:5
    [matches, scores] = vl_ubcmatch(d_im, squeeze(NB_d(i,:,:)), 1.8);
    [input_points(i,:,:), base_points(i,:,:)] = visualise_sift_matches( im, squeeze(NB(i,:,:)), f_im, squeeze(NB_f(i,:,:)), matches );
%end


%%

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

