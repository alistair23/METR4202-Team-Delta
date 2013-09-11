function combvec=getcombs(ind1,ind2,maincombvec)
% GETCOMBS is an auxiliary function used for obtaining combinations.
%
% GETCOMBS extracts a subset combination from a larger combination array.
% 
% INPUTS:
%     ind1: first limit of subset combination.
% 
%     ind2: second limit of subset combination.
% 
%     maincombvec: main combination array.
% 
% OUTPUTS:
%     combvec: subset combination array.
% 
% Abdallah Kassir 1/3/2010
combind=maincombvec>=ind1 & maincombvec<=ind2;
combvec=maincombvec(combind(:,1)&combind(:,2),:);