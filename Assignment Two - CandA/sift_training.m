function [ output_args ] = sift_training( input_string )
I = imread(input_string);
I = single(rgb2gray(I));
[f_I, d_I] = vl_sift(I);


J = imread('test_2.jpg');
J = single(rgb2gray(J));
[f_J, d_J] = vl_sift(J);

[matches, scores] = vl_ubcmatch(d_I, d_J) ;

[ input_points, base_points ] = visualise_sift_matches( I, J, f_I, f_J, matches )


% perm = randperm(size(f, 2));
% sel = perm(1:50);

% h1 = vl_plotframe(f(:,sel));
% h2 = vl_plotframe(f(:,sel));
% set(h1, 'color', 'k', 'linewidth', 3);
% set(h2, 'color', 'k', 'linewidth', 2);
end

