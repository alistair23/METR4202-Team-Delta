% First function for optimisation
%Fabio Tozeto Ramos 05/10/05

% vectorised and modified by Abdallah Kassir
function F=calibFun0(H,Pf,N)
% npts = length(N);
% F = zeros(1, npts);
% for i=1:npts
%     F(i) = dot(N(:,i),H*Pf(:,i)) - norm(N(:,i))^2;
% end

F=(dot(N,H*Pf)-sum(N.^2))./sqrt(sum(N.^2)); % normalised