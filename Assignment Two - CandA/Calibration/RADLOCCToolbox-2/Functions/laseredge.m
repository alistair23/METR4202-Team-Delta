function values=laseredge(ranges)
% LASEREDEGE gets the range difference between consecutive laser ranges.
% 
% LASEREDEGE gets the range difference between consecutive laser ranges.
% 
% INPUTS:
%     ranges: 1xN vector of ranges.
% 
% OUTPUTS:
%     values: 1xN vector of differences.

values=zeros(size(ranges));

% pad ranges
ranges=[ranges(1),ranges];

% work with padded ranges array
for cntr=1:length(ranges)-1
    values(cntr)=ranges(cntr+1)-ranges(cntr);
end