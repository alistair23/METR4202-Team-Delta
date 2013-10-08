function [im] =  undistort_image_color( image_name, fc, cc, kc, alpha_c )
    if ~exist('fc')|~exist('cc')|~exist('kc')|~exist('alpha_c')
        fprintf(1,'No intrinsic camera parameters available.\n');
        return
    end

    KK = [fc(1) alpha_c*fc(1) cc(1);0 fc(2) cc(2) ; 0 0 1];

    format_image2 = 'png';

    ima_name = [image_name '.' format_image2];


    %%% READ IN IMAGE:
    I = double(imread(ima_name));

    %% UNDISTORT THE IMAGE:
    if size(I, 3) ~= 1
        [Ipart_1] = rect(I(:,:,1),eye(3),fc,cc,kc,alpha_c,KK);
        [Ipart_2] = rect(I(:,:,2),eye(3),fc,cc,kc,alpha_c,KK);
        [Ipart_3] = rect(I(:,:,3),eye(3),fc,cc,kc,alpha_c,KK);

        I2 = ones(480, 640, 3);
        I2(:,:,1) = Ipart_1;
        I2(:,:,2) = Ipart_2;
        I2(:,:,3) = Ipart_3;
    else
        dist_amount = 1;
        fc_new = dist_amount * fc;
        KK_new = [fc_new(1) alpha_c*fc_new(1) cc(1);0 fc_new(2) cc(2) ; 0 0 1];
        [I2] = rect(I,eye(3),fc,cc,kc,alpha_c,KK_new);
    end

    im = uint8(I2);
end