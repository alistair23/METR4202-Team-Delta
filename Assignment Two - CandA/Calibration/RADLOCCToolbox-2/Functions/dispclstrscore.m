function dispclstrscore(theta,ranges,clstrs,score,hflag)
% DISPCLSTRSCORE displays the line clustered scan with scores.
%
% DISPCLSTRSCORE displays the line clustered scan with scores. 'score' is
% an optional input.
%
% USAGE:
%     dispclstrscore(angleVector,rangeVector,clstrs,scores);
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeVector: 1xN vector; ranges of laser scan
% 
%     clstrs: 1xN vector, cluster numbers of laser points
% 
%     scores: 1xM vector, where M is the number of clusters. 'scores'
%     includes the score for each cluster

if ~exist('hflag','var') || isempty(hflag)
    hflag=0;
end

hold on;
k=max(clstrs);
h=min(clstrs);
noclstrs=k-h+1;
cmap=lines(noclstrs);
% cmap=[0,0,1;1,0,0];
[z,x]=pol2cart(theta,ranges);

xcent=zeros(1,k-h+1);
zcent=zeros(1,k);

% get centroids
for cntr=1:noclstrs
    xcent(cntr)=mean(x(clstrs==h+cntr-1));
    zcent(cntr)=mean(z(clstrs==h+cntr-1));
end

if ~exist('score','var')
    score=unique(clstrs);
end


for cntr=1:noclstrs
    plot(x(clstrs==h+cntr-1),z(clstrs==h+cntr-1),'.','color',cmap(cntr,:));
%     plot(x(clstrs==cntr),z(clstrs==cntr),'color',cmap(cntr,:));
    if noclstrs>2
        text(xcent(cntr),zcent(cntr),num2str(score(cntr)));
    end
end


plot(0,0,'o');
% axis manual;
axis image;
xlabel('laser range finder at (0,0), units: m');
grid on;

hold off;