% Intrinsic and Extrinsic Camera Parameters
%
% This script file can be directly excecuted under Matlab to recover the camera intrinsic and extrinsic parameters.
% IMPORTANT: This file contains neither the structure of the calibration objects nor the image coordinates of the calibration points.
%            All those complementary variables are saved in the complete matlab data file Calib_Results.mat.
% For more information regarding the calibration model visit http://www.vision.caltech.edu/bouguetj/calib_doc/


%-- Focal length:
fc = [ 656.729909635027750 ; 657.135160930409370 ];

%-- Principal point:
cc = [ 302.971317863060510 ; 243.566801860983190 ];

%-- Skew coefficient:
alpha_c = 0.000000000000000;

%-- Distortion coefficients:
kc = [ -0.256220988935246 ; 0.132149022132478 ; -0.000289166481323 ; 0.000032397893284 ; 0.000000000000000 ];

%-- Focal length uncertainty:
fc_error = [ 0.353999221050904 ; 0.380393358173194 ];

%-- Principal point uncertainty:
cc_error = [ 0.714883890724601 ; 0.654599432496228 ];

%-- Skew coefficient uncertainty:
alpha_c_error = 0.000000000000000;

%-- Distortion coefficients uncertainty:
kc_error = [ 0.002803812118638 ; 0.011426942898206 ; 0.000148198921773 ; 0.000147333570730 ; 0.000000000000000 ];

%-- Image size:
nx = 640;
ny = 480;


%-- Various other variables (may be ignored if you do not use the Matlab Calibration Toolbox):
%-- Those variables are used to control which intrinsic parameters should be optimized

n_ima = 20;						% Number of calibration images
est_fc = [ 1 ; 1 ];					% Estimation indicator of the two focal variables
est_aspect_ratio = 1;				% Estimation indicator of the aspect ratio fc(2)/fc(1)
center_optim = 1;					% Estimation indicator of the principal point
est_alpha = 0;						% Estimation indicator of the skew coefficient
est_dist = [ 1 ; 1 ; 1 ; 1 ; 0 ];	% Estimation indicator of the distortion coefficients


%-- Extrinsic parameters:
%-- The rotation (omc_kk) and the translation (Tc_kk) vectors for every calibration image and their uncertainties

%-- Image #1:
omc_1 = [ -7.919905e-001 ; 5.891928e-001 ; 1.453960e+000 ];
Tc_1  = [ 1.795877e+002 ; -6.604197e+001 ; 8.932561e+002 ];
omc_error_1 = [ 1.273471e-003 ; 1.229094e-003 ; 6.123355e-004 ];
Tc_error_1  = [ 9.731394e-001 ; 8.999780e-001 ; 4.837645e-001 ];

%-- Image #2:
omc_2 = [ -5.131713e-001 ; 3.242306e-001 ; 1.554716e+000 ];
Tc_2  = [ 2.023953e+002 ; -1.400762e+002 ; 7.954136e+002 ];
omc_error_2 = [ 1.424056e-003 ; 1.360310e-003 ; 4.228895e-004 ];
Tc_error_2  = [ 8.701605e-001 ; 8.035621e-001 ; 4.888907e-001 ];

%-- Image #3:
omc_3 = [ -4.374104e-001 ; 4.125089e-001 ; 1.700209e+000 ];
Tc_3  = [ 2.296202e+002 ; -1.159383e+002 ; 7.640230e+002 ];
omc_error_3 = [ 1.455389e-003 ; 1.409073e-003 ; 4.160391e-004 ];
Tc_error_3  = [ 8.356247e-001 ; 7.728077e-001 ; 5.046977e-001 ];

%-- Image #4:
omc_4 = [ -1.555188e-001 ; 8.453742e-001 ; 1.624473e+000 ];
Tc_4  = [ 2.601936e+002 ; -1.572415e+002 ; 6.227101e+002 ];
omc_error_4 = [ 1.311502e-003 ; 1.249687e-003 ; 4.707747e-004 ];
Tc_error_4  = [ 6.857945e-001 ; 6.427403e-001 ; 5.081308e-001 ];

%-- Image #5:
omc_5 = [ -1.107290e+000 ; 2.525214e-001 ; 1.930879e+000 ];
Tc_5  = [ 1.836656e+002 ; -1.423354e+001 ; 8.186299e+002 ];
omc_error_5 = [ 1.304839e-003 ; 1.251030e-003 ; 7.185983e-004 ];
Tc_error_5  = [ 8.964550e-001 ; 8.176592e-001 ; 4.349496e-001 ];

%-- Image #6:
omc_6 = [ 5.142355e-001 ; -6.610449e-001 ; 1.608919e+000 ];
Tc_6  = [ 2.072603e+002 ; -5.308103e+001 ; 4.894862e+002 ];
omc_error_6 = [ 1.220525e-003 ; 1.248737e-003 ; 5.329548e-004 ];
Tc_error_6  = [ 5.359791e-001 ; 5.028404e-001 ; 4.125611e-001 ];

%-- Image #7:
omc_7 = [ -7.892616e-002 ; -9.692626e-001 ; 1.427574e+000 ];
Tc_7  = [ 2.003046e+002 ; -1.534543e+002 ; 6.487295e+002 ];
omc_error_7 = [ 1.165527e-003 ; 1.192999e-003 ; 5.764377e-004 ];
Tc_error_7  = [ 7.079008e-001 ; 6.576610e-001 ; 4.594245e-001 ];

%-- Image #8:
omc_8 = [ -1.722180e-001 ; -1.003898e+000 ; 1.379775e+000 ];
Tc_8  = [ 9.233322e+001 ; -1.937328e+002 ; 6.909754e+002 ];
omc_error_8 = [ 1.160537e-003 ; 1.167393e-003 ; 5.390021e-004 ];
Tc_error_8  = [ 7.573111e-001 ; 6.919347e-001 ; 4.399336e-001 ];

%-- Image #9:
omc_9 = [ 8.378402e-001 ; 2.950196e-001 ; 1.828596e+000 ];
Tc_9  = [ 2.568347e+002 ; -7.665554e+001 ; 5.268105e+002 ];
omc_error_9 = [ 1.459590e-003 ; 1.280206e-003 ; 5.397312e-004 ];
Tc_error_9  = [ 6.089906e-001 ; 5.597024e-001 ; 5.680761e-001 ];

%-- Image #10:
omc_10 = [ 6.472856e-001 ; 1.637423e-001 ; 1.827765e+000 ];
Tc_10  = [ 2.712530e+002 ; -1.732524e+002 ; 7.087108e+002 ];
omc_error_10 = [ 1.755578e-003 ; 1.618545e-003 ; 4.488878e-004 ];
Tc_error_10  = [ 8.174204e-001 ; 7.423453e-001 ; 7.484631e-001 ];

%-- Image #11:
omc_11 = [ 4.151426e-001 ; -3.892692e-001 ; 1.671372e+000 ];
Tc_11  = [ 2.056282e+002 ; -1.876928e+002 ; 7.103334e+002 ];
omc_error_11 = [ 1.580762e-003 ; 1.604730e-003 ; 4.005392e-004 ];
Tc_error_11  = [ 7.956902e-001 ; 7.439635e-001 ; 6.129136e-001 ];

%-- Image #12:
omc_12 = [ 3.547478e-001 ; -4.110213e-001 ; 1.660842e+000 ];
Tc_12  = [ 2.236668e+002 ; -1.384292e+002 ; 6.266713e+002 ];
omc_error_12 = [ 1.438950e-003 ; 1.456459e-003 ; 3.862142e-004 ];
Tc_error_12  = [ 6.950799e-001 ; 6.515819e-001 ; 5.304100e-001 ];

%-- Image #13:
omc_13 = [ 2.535253e-001 ; -4.598266e-001 ; 1.635112e+000 ];
Tc_13  = [ 2.227719e+002 ; -1.215015e+002 ; 5.965092e+002 ];
omc_error_13 = [ 1.377140e-003 ; 1.391134e-003 ; 3.733036e-004 ];
Tc_error_13  = [ 6.570897e-001 ; 6.157464e-001 ; 4.892765e-001 ];

%-- Image #14:
omc_14 = [ 2.221855e-001 ; -4.476351e-001 ; 1.624032e+000 ];
Tc_14  = [ 2.317075e+002 ; -1.200927e+002 ; 5.455801e+002 ];
omc_error_14 = [ 1.333785e-003 ; 1.346173e-003 ; 3.581107e-004 ];
Tc_error_14  = [ 6.013092e-001 ; 5.663797e-001 ; 4.615915e-001 ];

%-- Image #15:
omc_15 = [ 1.673686e-002 ; -3.568385e-001 ; 1.621002e+000 ];
Tc_15  = [ 1.519348e+002 ; -1.214338e+002 ; 5.528732e+002 ];
omc_error_15 = [ 1.370889e-003 ; 1.379986e-003 ; 2.400232e-004 ];
Tc_error_15  = [ 6.065489e-001 ; 5.598634e-001 ; 4.048816e-001 ];

%-- Image #16:
omc_16 = [ -1.525520e-001 ; 1.307696e-001 ; 1.777563e+000 ];
Tc_16  = [ 3.360911e+002 ; -9.585858e+001 ; 6.927143e+002 ];
omc_error_16 = [ 1.772250e-003 ; 1.814731e-003 ; 2.319725e-004 ];
Tc_error_16  = [ 7.593847e-001 ; 7.223693e-001 ; 6.028774e-001 ];

%-- Image #17:
omc_17 = [ 6.676613e-001 ; -3.020697e-001 ; 1.693245e+000 ];
Tc_17  = [ 2.099974e+002 ; -5.453954e+001 ; 4.326275e+002 ];
omc_error_17 = [ 1.284675e-003 ; 1.270185e-003 ; 4.856763e-004 ];
Tc_error_17  = [ 4.795194e-001 ; 4.533578e-001 ; 4.195951e-001 ];

%-- Image #18:
omc_18 = [ 1.097049e+000 ; -2.874409e-001 ; 1.632555e+000 ];
Tc_18  = [ 1.253539e+002 ; -2.324941e+001 ; 3.188495e+002 ];
omc_error_18 = [ 1.234451e-003 ; 1.140129e-003 ; 6.753095e-004 ];
Tc_error_18  = [ 3.535408e-001 ; 3.296666e-001 ; 3.313556e-001 ];

%-- Image #19:
omc_19 = [ 1.314479e-001 ; -1.086395e+000 ; 1.384966e+000 ];
Tc_19  = [ 1.903352e+002 ; -1.734039e+002 ; 5.145299e+002 ];
omc_error_19 = [ 1.092004e-003 ; 1.145107e-003 ; 6.095462e-004 ];
Tc_error_19  = [ 5.672401e-001 ; 5.272734e-001 ; 4.017548e-001 ];

%-- Image #20:
omc_20 = [ -2.942200e-001 ; -1.149947e+000 ; 1.244088e+000 ];
Tc_20  = [ 6.643973e+001 ; -2.202996e+002 ; 6.564769e+002 ];
omc_error_20 = [ 1.097545e-003 ; 1.108216e-003 ; 6.088328e-004 ];
Tc_error_20  = [ 7.231942e-001 ; 6.557614e-001 ; 4.197790e-001 ];

