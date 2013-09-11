function scannos=selectscans(angleVector,rangeMatrix,speed)
% SELECTSCANS is a debugging function.
% 
% SELECTSCANS is a debugging function.

scannos=[];
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
%         imdistline;
        title(num2str(cntr));
        b=waitforbuttonpress;
        if b~=0
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
            elseif btnprsd=='s'
                scannos=[scannos,cntr];
            end
        end
        cla;
    end
end

% remove repetitions
scannos=unique(scannos);