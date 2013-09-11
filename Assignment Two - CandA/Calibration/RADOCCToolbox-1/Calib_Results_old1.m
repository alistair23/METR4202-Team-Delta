% Intrinsic and Extrinsic Camera Parameters
%
% This script file can be directly excecuted under Matlab to recover the camera intrinsic and extrinsic parameters.
% IMPORTANT: This file contains neither the structure of the calibration objects nor the image coordinates of the calibration points.
%            All those complementary variables are saved in the complete matlab data file Calib_Results.mat.
% For more information regarding the calibration model visit http://www.vision.caltech.edu/bouguetj/calib_doc/


%-- Focal length:
fc = [ 657.441441986058860 ; 657.822097611584240 ];

%-- Principal point:
cc = [ 302.955744761465380 ; 242.792195230546920 ];

%-- Skew coefficient:
alpha_c = 0.000000000000000;

%-- Distortion coefficients:
kc = [ -0.256980502104812 ; 0.134603934513618 ; -0.000210742172505 ; -0.000013470942263 ; 0.000000000000000 ];

%-- Focal length uncertainty:
fc_error = [ 0.350851651051606 ; 0.377067008488671 ];

%-- Principal point uncertainty:
cc_error = [ 0.708663068878942 ; 0.648508985746119 ];

%-- Skew coefficient uncertainty:
alpha_c_error = 0.000000000000000;

%-- Distortion coefficients uncertainty:
kc_error = [ 0.002776899207065 ; 0.011348515092699 ; 0.000146631785397 ; 0.000145841869001 ; 0.000000000000000 ];

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
omc_1 = [ -7.925019e-001 ; 5.895213e-001 ; 1.453934e+000 ];
Tc_1  = [ 1.796143e+002 ; -6.509156e+001 ; 8.940399e+002 ];
omc_error_1 = [ 1.260788e-003 ; 1.216329e-003 ; 6.060616e-004 ];
Tc_error_1  = [ 9.644473e-001 ; 8.914760e-001 ; 4.791749e-001 ];

%-- Image #2:
omc_2 = [ -5.137825e-001 ; 3.247383e-001 ; 1.554653e+000 ];
Tc_2  = [ 2.024082e+002 ; -1.391312e+002 ; 7.962306e+002 ];
omc_error_2 = [ 1.410036e-003 ; 1.346284e-003 ; 4.184758e-004 ];
Tc_error_2  = [ 8.624574e-001 ; 7.961090e-001 ; 4.841270e-001 ];

%-- Image #3:
omc_3 = [ -4.378958e-001 ; 4.133455e-001 ; 1.700198e+000 ];
Tc_3  = [ 2.296289e+002 ; -1.150134e+002 ; 7.647948e+002 ];
omc_error_3 = [ 1.441088e-003 ; 1.394457e-003 ; 4.118302e-004 ];
Tc_error_3  = [ 8.282302e-001 ; 7.656255e-001 ; 4.998115e-001 ];

%-- Image #4:
omc_4 = [ -1.559134e-001 ; 8.462497e-001 ; 1.624197e+000 ];
Tc_4  = [ 2.601901e+002 ; -1.564622e+002 ; 6.234029e+002 ];
omc_error_4 = [ 1.298545e-003 ; 1.236498e-003 ; 4.661736e-004 ];
Tc_error_4  = [ 6.797345e-001 ; 6.367975e-001 ; 5.030995e-001 ];

%-- Image #5:
omc_5 = [ -1.107542e+000 ; 2.536450e-001 ; 1.930912e+000 ];
Tc_5  = [ 1.837118e+002 ; -1.338274e+001 ; 8.192400e+002 ];
omc_error_5 = [ 1.292039e-003 ; 1.238317e-003 ; 7.112047e-004 ];
Tc_error_5  = [ 8.883404e-001 ; 8.098551e-001 ; 4.309525e-001 ];

%-- Image #6:
omc_6 = [ 5.140120e-001 ; -6.602563e-001 ; 1.608927e+000 ];
Tc_6  = [ 2.072233e+002 ; -5.243680e+001 ; 4.901670e+002 ];
omc_error_6 = [ 1.208281e-003 ; 1.235979e-003 ; 5.277707e-004 ];
Tc_error_6  = [ 5.314355e-001 ; 4.982812e-001 ; 4.086563e-001 ];

%-- Image #7:
omc_7 = [ -7.913624e-002 ; -9.690287e-001 ; 1.427694e+000 ];
Tc_7  = [ 2.003095e+002 ; -1.527010e+002 ; 6.494266e+002 ];
omc_error_7 = [ 1.153731e-003 ; 1.180920e-003 ; 5.704938e-004 ];
Tc_error_7  = [ 7.016361e-001 ; 6.515529e-001 ; 4.549559e-001 ];

%-- Image #8:
omc_8 = [ -1.726431e-001 ; -1.003472e+000 ; 1.380007e+000 ];
Tc_8  = [ 9.233336e+001 ; -1.929197e+002 ; 6.918207e+002 ];
omc_error_8 = [ 1.148712e-003 ; 1.155440e-003 ; 5.333144e-004 ];
Tc_error_8  = [ 7.506799e-001 ; 6.855869e-001 ; 4.356998e-001 ];

%-- Image #9:
omc_9 = [ 8.375306e-001 ; 2.961028e-001 ; 1.828282e+000 ];
Tc_9  = [ 2.567869e+002 ; -7.596958e+001 ; 5.276402e+002 ];
omc_error_9 = [ 1.444373e-003 ; 1.266409e-003 ; 5.345598e-004 ];
Tc_error_9  = [ 6.037485e-001 ; 5.546801e-001 ; 5.627096e-001 ];

%-- Image #10:
omc_10 = [ 6.473220e-001 ; 1.646397e-001 ; 1.827566e+000 ];
Tc_10  = [ 2.712136e+002 ; -1.723223e+002 ; 7.096021e+002 ];
omc_error_10 = [ 1.735658e-003 ; 1.600092e-003 ; 4.445517e-004 ];
Tc_error_10  = [ 8.101538e-001 ; 7.354723e-001 ; 7.410026e-001 ];

%-- Image #11:
omc_11 = [ 4.151142e-001 ; -3.886837e-001 ; 1.671329e+000 ];
Tc_11  = [ 2.055978e+002 ; -1.867503e+002 ; 7.112264e+002 ];
omc_error_11 = [ 1.562876e-003 ; 1.586637e-003 ; 3.963291e-004 ];
Tc_error_11  = [ 7.887160e-001 ; 7.369646e-001 ; 6.067601e-001 ];

%-- Image #12:
omc_12 = [ 3.546386e-001 ; -4.103980e-001 ; 1.660815e+000 ];
Tc_12  = [ 2.236279e+002 ; -1.375835e+002 ; 6.275080e+002 ];
omc_error_12 = [ 1.423669e-003 ; 1.440967e-003 ; 3.823010e-004 ];
Tc_error_12  = [ 6.890729e-001 ; 6.455475e-001 ; 5.252350e-001 ];

%-- Image #13:
omc_13 = [ 2.532372e-001 ; -4.592071e-001 ; 1.635127e+000 ];
Tc_13  = [ 2.227360e+002 ; -1.207036e+002 ; 5.973169e+002 ];
omc_error_13 = [ 1.362973e-003 ; 1.376763e-003 ; 3.694788e-004 ];
Tc_error_13  = [ 6.514484e-001 ; 6.100914e-001 ; 4.845182e-001 ];

%-- Image #14:
omc_14 = [ 2.219145e-001 ; -4.469072e-001 ; 1.624086e+000 ];
Tc_14  = [ 2.316733e+002 ; -1.193399e+002 ; 5.463619e+002 ];
omc_error_14 = [ 1.320291e-003 ; 1.332513e-003 ; 3.544145e-004 ];
Tc_error_14  = [ 5.961861e-001 ; 5.612238e-001 ; 4.571081e-001 ];

%-- Image #15:
omc_15 = [ 1.645291e-002 ; -3.559009e-001 ; 1.621126e+000 ];
Tc_15  = [ 1.519161e+002 ; -1.206649e+002 ; 5.536593e+002 ];
omc_error_15 = [ 1.356808e-003 ; 1.366109e-003 ; 2.373707e-004 ];
Tc_error_15  = [ 6.013847e-001 ; 5.548069e-001 ; 4.010150e-001 ];

%-- Image #16:
omc_16 = [ -1.528916e-001 ; 1.317017e-001 ; 1.777582e+000 ];
Tc_16  = [ 3.360700e+002 ; -9.496367e+001 ; 6.935293e+002 ];
omc_error_16 = [ 1.754738e-003 ; 1.797141e-003 ; 2.291979e-004 ];
Tc_error_16  = [ 7.527680e-001 ; 7.157063e-001 ; 5.969994e-001 ];

%-- Image #17:
omc_17 = [ 6.672105e-001 ; -3.012689e-001 ; 1.693064e+000 ];
Tc_17  = [ 2.099363e+002 ; -5.395855e+001 ; 4.333714e+002 ];
omc_error_17 = [ 1.271801e-003 ; 1.257097e-003 ; 4.810227e-004 ];
Tc_error_17  = [ 4.755511e-001 ; 4.493387e-001 ; 4.156401e-001 ];

%-- Image #18:
omc_18 = [ 1.096610e+000 ; -2.867793e-001 ; 1.632231e+000 ];
Tc_18  = [ 1.253183e+002 ; -2.279959e+001 ; 3.194930e+002 ];
omc_error_18 = [ 1.222064e-003 ; 1.128260e-003 ; 6.689076e-004 ];
Tc_error_18  = [ 3.507211e-001 ; 3.268530e-001 ; 3.283709e-001 ];

%-- Image #19:
omc_19 = [ 1.312497e-001 ; -1.085796e+000 ; 1.385202e+000 ];
Tc_19  = [ 1.903352e+002 ; -1.727530e+002 ; 5.152638e+002 ];
omc_error_19 = [ 1.081069e-003 ; 1.133627e-003 ; 6.032918e-004 ];
Tc_error_19  = [ 5.623598e-001 ; 5.225085e-001 ; 3.978566e-001 ];

%-- Image #20:
omc_20 = [ -2.946670e-001 ; -1.149780e+000 ; 1.244199e+000 ];
Tc_20  = [ 6.642472e+001 ; -2.195996e+002 ; 6.572006e+002 ];
omc_error_20 = [ 1.086384e-003 ; 1.096976e-003 ; 6.025254e-004 ];
Tc_error_20  = [ 7.167569e-001 ; 6.496845e-001 ; 4.156843e-001 ];

