function showlasmove(angleVector,rangeMatrix,speed)
% SHOWLASMOVE is a debugging function to display the laser scans.
%
% SHOWLASMOVE is a debugging function to display the laser scans.
% 
% USAGE:
%     showclusters(angleVector,rangeMatrix,clstrs);
% 
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     speed: display speed (integer greater than or equal to 1).

figure;
axis;
mov=1;
if ~exist('speed','var')
    mov=0;
end

if mov
    for cntr=1:speed:size(rangeMatrix,1);
        polar(angleVector,rangeMatrix(cntr,:),'.');
        title(num2str(cntr));
        drawnow;
        cla;
    end
else
    cntr=1;
    while 1
        dispclstrscore(angleVector,rangeMatrix(cntr,:),ones(size(angleVector)));
        imdistline;
        title(num2str(cntr));
        while ~waitforbuttonpress
        end
        btnprsd=get(gcf,'CurrentCharacter');
        if btnprsd=='n'
            cntr=cntr+1;
            if cntr>size(rangeMatrix,1)
                cntr=size(rangeMatrix,1);
            end
        elseif btnprsd=='b'
            cntr=cntr-1;
            if cntr<1
                cntr=1;
            end
        elseif btnprsd=='e'
            break;
        end
        cla;
    end
end