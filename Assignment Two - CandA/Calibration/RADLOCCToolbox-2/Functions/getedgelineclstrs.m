function clstrs=getedgelineclstrs(angleVector,rangeMatrix,distth)
% GETEDGELINECLSTRS divides the laser scans into straight lines.
% 
% GETEDGELINECLSTRS divides the laser scans into straight lines by
% considering range jumps and the straight line criteria as described in
% the RADLOCC technical report.
% 
% USAGE:
%     clstrs=getedgelineclstrs(angleVector,rangeMatrix,distth);
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     distth: is the threshold value for the straight line criteria. 
% 
% OUTPUTS:
%     clstrs: MxN array. Each element in clstrs is an integer indicating
%     the line cluster the range to which reading belongs.

fprintf('Progress: ');

if ~exist('distth','var')
    distth=0.02; %m
end

% pec threshold
% objth=1; % metre

% jump threshold
jumpth=0.1; % metre (10cm)

noscans=size(rangeMatrix,1);
nopts=length(angleVector);

mainlncombvec=combnk(1:nopts,2);

% get xs and ys

[x,y]=pol2cart(repmat(angleVector,[noscans,1]),rangeMatrix);


% set splitpoints according to jumps
% all points within the object threshold are included but large jumps should divide the scan

splitpoints=cell(noscans,1);
spointdone=cell(noscans,1);

for cntr1=1:noscans
    range=rangeMatrix(cntr1,:);

    rangeedge=laseredge(range);
    
    jumppts=find(abs(rangeedge)>jumpth);

    splitpoints{cntr1}=[1,jumppts,nopts+1];
    spointdone{cntr1}=[0,zeros(size(jumppts)),1];
end

% loop over all scans
% objects are now divided according to straight lines

progmsg=[];

for cntr1=1:noscans
    fprintf(repmat('\b', 1, length(progmsg)));
    progmsg=sprintf('%%%i',round(cntr1/noscans*100));
    fprintf('%s',progmsg);
    while ~isempty(find(spointdone{cntr1}==0,1))
        % loop over all clusters
        for cntr2=1:length(splitpoints{cntr1})-1
            if ~spointdone{cntr1}(cntr2)
                indcs=splitpoints{cntr1}(cntr2):splitpoints{cntr1}(cntr2+1)-1;

                if length(indcs)>2 % check if worth splitting first

                    % loop over all line combinations
                    mxlen=0;
                    lncombvec=getcombs(indcs(1),indcs(end),mainlncombvec);
                    for cntr3=1:size(lncombvec,1)
                        ind1=lncombvec(cntr3,1);
                        ind2=lncombvec(cntr3,2);

                        % one line should be quicker

                        dist = abs((x(cntr1,ind2)-x(cntr1,ind1))*(y(cntr1,ind1)-y(cntr1,ind1:ind2))-(x(cntr1,ind1)-x(cntr1,ind1:ind2))*(y(cntr1,ind2)-y(cntr1,ind1)))...
                            /norm([x(cntr1,ind2)-x(cntr1,ind1),y(cntr1,ind2)-y(cntr1,ind1)]);

                        if isempty(find(dist>distth,1))
                            if length(dist)>mxlen
                                mxlen=length(dist);
                                mxind1=ind1;
                                mxind2=ind2;
                            end
                        end
                    end

                    % register results
                    if mxind1==indcs(1) && mxind2==indcs(end)
                        spointdone{cntr1}(cntr2)=1;
                    elseif mxind1==indcs(1)
                        spointdone{cntr1}(cntr2)=1;
                        spointdone{cntr1}=[spointdone{cntr1}(1:cntr2),0,spointdone{cntr1}(cntr2+1:end)];
                        splitpoints{cntr1}=[splitpoints{cntr1}(1:cntr2),mxind2+1,splitpoints{cntr1}(cntr2+1:end)];
                    elseif mxind2==indcs(end)
                        spointdone{cntr1}=[spointdone{cntr1}(1:cntr2),0,spointdone{cntr1}(cntr2+1:end)];
                        splitpoints{cntr1}=[splitpoints{cntr1}(1:cntr2),mxind1,splitpoints{cntr1}(cntr2+1:end)];
                    else
                        spointdone{cntr1}=[spointdone{cntr1}(1:cntr2),0,0,spointdone{cntr1}(cntr2+1:end)];
                        splitpoints{cntr1}=[splitpoints{cntr1}(1:cntr2),mxind1,mxind2+1,splitpoints{cntr1}(cntr2+1:end)];
                    end
                    break; % break under all conditions since spointdone exists
                else
                    spointdone{cntr1}(cntr2)=1;
                end
            end
        end
    end
    for cntr2=1:length(splitpoints{cntr1})-1
        clstrs(cntr1,splitpoints{cntr1}(cntr2):splitpoints{cntr1}(cntr2+1)-1)=cntr2;
    end
end

fprintf('\n');