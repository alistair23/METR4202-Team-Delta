function [ input_points, base_points ] = visualise_sift_matches( I, J, f_I, f_J, matches )
%VISUALISE_SIFT_MATCHES Visualises the SIFT feature matches between images
%   Plots image I and J side by side, then draws red lines showing the SIFT
%   matches from one image to the other. A blue line showing the average
%   vector will also be drawn. Returns a set of input and base points that
%   can be used to derive the transform from I to J using cp2tform.
    
    [merged, tform] = merge_images(I, J);
    
    imshow(vl_imsc(merged));
    hold on;
    
    av_i_pt = [0; 0];
    av_j_pt = [0; 0];
    
    input_points = zeros(size(matches, 2), 2);
    base_points = zeros(size(matches, 2), 2);
    
    for i=1:size(matches, 2)
        pt_I = f_I(1:2, matches(1, i));
        pt_J = f_J(1:2, matches(2, i));
        
        input_points(i, :) = pt_I;
        base_points(i, :) = pt_J;
        
        av_i_pt = av_i_pt + pt_I;
        
        pt_J = tform * [pt_J; 0; 1];
        
        av_j_pt = av_j_pt + pt_J(1:2);
        
        plot([pt_I(1) pt_J(1)], [pt_I(2) pt_J(2)], 'r-', 'LineWidth', 1);
        
    end
    
    av_i_pt = av_i_pt ./ size(matches, 2);
    av_j_pt = av_j_pt ./ size(matches, 2);
    
    plot([av_i_pt(1) av_j_pt(1)], [av_i_pt(2) av_j_pt(2)], 'b-', 'LineWidth', 3);

end

