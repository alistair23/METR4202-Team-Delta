function [ RGB, HSV, lab ] = ColourCalibrationGold( picture )
im = imread(picture);

[X, C] = CCFind(im);

RGB = impixel(im, X(12, 2), X(12, 1));

HSV = rgb2hsv(RGB);

lab = rgb2ycbcr(RGB);
end