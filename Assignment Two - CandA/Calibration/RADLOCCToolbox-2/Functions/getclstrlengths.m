function lengths=getclstrlengths(angleVector,rangeMatrix,clstrs)
% GETCLSTRLENGTHS gets the length of each cluster.
%
% GETCLSTRLENGTHS gets the length of each cluster. The length is defined as
% the Euclidean distance between the end points.
% 
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
% OUTPUTS:
%     lengths: MxK array where K is the largest cluster number. It
%     contains the lengths for each cluster.

noscans=size(rangeMatrix,1);

[x,y]=pol2cart(repmat(angleVector,[noscans,1]),rangeMatrix);

noclstrs=max(clstrs,[],2);
maxnoclstrs=max(noclstrs);

lengths=zeros(noscans,maxnoclstrs);

for cntr1=1:noscans
    for cntr2=1:noclstrs(cntr1)
        ind=find(clstrs(cntr1,:)==cntr2);
        pt1=min(ind);
        pt2=max(ind);
        len=norm([x(cntr1,pt2)-x(cntr1,pt1),y(cntr1,pt2)-y(cntr1,pt1)]);
        lengths(cntr1,cntr2)=len;
    end
end
