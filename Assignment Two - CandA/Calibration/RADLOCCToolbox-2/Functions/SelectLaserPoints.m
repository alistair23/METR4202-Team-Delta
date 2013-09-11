function laserInd= SelectLaserPoints(angleVector,rangeVector)
% USAGE:
%   [laserRange,laserAngle] = SelectLaserPoints(laserRange,laserAngle)
%   Displays a plot of the range data and prompts user to select
%   a single region within.
%
% INPUTS:
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeVector: 1xN vector; ranges of laser scan
%
% OUPUTS:
%     laserInd: indices of the selected set.
%
% Written by James Underwood 10/07/06
%
% Modified by Abdallah Kassir 1/3/2010

dispclstrscore(angleVector,rangeVector,ones(size(angleVector)));
title('Select appropriate region in laser scan - bound 1');
hold on;
p=plot(0,0,'r');
hold off;
set(gcf, 'WindowButtonMotionFcn', {@linedispcallback,p});
set(gcf,'Pointer','crosshair');
% set(gcf, 'WindowButtonDownFcn', 'uiresume(gcbf)');
% uiwait;
b=waitforbuttonpress;
if b~=0
    btnprsd=get(gcf,'CurrentCharacter');
    if btnprsd=='e'
        laserInd=0;
    else
        laserInd=[];
    end
    set(gcf, 'WindowButtonMotionFcn', '');
    return;
end
pt = get(gca, 'CurrentPoint');
pt1=pt(1,1:2);

title('Select appropriate region in laser scan - bound 2');
b=waitforbuttonpress;
if b~=0
    btnprsd=get(gcf,'CurrentCharacter');
    if btnprsd=='e'
        laserInd=0;
    else
        laserInd=[];
    end
    set(gcf, 'WindowButtonMotionFcn', '');
    return;
end
pt = get(gca, 'CurrentPoint');
pt2=pt(1,1:2);

% choose smaller range if user selected in opposite order
if det([pt1;pt2])<0
    % swap points
    pttmp=pt1;
    pt1=pt2;
    pt2=pttmp;
end

% find angle range using cross product
[z,x]=pol2cart(angleVector,rangeVector);
dets1=cross(repmat([pt1,0]',1,length(x)),[x;z;ones(size(x))]);
dets1=dets1(end,:);
dets2=cross(repmat([pt2,0]',1,length(x)),[x;z;ones(size(x))]);
dets2=dets2(end,:);

laserInd = dets1>0 & dets2<0;
% stop callback
set(gcf, 'WindowButtonMotionFcn', '');

function linedispcallback(src,event,p)

% get mouse position
pt = get(gca, 'CurrentPoint');
x = pt(1, 1);
y = pt(1, 2);

% check if its within axes limits
xLim = get(gca, 'XLim');	
yLim = get(gca, 'YLim');
if x < xLim(1)
    x=xLim(1);
elseif x > xLim(2)
    x=xLim(2);
end
if y < yLim(1)
    y=yLim(1);
elseif y>yLim(2)
    y=yLim(2);
end
set(p,'XData',[0,x],'YData',[0,y]);

function getcoordscallback(src,event)
% get mouse position
pt = get(gca, 'CurrentPoint');
x = pt(1, 1);
y = pt(1, 2);

% check if its within axes limits
xLim = get(gca, 'XLim');	
yLim = get(gca, 'YLim');
if x < xLim(1)
    x=xLim(1);
elseif x > xLim(2)
    x=xLim(2);
end
if y < yLim(1)
    y=yLim(1);
elseif y>yLim(2)
    y=yLim(2);
end

