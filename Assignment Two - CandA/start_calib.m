function [ output_args ] = start_calib()

for i=1:7
    %Get a picture from the kinect
    %[photo(:,:,:,i), depth(:,:,:,i)] = capture_image(false, true, i);
end

ima_read_calib();
add_suppress();
click_calib();
go_calib_optim();

end

