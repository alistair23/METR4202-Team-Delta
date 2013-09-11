function [closestTimeStamps, closestTimeStampIndices] = GetClosestTimeStamps( searchTimeStamps, timeStampList )
% GETCLOSESTTIMESTAMPS searches for the closest timestamps within a list.
%
% GETCLOSESTTIMESTAMPS accepts two inputs. The first is a vector of the
% original timestamps and the other is a list of timestamps. The function
% extracts from the list the timestamps closest to the those in the
% original vector.
closestTimeStamps=zeros(size(searchTimeStamps));
closestTimeStampIndices=zeros(size(searchTimeStamps));
for i=1:length(searchTimeStamps)
    [minimum,closestTimeStampIndices(i)] = min( abs(timeStampList-searchTimeStamps(i)) );
    closestTimeStamps(i) = timeStampList(closestTimeStampIndices(i));
end