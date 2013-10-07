function [ fc, cc, alpha_c, kc, err, transformMat ] = CameraCalibration()
ima_read_calib();
add_suppress();
click_calib();
go_calib_optim();
ext_calib();

intrinsics.fc = fc;
intrinsics.cc = cc;
intrinsics.alpha_c = alpha_c;
intrinsics.kc = kc;
intrinsics.err = err_std;

transformMat = Rckk(1,1:2);
transformMat = [transformMat; Rckk(2,1:2)];
transformMat = [transformMat [0; 0]];
transformMat = [transformMat; [0 0 1]];
extrinsics.transformation_matrices = transformMat;
end

