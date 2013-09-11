function [zero, diff] = constraint_phi(A)
%Compute the constraint for the rotation matrix phi
%function diff = constraint_phi(A)
%Fabio Tozeto Ramos 05/10/05
diff = A*A' - eye(3);
zero =0;