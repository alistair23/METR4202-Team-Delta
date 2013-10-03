function [ I, f_I, d_I ] = sift_training( input_string )
I = imread(input_string);
I = single(rgb2gray(I));

% Get a gaussian kernel for blurring
K = fspecial('gaussian');

% Blur the image
I = imfilter(I, K);
I = imfilter(I, K);
I = imfilter(I, K);

[f_I, d_I] = vl_sift(I);
end