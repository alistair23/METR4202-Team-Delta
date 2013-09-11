function scores=getinitestscore(delta,phi,Nci,BoardCorners,angleVector,rangeMatrix,debug)
% GETINIESTSCORE gets the score based on the tranformation estimate.
%
% GETINIESTSCORE uses the transformation estimate to score the match of the
% points with the initial estimate. The better the estimate the more
% accurate the score will be.
% 
% USAGE:
%     scores=getinitestscore(delta,phi,Nci,BoardCorners,angleVector,rangeMatrix);
% 
% INPUTS:
%     delta: 3x1 translation offset vector.
% 
%     phi: 3x3 rotation matrix.
% 
%     angleVector: 1xN vector; angleVector lists the angles for the ranges
%     in rangeMatrix.
% 
%     rangeMatrix: MxN array; Each row in rangeMatrix contains a laser scan
%     with ranges at the angles specified in angleVector.
% 
%     Nci: 3xM vector containing the normal vector of the
%     calibration plane of the corresponding laser scan.
% 
%     BoardCorners: is a 1xnoscans array of structures. Each structure has
%     the following elements:
% 
%         n_sq_x: number of squares of the calibration chessboard along the
%         x direction.
% 
%         n_sq_y: number of squares of the calibration chessboard along the
%         y direction.
% 
%         corners: 3x((n_sq_x+1)*(n_sq_y+1)) array with the coordinates of
%         the chessboard corners in the camera frame.
% 
% OUTPUTS:
%     scores: MxN array with the score for each laser point.
% 
% Abdallah Kassir 1/3/2010



noscans=size(rangeMatrix,1);

% intitial estimate score:
% Manhattan distance from centroid of board projected on to board
[z,x]=pol2cart(repmat(angleVector,[noscans,1]),rangeMatrix);

% BoardCorners=GetBoardCorners();
cameraPlanes = Nci;

scores=zeros(size(rangeMatrix));


if ~exist('debug','var') || isempty(debug)
    debug=0;
end

        
% camera axis
org=[0;0;0];
xax=[1;0;0];
yax=[0;1;0];
zax=[0;0;1];
org=phi*(org-delta);
xax=phi*(xax-delta);
yax=phi*(yax-delta);
zax=phi*(zax-delta);

if debug
    figure;
end

for cntr=1:noscans
    
    % get norm
    if cntr<=size(BoardCorners,2) &&  ~isnan(BoardCorners(cntr).corners(1))
        
        lBoardCorners=BoardCorners(cntr).corners;
        lBoardCorners=lBoardCorners-repmat(delta,1,size(lBoardCorners,2)); % minus delta
        lBoardCorners=phi*lBoardCorners;
        
        % get mean point
        meanpt=mean(lBoardCorners,2);

        lpts=[x(cntr,:);zeros(size(angleVector));z(cntr,:)];
        lvecs=lpts-repmat(meanpt,[1,length(lpts)]);
        
        % get norm vector
        N=cameraPlanes(:,cntr);
    
        N=phi*N; % normal vector no need for delta because delta is the coordinates of the camera origin in the laser frame

        % change to unit vector
        N=N/norm(N);
        
        % debugging
        if debug
            clf;
            % display board
            lBoardCornersx=zeros(BoardCorners(cntr).n_sq_x+1,BoardCorners(cntr).n_sq_y+1);
            lBoardCornersy=zeros(BoardCorners(cntr).n_sq_x+1,BoardCorners(cntr).n_sq_y+1);
            lBoardCornersz=zeros(BoardCorners(cntr).n_sq_x+1,BoardCorners(cntr).n_sq_y+1);

            lBoardCornersx(:)=lBoardCorners(1,:);
            lBoardCornersy(:)=lBoardCorners(2,:);
            lBoardCornersz(:)=lBoardCorners(3,:);
            hold on;
            h= mesh(lBoardCornersx,lBoardCornersy,lBoardCornersz);
            set(h,'edgecolor','red');        
            plot3(x(cntr,:),zeros(size(x(cntr,:))),z(cntr,:),'.');
            xlabel('x');
            ylabel('y');
            zlabel('z');
            axis equal;
            title(num2str(cntr));


            plot3([org(1),xax(1)],[org(2),xax(2)],[org(3),xax(3)]);text(xax(1),xax(2),xax(3),'x');
            plot3([org(1),yax(1)],[org(2),yax(2)],[org(3),yax(3)]);text(yax(1),yax(2),yax(3),'y');
            plot3([org(1),zax(1)],[org(2),zax(2)],[org(3),zax(3)]);text(zax(1),zax(2),zax(3),'z');
            plot3([0,N(1)],[0,N(2)],[0,N(3)]);text(N(1),N(2),N(3),'N');
            axis tight;
            cameratoolbar(gcf);
            cameratoolbar(gcf,'SetCoordSys','y');
            while 1
                b=waitforbuttonpress;
                if b~=0
                    btnprsd=get(gcf,'CurrentCharacter');
                    if btnprsd=='e'
                        debug=0; % stop debugging
                    end
                    break;
                end
            end
        end
        
        N=repmat(N,[1,length(angleVector)]);
        dists=cross(lvecs,N);
        dists=sqrt(sum(dists.^2));
        dists=dists+abs(dot(lvecs,N));
        % normalise
        dists=dists./max(dists);
        scores(cntr,:)=1-dists;
    else
        scores(cntr,:)=0;
    end
end
