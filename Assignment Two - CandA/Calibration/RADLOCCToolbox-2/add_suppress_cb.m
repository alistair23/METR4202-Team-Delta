% ADD_SUPPRESS_CB is responsible for adding or suppressing scans.
% 
% ADD_SUPPRESS_CB can be used to add or remove scans from the calibration
% data in order to improve the calibration results.

% check inputs
if ~exist('selectionnumbers','var')
    disp('Boards have not been extracted.');
    return;
end

% display
disp('Active scans are:');
disp(selectionnumbers);
addsupnos=input('Select the numbers do you want to add/suppress ([]=None):');

if ~isempty(addsupnos)
    for cntr=1:length(addsupnos)
        ind=find(selectionnumbers==addsupnos(cntr),1);
        if isempty(ind)
            selectionnumbers=[selectionnumbers,addsupnos(cntr)];
        else
            selectionnumbers(ind)=[];
        end
    end

    selectionnumbers=sort(selectionnumbers);
    dispboardpts(angleVector,rangeMatrix,clstrs,boardclstrs,selectionnumbers);
    disp('Rerun calibration.');
    % clear calib data to keep selnos and delta at sync
    clear delta;
    clear phi;
end
