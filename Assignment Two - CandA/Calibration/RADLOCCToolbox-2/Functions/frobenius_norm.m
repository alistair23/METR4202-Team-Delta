function diff = frobenius_norm(A, B)
%Computes the Frobenius norm of the difference of two matrices
%function diff = frobenius_norm(A)
%Fabio Tozeto Ramos 05/10/05
diff = sqrt(sum(sum(abs(A-B).^2,1),2));
