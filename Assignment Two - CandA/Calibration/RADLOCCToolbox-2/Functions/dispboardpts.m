function dispboardpts(angleVector,rangeMatrix,clstrs,boardclstrs,selectionnumbers)
% DISPBOARDPTS displays the selected calibration points onto one scan.
%
% DISPBOARDPTS displays the selected calibration points onto one scan.
%
% USAGE:
%     dispboardpts(angleVector,rangeMatrix,clstrs,boardclstrs,selectionnumbers)
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     clstrs: MxN array. clstrs should be the same size as rangeMatrix.
%     Each element in clstrs is an integer indicating the line cluster the
%     range to which reading belongs.
% 
%     boardclstrs: Mx1 vector with the selected cluster of each scan.
%     (0=none)
% 
%     selectionnumbers: row vector containing the active scan numbers

% remove scans with no boardcluster (guard)
selectionnumbers(boardclstrs(selectionnumbers)==0)=[];

if isempty(selectionnumbers)
    disp('No selected boards.');
    return;
end

noscans=size(rangeMatrix,1);
% conver to cartestian coordinates
[z,x]=pol2cart(repmat(angleVector,[noscans,1]),rangeMatrix);

figure;
hold on;
nopoints=0;
for cntr=selectionnumbers
    ind=(clstrs(cntr,:)==boardclstrs(cntr));
    plot(x(cntr,ind),z(cntr,ind),'r.',0,0,'bo');
    nopoints=nopoints+length(ind);
end
hold off;

axis equal;
grid on;
legend('Laser Points','Laser Origin');
title('Laser Points on Board Planes');
xlabel('x');
ylabel('z');
fprintf( 'Planes selected:');disp(selectionnumbers);
fprintf( 'Total of %d planes selected, with a total of %d data points.\n', length(selectionnumbers),nopoints);
drawnow;