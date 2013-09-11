function clstrscores=getclstrscores(clstrs,scores)
% GETCLSTRSCORES gets the mean score of a cluster.
%
% GETCLSTRSCORES gets the mean score of a cluster given the scores of the
% individual points.
% 
% INPUTS:
%     clstrs: MxN array. clstrs should be the same size as rangeMatrix.
%     Each element in clstrs is an integer indicating the line cluster the
%     range to which reading belongs.
% 
%     scores: MxN array with the score of each element.
% 
% OUTPUTS:
%     clstrscores: MxK array where K is the largest cluster number. It
%     contains the mean scores for each cluster.
% 
% Abdallah Kassir 1/3/2010

noscans=size(clstrs,1);
clstrscores=zeros(noscans,max(clstrs(:)));
for cntr1=1:noscans
    for cntr2=1:max(clstrs(cntr1,:))
        clstrscores(cntr1,cntr2)=mean(scores(cntr1,clstrs(cntr1,:)==cntr2));
    end
end