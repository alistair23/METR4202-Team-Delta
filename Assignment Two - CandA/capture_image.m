% This Code is property of Aaron METR4202
%
% [photo, depth] = capture_image(show);
% Captures a single, photo and depth image from an attached Kinect
% If the parameter show is set to true, then the images will also be displayed
%
% NB: You need to set the internal path variable to point to the KinectMatlab
% folder. 
%
% NB: The returned depth map is on the range [0...9000]
% To display these images, for example, you could use
% 
% figure;
% subplot(1,2,1), h1 = imshow(photo); 
% subplot(1,2,2), h2 = imshow(depth, [0 9000]); colormap('jet');
function [photo, depth] = caputre_image(show, save_images)
    % Path to the KinectMatlab toolbox. No trailing slash
    PATH_TO_KINECT_MATLAB = 'C:\Users\Alistair\Documents\MATLAB\Kinect_Matlab_version2\OpenNI1';
    MEX_PATH = fullfile(PATH_TO_KINECT_MATLAB, 'Mex');
    CONFIG_PATH = fullfile('ConfigAllNodes.xml');

    % Conect to the Kinect
    addpath(MEX_PATH);
    ctx = mxNiCreateContext(CONFIG_PATH);

    % Set up a cleaup routine to destroy the kinect context
    cleaner = onCleanup(@() mxNiDeleteContext(ctx));

    % Grab IR image
    %J = mxNiInfrared(ctx);
    %ir = permute(J, [2 1]);

    % Adjust depth coordinates to match color image
    mxNiChangeDepthViewPoint(ctx);
    option.adjust_view_point = true;
    mxNiUpdateContext(ctx, option);

    % Grab depth image
    J = mxNiDepth(ctx);
    depth = flipdim(permute(J, [2 1]), 2);

    % Grab color image
    J = mxNiPhoto(ctx);
    photo = flipdim(permute(J, [3 2 1]), 2);

    % Optionally show the images
    if(nargin() > 0 && show == true)
        figure;
        subplot(1,2,1), h1 = imshow(photo); 
        subplot(1,2,2), h2 = imshow(depth, [0 9000]); colormap('jet');
    end

    if(nargin() > 1 && save_images == true)
        disp('Saving Images...');
        filename = fullfile(pwd(), [datestr(clock, 'yyyy-mm-dd_HH-MM-SS')])
        
        % Save .mat file
        save([filename '_rgbd.mat'], 'photo', 'depth');
        
        % Save pngs
        imwrite(photo, [filename '_rgb.png']);
        imwrite(depth, [filename '_d.png']);
    end

    disp('Done!');
end