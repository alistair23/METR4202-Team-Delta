% ANALYSE_ERROR_CB plots the error for each scan.
% 
% ANALYSE_ERROR_CB plots the error for each scan and can optionally be
% asked to display the selected laser points  from a scan. The user can
% then suppress the scan if the board extraction has produced undesirable
% results.

if ~exist('delta','var')
    disp('No Calibration Data.');
    return;
end
[rmserror,errorvec1,errorvec2]=geterror(Lpts,Nc,delta,phi,Lptsnos);
disp('Total RMS error (in m):');
disp(rmserror);
figure;
scatter(selectionnumbers,errorvec2(selectionnumbers),'+');
xlabel('Scan Number');
ylabel('RMS error (in metres)');
title('RMS error for each scan');
dispnos=input('Enter the number of scans you which to display ([]=none):');

figure;
for cntr=dispnos
    % make sure dispnos is within selectionnumbers
    if ~isempty(find(selectionnumbers==cntr,1))
        dispclstrscore(angleVector,rangeMatrix(cntr,:),clstrs(cntr,:));
        title(['Selected cluster is ',num2str(boardclstrs(cntr))]);
    end
    pause;
    clf;
end
close;