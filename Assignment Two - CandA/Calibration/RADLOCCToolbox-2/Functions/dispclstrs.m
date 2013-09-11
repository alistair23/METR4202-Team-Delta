function dispclstrs(angleVector,rangeMatrix,clstrs)
% DISPCLSTRS is an auxiliary function used for debugging.
% 
% DISPCLSTRS is an auxiliary function used to display the line extraction
% results.
% USAGE:
%     dispclstrs(angleVector,rangeMatrix,clstrs);
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

for cntr=1:size(rangeMatrix,1)
    dispclstrscore(angleVector,rangeMatrix(cntr,:),clstrs(cntr,:));
    title(num2str(cntr));
    pause;
    close;
end