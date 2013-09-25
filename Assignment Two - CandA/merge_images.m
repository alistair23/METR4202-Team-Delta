function [ out tform ] = merge_images( I, J )
%MERGE_IMAGES Places two images side-by-side
%   Returns a new image that contains I and J placed side-by-side. I and J
%   must be of the same data-type and must both be r,g,b. If requested, the
%   second return variable will be the transformation from the frame of J
%   to I.
    
    % Get the height of the taller of the two images
    max_height = max(size(I, 1), size(J, 1));
    
    % Pad the end of image to the same (tallest) height with zeros
    I_pad = padarray(I, max_height - size(I, 1), 0, 'post');
    J_pad = padarray(J, max_height - size(J, 1), 0, 'post');
    
    % Concatenate images
    out = [I_pad J_pad];
    
    tform = eye(4);
    tform(1, 4) = size(I_pad, 2);

end

