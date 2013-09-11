% RADLOCC is the main GUI file.
%
% RADLOCC, the main GUI file, should be called to use the RADLOCC toolbox.
% 
% USAGE:
%     RADLOCC


cell_list = {};

fig_number = 1;

% title name
title_figure = 'RADLOCC Laser-Camera Calibration Toolbox';

% Buttons
cell_list{1,1} = {'Read data','read_data_cb;'};
cell_list{1,2} = {'Auto select','auto_select_cb;'};
cell_list{1,3} = {'Manual select','manual_select_cb;'};
cell_list{1,4} = {'Calibrate','calibrate_cb;'};
cell_list{1,5} = {'Laser onto image','laser_onto_image_cb;'};
cell_list{2,1} = {'Analyse error','analyse_error_cb;'};
cell_list{2,2} = {'Add/Suppress scans','add_suppress_cb;'};
cell_list{2,3} = {'Save','save_cb;'};
cell_list{2,4} = {'Load','load_cb;'};
cell_list{2,5} = {'Exit',['close(' num2str(fig_number) ');']};

% display windows by Bouget
show_window(cell_list,fig_number,title_figure,130,18,0,'clean',12);