% Copyright 2007 The MathWorks, Inc.
function showDifference(squareMeans, chartSquares)
axes(gca)
observedL = reshape(cellfun(@(x) x(1,1,1), squareMeans), [], 1);
observedA = reshape(cellfun(@(x) x(1,1,2), squareMeans), [], 1);
observedB = reshape(cellfun(@(x) x(1,1,3), squareMeans),[], 1);

chartL = reshape(cellfun(@(x) x(1,1,1), chartSquares), [], 1);
chartA = reshape(cellfun(@(x) x(1,1,2), chartSquares), [], 1);
chartB = reshape(cellfun(@(x) x(1,1,3), chartSquares),[], 1);


d = 4;
cform = makecform('lab2srgb');
numVals = length(observedA);
observedCircles = cell(1,numVals);
chartCircles = cell(1,numVals);
lines = cell(1,numVals);
for i = 1:numVals
	observedCircles{i} = circle(observedA(i), observedB(i), observedL(i), d, applycform(squareMeans{i}, cform),0);
	chartCircles{i} = circle(chartA(i), chartB(i), chartL(i), d, applycform(chartSquares{i}, cform),1);

	% 3-D
	lines{i} = line([observedA(i) chartA(i)], [observedB(i) chartB(i)], [observedL(i) chartL(i)]);
% 	labels{i} = text(chartA(i), chartB(i), chartL(i), num2str(i), 'FontWeight', 'Bold');

end

daspect([1,1,1])
view(-19,4)
drawnow

%%
function h = circle(x,y,z,r, color, type)

hold on
n = 20;
theta = (-n:2:n)/n*pi;
phi = (-n:2:n)'/n*pi/2;
cosphi = cos(phi); cosphi(1) = 0; cosphi(n+1) = 0;
sintheta = sin(theta); sintheta(1) = 0; sintheta(n+1) = 0;

xx = r*cosphi*cos(theta) + x;
yy = r*cosphi*sintheta + y;
zz = r*sin(phi)*ones(1,n+1) +z ;
if type
    h = patch(surf2patch(surf(xx,yy,zz)));
    grid on
    edgeColor = color;
else
xp = x+r;
xm = x-r;
yp = y+r;
ym = y-r;
zp = z+r;
zm = z-r;
%Vertices
verts = [xm, ym, zm;xp, ym, zm;xp, yp, zm;xm, yp, zm; xm, ym, zp;xp, ym, zp;xp, yp, zp;xm, yp, zp];
%faces
f1 = [1 2 6 5; 2 3 7 6; 3 4 8 7; 4 1 5 8; 1 2 3 4; 5 6 7 8];
h = patch('Vertices', verts, 'Faces', f1, 'facealpha', .5);
edgeColor = 'k';
end
set(h, 'FaceColor', color, 'EdgeColor', edgeColor)
hold off


