function InsertLaserIntoImage( im, angleVector, rangeVector, delta, phi, f, c, k, alpha,deltae,rote,fe,ce,ke,alphae)
% INSERTLASERINTOIMAGE reprojects the laser points onto the image.
% 
% INSERTLASERINTOIMAGE reprojects the laser points onto the image. It takes
% as input the extrinsic laser-camera parameters and the camera parameters.
% It also takes as input the standard deviations of the errors in the
% parameters.

% INPUTS:
%     im: the image selected for reprojection.
% 
%     angleVector: row vector of the angles of the laser data.
% 
%     rangeVector: row vector, same size as angleVector, containing the
%     corresponding ranges.
% 
%     delta: translation 3x1 vector of the laser-camera calibration.
% 
%     phi: rotation 3x3 matrix of the laser-camera calibraion.
% 
%     f: focal length vector (from camera calibration).
% 
%     c: principal point vector (from camera calibration).
% 
%     k: distortion vector (from camera calibration).
% 
%     alpha: skew coefficient (from camera calibration).;
% 
%     deltae: standard deviation of error in delta.
% 
%     rote: standard deviation of error in angles forming phi.
% 
%     fe: standard deviation of error in f.
% 
%     ce: standard deviation of error in c.
% 
%     ke: standard deviation of error in ke.
% 
%     alphae: standard deviation of error in alpha


% get rotation vector
rot=dcm2angvec(phi);
phiinv=inv(phi);

%% Get points

% change to cartesian coords
[zl,xl]=pol2cart(angleVector,rangeVector);

Lpts=[xl;zeros(size(xl));zl];

% change to mm (camera parameters in mm)
Lpts=Lpts.*1000;
delta=delta.*1000;
deltae=deltae.*1000;

% apply laser to camera transformation
Cpts=phiinv*Lpts+repmat(delta,1,size(Lpts,2));

xc=Cpts(1,:);
yc=Cpts(2,:);
zc=Cpts(3,:);


%normalise over Z (in this frame);
a=xc./zc;
b=yc./zc;

% add distortion

r = sqrt(a.^2 + b.^2);

ad = a.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6) +  2.*k(3).*a.*b + k(4).*(r.^2 + 2.*a.^2);
bd = b.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6) +  k(3).*(r.^2 + 2.*b.^2) + 2.*k(4).*a.*b;


% image coordinates
x = f(1).*(ad + alpha.*bd) + c(1) + 1; % add 1 for matlab coords
y = f(2).*bd + c(2) + 1; % add 1 for matlab coords



%% Get error (uncertainty)

% get rotation derivative matrices
% check conventions in labbook

[phi,phix,phiy,phiz,dphix,dphiy,dphiz]=angvec2dcm(rot);
   
% partial derivatives of points per delta params
dpcddeltax=repmat([1;0;0],1,size(Lpts,2));
dpcddeltay=repmat([0;1;0],1,size(Lpts,2));
dpcddeltaz=repmat([0;0;1],1,size(Lpts,2));

% partial derivatives of points per rot params
dpcdrot1=-phiinv*(phiz*phiy*dphix)*phiinv*Lpts;
dpcdrot2=-phiinv*(phiz*dphiy*phix)*phiinv*Lpts;
dpcdrot3=-phiinv*(dphiz*phiy*phix)*phiinv*Lpts;

dxcddeltax=dpcddeltax(1,:);
dycddeltax=dpcddeltax(2,:);
dzcddeltax=dpcddeltax(3,:);

dxcddeltay=dpcddeltay(1,:);
dycddeltay=dpcddeltay(2,:);
dzcddeltay=dpcddeltay(3,:);

dxcddeltaz=dpcddeltaz(1,:);
dycddeltaz=dpcddeltaz(2,:);
dzcddeltaz=dpcddeltaz(3,:);

dxcdrot1=dpcdrot1(1,:);
dycdrot1=dpcdrot1(2,:);
dzcdrot1=dpcdrot1(3,:);

dxcdrot2=dpcdrot2(1,:);
dycdrot2=dpcdrot2(2,:);
dzcdrot2=dpcdrot2(3,:);

dxcdrot3=dpcdrot3(1,:);
dycdrot3=dpcdrot3(2,:);
dzcdrot3=dpcdrot3(3,:);

% derivatives over xc
dadxc=1./zc;
% dbdxc=0;
drdxc=a.*dadxc./r;
daddxc=dadxc.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6)...
    +a.*(2.*k(1).*r.*drdxc + 4.*k(2).*r.^3.*drdxc + 6.*k(5).*r.^5.*drdxc)...
    +2.*k(3).*dadxc.*b +k(4).*(2.*r.*drdxc +4.*a.*dadxc);
dbddxc=b.*(2.*k(1).*r.*drdxc + 4.*k(2).*r.^3.*drdxc + 6.*k(5).*r.^5.*drdxc)...
    +k(3).*2.*r.*drdxc +2.*k(4).*b.*dadxc;

% derivatives over yc
% dadyc=0;
dbdyc=1./zc;
drdyc=b.*dbdyc./r;
daddyc=a.*(2.*k(1).*r.*drdyc + 4.*k(2).*r.^3.*drdyc + 6.*k(5).*r.^5.*drdyc)...
    +2.*k(3).*a.*dbdyc +k(4).*2.*r.*drdyc;
dbddyc=dbdyc.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6)...
    +b.*(2.*k(1).*r.*drdyc + 4.*k(2).*r.^3.*drdyc + 6.*k(5).*r.^5.*drdyc)...
    +k(3).*(2.*r.*drdyc+4.*b.*dbdyc) +2.*k(4).*a.*dbdyc;

% derivatives over zc
dadzc=-xc./(zc.^2);
dbdzc=-yc./(zc.^2);
drdzc=(a.*dadzc+b.*dbdzc)./r;
daddzc=dadzc.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6)...
    +a.*(2.*k(1).*r.*drdzc + 4.*k(2).*r.^3.*drdzc + 6.*k(5).*r.^5.*drdzc)...
    +2.*k(3).*(a.*dbdzc + b.*dadzc) + k(4).*(2.*r.*drdzc+4.*a.*dadzc);
dbddzc=dbdzc.*(1 + k(1).*r.^2 + k(2).*r.^4 + k(5).*r.^6)...
    +b.*(2.*k(1).*r.*drdzc + 4.*k(2).*r.^3.*drdzc + 6.*k(5).*r.^5.*drdzc)...
    +k(3).*(2.*r.*drdzc+4.*b.*dbdzc)+2.*k(4).*(a.*dbdzc+b.*dadzc);

%
% x Jacobian
%
dxdxc=f(1).*(daddxc+alpha.*dbddxc);
dxdyc=f(1).*(daddyc+alpha.*dbddyc);
dxdzc=f(1).*(daddzc+alpha.*dbddzc);

dxddeltax=dxdxc.*dxcddeltax+dxdyc.*dycddeltax+dxdzc.*dzcddeltax;
dxddeltay=dxdxc.*dxcddeltay+dxdyc.*dycddeltay+dxdzc.*dzcddeltay;
dxddeltaz=dxdxc.*dxcddeltaz+dxdyc.*dycddeltaz+dxdzc.*dzcddeltaz;

dxdrot1=dxdxc.*dxcdrot1+dxdyc.*dycdrot1+dxdzc.*dzcdrot1;
dxdrot2=dxdxc.*dxcdrot2+dxdyc.*dycdrot2+dxdzc.*dzcdrot2;
dxdrot3=dxdxc.*dxcdrot3+dxdyc.*dycdrot3+dxdzc.*dzcdrot3;

dxdf1=ad.*alpha.*bd;
dxdf2=zeros(size(ad));
dxdc1=ones(size(ad));
dxdc2=zeros(size(ad));
dxdk1=f(1).*(a.*r.^2 + alpha.*b.*r.^2);
dxdk2=f(1).*(a.*r.^4 + alpha.*b.*r.^4);
dxdk3=f(1).*(2.*a.*b + alpha.*(r.^2+2.*b.^2));
dxdk4=f(1).*(r.^2+2.*a.^2+alpha.*(2.*a.*b));
dxdk5=f(1).*(a.*r.^6 + alpha.*(b.*r.^6));
dxdalpha=f(1).*bd;

Jx=[dxddeltax',dxddeltay',dxddeltaz',dxdrot1',dxdrot2',dxdrot3',dxdf1',dxdf2',dxdc1',dxdc2',dxdk1',dxdk2',dxdk3',dxdk4',dxdk5',dxdalpha'];

% y Jacobian

dydxc=f(2).*dbddxc;
dydyc=f(2).*dbddyc;
dydzc=f(2).*dbddzc;

dyddeltax=dydxc.*dxcddeltax+dydyc.*dycddeltax+dydzc.*dzcddeltax;
dyddeltay=dydxc.*dxcddeltay+dydyc.*dycddeltay+dydzc.*dzcddeltay;
dyddeltaz=dydxc.*dxcddeltaz+dydyc.*dycddeltaz+dydzc.*dzcddeltaz;

dydrot1=dydxc.*dxcdrot1+dydyc.*dycdrot1+dydzc.*dzcdrot1;
dydrot2=dydxc.*dxcdrot2+dydyc.*dycdrot2+dydzc.*dzcdrot2;
dydrot3=dydxc.*dxcdrot3+dydyc.*dycdrot3+dydzc.*dzcdrot3;


dydf1=zeros(size(bd));
dydf2=bd;
dydc1=zeros(size(bd));
dydc2=ones(size(bd));
dydk1=f(2).*b.*r.^2;
dydk2=f(2).*b.*r.^4;
dydk3=f(2).*(r.^2+2.*b.^2);
dydk4=f(2).*(2.*a.*b);
dydk5=f(2).*b.*r.^6;
dydalpha=zeros(size(bd));

Jy=[dyddeltax',dyddeltay',dyddeltaz',dydrot1',dydrot2',dydrot3',dydf1',dydf2',dydc1',dydc2',dydk1',dydk2',dydk3',dydk4',dydk5',dydalpha'];

% error matrix
Q=diag([deltae',rote',fe',ce',ke',alphae'].^2); % square errors
% Q=diag([deltae',rote',zeros(size(fe')),zeros(size(ce')),zeros(size(ke')),zeros(size(alphae'))].^2); % square errors
% Q=diag([1,0,0,0,0,0,0,0,0,0,0].^2); % square errors
% Q1(4:6,4:6)=Q1(4:6,4:6)*1e6;
% Q1(4:6,1:3)=Q1(4:6,1:3)*1e3;
% Q1(1:3,4:6)=Q1(1:3,4:6)*1e3;
% Q(1:6,1:6)=Q1;

unx2=diag(Jx*Q*Jx');
uny2=diag(Jy*Q*Jy');

errorx=sqrt(unx2)';
errory=sqrt(uny2)';


%get only pixels within the image
validindices=find(x<=size(im,2) & x>0 & y<=size(im,1) & y>0);
% invind=x>size(im,2) | x<1 | y>size(im,1) | y<1;

px=x(validindices);
py=y(validindices);
perrorx=errorx(validindices);
perrory=errory(validindices);

%% Display

figure;
warning('off','Images:initSize:adjustingMag');
imshow(im,[]);
warning('on','Images:initSize:adjustingMag');
hold on;
c=lines(length(px));
scatter(px,py,10,'blue','filled');
ellipse(2.*perrorx,2.*perrory,zeros(size(x)),px,py,c);


