function scores=getpecscores2(angleVector,rangeMatrix,laserDivisor)
% PECSCORES2 gets the frequency score for each point.
% 
% PECSCORES2 gets the frequency score for each point. The frequency is
% calculated using bins whose size depends of 'laserDivisor'.
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     laserDivisor: scalar indicating the resolution of the laser at the
%     time of capture.
% 
% OUTPUTS:
%     scores: MxN array with the score for each laser point.


% pecscores2 gives a score corresponding to the score of range bins

noscans=size(rangeMatrix,1);

pre=0.01; % m, precision
hx=0:pre:ceil(max(rangeMatrix(:))); % histogram vector

h=hist(rangeMatrix,hx)./noscans; % normalize

% get pecularity score number 2
rindcs=round(rangeMatrix./pre)+1;
aindcs=meshgrid(1:length(angleVector),1:noscans);
scores=h(sub2ind(size(h),rindcs,aindcs));
scores=1-scores;