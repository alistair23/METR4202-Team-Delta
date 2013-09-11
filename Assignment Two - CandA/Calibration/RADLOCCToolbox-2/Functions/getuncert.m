function [deltae,rote]=getuncert(Lpts,Lptsnos,Nc)
% GETUNCERT finds the uncertainty in the calibration parameters
%
% GETUNCERT finds the uncertainty in the calibration parameters using
% jack-knife resampling method.
% 
% INPUTS:
%     Lpts: 3xN array containing all N points to be used for the
%     calibration process.
% 
%     Lptsnos: 3xN array containing the corresponding scan numbers of the
%     points in Lpts.
%     
%     Nc: 3xN array containing the corresponding normal vectors for the
%     points in Lpts.
% 
% INPUTS:
%     deltae: 3x1 vector with the standard deviations of the delta vector.
%     
%     rote: 3x1 vector with the standard deviations of the Euler angles.

% setup for closed form solution
Lptshat = [Lpts(1,:); Lpts(3,:); ones(1,size(Lpts,2))];

Avec=zeros(size(Lpts,2),9);

for cntr=1:size(Lpts,2)
    Avec(cntr,:)=reshape(Lptshat(:,cntr)*Nc(:,cntr)',1,9);
end

N=sqrt(sum(Nc.^2)');

% normalise
Avec=Avec./repmat(N,1,9);

% get indices for each scan
scannos=unique(Lptsnos);

sinds=cell(1,max(scannos));

for cntr=scannos
    sinds{cntr}=find(Lptsnos==cntr);
end

scancomb=combnk(scannos,length(scannos)-1);
% scancomb=combnk(scannos,5);

% allocate memory for parameters

parmat=zeros(size(scancomb,1),6);
detmat=zeros(size(scancomb,1),1);

options = optimset('LargeScale','off','Display','off');
warning('off','optim:fmincon:NLPAlgLargeScaleConflict');

Ns=size(scancomb,1);

% loop over all scan combinations
for cntr=1:size(scancomb,1)
    ctinds=cell2mat(sinds(scancomb(cntr,:)));
    A=Avec(ctinds,:);
    ctN=N(ctinds);
    h=A\ctN;
    H=reshape(h,3,3)';
    ctphi=[H(:,1), cross(-H(:,1),H(:,2)), H(:,2)]';
    ctphi0=ctphi;
    ctphi = fmincon(@(ctphi)frobenius_norm(ctphi,ctphi0),ctphi0,[],[],[],[],[],[],@constraint_phi,options);
    ctrot=dcm2angvec(ctphi);
    ctdelta=H(:,3); % delta positive
    parmat(cntr,:)=[ctdelta',ctrot'];
    detmat(cntr)=1/cond(A);
end

% jack knifing
deltamat=parmat(:,1:3);
rotmat=parmat(:,4:6);

% get average of delta
deltamatm=sum(deltamat)/Ns;

% get average of rot
[rotmatx,rotmaty]=pol2cart(rotmat,ones(size(rotmat)));
rotmatxm=sum(rotmatx)/Ns;
rotmatym=sum(rotmaty)/Ns;
rotmatm=cart2pol(rotmatxm,rotmatym);

% get differences
deltamatd=deltamat-repmat(deltamatm,Ns,1);
rotmatd=rotmat-repmat(rotmatm,Ns,1);


% adjust for angle differences more than pi and less than -pi
rotmatd(rotmatd>pi)=rotmatd(rotmatd>pi)-2*pi;
rotmatd(rotmatd<-pi)=rotmatd(rotmatd<-pi)+2*pi;

deltae=sqrt((Ns-1)/Ns*sum(deltamatd.^2))';
rote=sqrt((Ns-1)/Ns*sum(rotmatd.^2))';


