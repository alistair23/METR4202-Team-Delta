function [ RGB, HSV, lab ] = ColourCalibrationGrey( picture )
im = imread(picture);

[X, C] = CCFind(im);

RGB = impixel(im, X(21, 2), X(21, 1));

HSV = rgb2hsv(RGB);

lab = rgb2ycbcr(RGB);
end