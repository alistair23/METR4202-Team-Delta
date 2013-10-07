function main()
%% Start Colour Calibration
% %Get a picture from the kinect
% %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, 30);
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
% 
% RGB_Yellow = impixel(squaresIm, round(squareLocations{2}(6, 1)), round(squareLocations{2}(6, 2)));
% RGB_Silver = impixel(squaresIm, round(squareLocations{4}(3, 1)), round(squareLocations{4}(3, 2)));
% 
% YCbCr_Yellow = rgb2ycbcr(RGB_Yellow);
% YCbCr_Silver = rgb2ycbcr(RGB_Silver);
% 
% HSV_Yellow = rgb2hsv(RGB_Yellow);
% HSV_Silver = rgb2hsv(RGB_Silver);

%% Start Intrisic Calibration
% for i=1:7
%     %Get a picture from the kinect
%     %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, i);
% end
% 
% ima_read_calib();
% add_suppress();
% click_calib();
% go_calib_optim();
% ext_calib();
% 
% fprintf(1,'Extrinsic parameters:\n\n');
% fprintf(1,'Translation vector: Tc_ext = [ %3.6f \t %3.6f \t %3.6f ]\n',Tckk);
% fprintf(1,'Rotation vector:   omc_ext = [ %3.6f \t %3.6f \t %3.6f ]\n',omckk);
% fprintf(1,'Rotation matrix:    Rc_ext = [ %3.6f \t %3.6f \t %3.6f\n',Rckk(1,:)');
% fprintf(1,'                               %3.6f \t %3.6f \t %3.6f\n',Rckk(2,:)');
% fprintf(1,'                               %3.6f \t %3.6f \t %3.6f ]\n',Rckk(3,:)');

%% Capture the image of the scene
%Get a picture from the kinect
%[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, 40);
im = imread('Testing2.png');
im_d = imread('Testing2_d.png');

% Convert to grey scale
imgrey = rgb2gray(im);

%% Rectify the Image
% Select four control points as shown in the figure,
% then select File > Export Points to Workspace
%[input_points, output_points] = cpselect(imgrey, im_d, 'Wait', true);

% Use the selected points to create a recover the projective transform
%tform = cp2tform([241.719226260258,272.387456037515;334.755568581477,270.136576787808;235.716881594373,342.164712778429;340.757913247362,346.666471277843], [250.722743259086,262.633645955451;330.253810082063,265.634818288394;251.473036342321,332.410902696366;333.254982415006,337.662954279015], 'projective');
tform = cp2tform([242.469519343494,285.142438452521;441.297186400938,283.641852286049;452.551582649472,396.936107854631;235.716881594373,396.936107854631], [238.718053927315,288.143610785463;445.798944900352,289.644196951934;454.052168815944,388.682883939039;234.966588511137,390.933763188746], 'projective');
%tform = cp2tform(input_points, output_points, 'projective');

% Transform the grayscale image
Igft = imtransform(imgrey, tform, 'XYScale', 1);
Ift = imtransform(im, tform, 'XYScale', 1);
Idft = imtransform(im_d, tform, 'XYScale', 1);

%% Detect Circles
min_radius = 10;
max_radius = 35;

% Detect and show circles
%circles = houghcircles(Igft, min_radius, max_radius, 0.33, 12, 300, 800, 650, 800);
circles = houghcircles(Igft, min_radius, max_radius, 0.33, 12, 350, 500, 300, 450);

%% Determine the colour of each circle
for i=1:size(circles, 1)
    circles_RGB(i, :) = impixel(Ift, circles(i, 1), circles(i, 2));
    circles_hsv(i, :) = rgb2hsv(circles_RGB(i, 1:3));
    if circles_hsv(i, 1) > 0.05 && circles_hsv(i, 1) < 0.15
        if circles_hsv(i, 2) > 0.3 && circles_hsv(i, 2) < 0.6
                circles_colour(i) = 'S';
                continue;
        end
    end
    if circles_hsv(i, 1) > 0.09 && circles_hsv(i, 1) < 0.15
        if circles_hsv(i, 2) > 0.55 && circles_hsv(i, 2) < 0.93
                circles_colour(i) = 'G';
                continue;
        end
    end
    %Can't identify the coin colour
    circles_colour(i) = 'U';
end

%% Estimate the value of the money
num_coins = [0, 0, 0, 0, 0, 0]; %($2, $1, 50c, 20c, 10c, 5c)
total_value = 0;

for i=1:size(circles, 1)
    intensity = rgb2hsv(impixel(Idft, circles(i, 1), circles(i, 2)));
    diameter_of_coin(i) = circles(i, 3) * intensity(3);
    diameter_of_coin(i) = round(6.792 * exp(0.0006 * diameter_of_coin(i)));
    
    if diameter_of_coin(i) < 20 && circles_colour(i) == 'G'
        % $2
        num_coins(1) = num_coins(1) + 1;
        total_value = total_value + 2;
    elseif diameter_of_coin(i) > 19 && circles_colour(i) == 'G'
        % $1
        num_coins(2) = num_coins(2) + 1;
        total_value = total_value + 1;
    elseif diameter_of_coin(i) > 29 && circles_colour(i) == 'S'
        % 50c
        num_coins(3) = num_coins(3) + 1;
        total_value = total_value + 0.5;
    elseif diameter_of_coin(i) > 24 && diameter_of_coin(i) < 29 && circles_colour(i) == 'S'
        % 20c
        num_coins(4) = num_coins(4) + 1;
        total_value = total_value + 0.2;
    elseif diameter_of_coin(i) > 14 && diameter_of_coin(i) < 20 && circles_colour(i) == 'S'
        % 10c
        num_coins(5) = num_coins(5) + 1;
        total_value = total_value + 0.1;
    elseif diameter_of_coin(i) > 10 && diameter_of_coin(i) < 15 && circles_colour(i) == 'S'
        % 5c
        num_coins(6) = num_coins(6) + 1;
        total_value = total_value + 0.05;
    elseif circles_colour(i) == 'G'
        % Guess $1 as that seems to be common
        num_coins(2) = num_coins(2) + 1;
        total_value = total_value + 1;
    elseif circles_colour(i) == 'S'
        % Guess 50c as that seems to be common
        num_coins(3) = num_coins(3) + 1;
        total_value = total_value + 0.5;
    else
        % Just screwed!
        printf(1, '\nUnidentified Coin!\n');
    end
end
fprintf(1,'\nThe Total value of money is: %3.6f \n\n', total_value);

%% Start SIFT
% [f_im, d_im] = vl_sift(im_gray);

%% Check for $2 coin
% [CF_2, CF_2_f, CF_2_d] = sift_training('NoteCalibration/2_C_Front.jpg');
% [CB_2, CB_2_f, CB_2_d] = sift_training('NoteCalibration/2_C_Back.jpg');
% 
% %Front
% [matches, scores] = vl_ubcmatch(d_im, squeeze(CF_2_d), 1.8);
% [coin_input_points(1,:,:), coin_base_points(1,:,:)] = visualise_sift_matches(im, squeeze(CF_2), f_im, squeeze(CF_2_f), matches );
% 
% %Back
% [matches, scores] = vl_ubcmatch(d_im, squeeze(CB_2_d), 1.8);
% [coin_input_points(2,:,:), coin_base_points(2,:,:)] = visualise_sift_matches(im, squeeze(CB_2), f_im, squeeze(CB_2_f), matches );

end

