function [ I, f_I, d_I ] = sift_training( input_string )
I = imread(input_string);
I = single(rgb2gray(I));
[f_I, d_I] = vl_sift(I);
end