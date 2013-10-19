function [dTa, dTb, dTc] = InverseKinematics(X, Y, A)
% Takes as arguments the the X and Y positions in mm, measured with 
% respect to the origin at the base and the angle of the tooltip 
% measured with respect to the horizontal in CCW direction.  (This should 
% always be either -90 or 270 in our case)

% B varaible is 1 for elbow up positions and -1 for elbow down positions.
% We always want elbow up, (but not sure how this behaves when moving to 
% negative X values).
B = 1;

% Set the arm lengths in mm
La = 144;
Lb = 93;
Lc = 39;

% Convert the angle of the tooltip to radains 
P = -degtorad(A);

Xp = abs(X) - Lc * cos(P);
Yp = abs(Y) - Lc * sin(P);

% Compute the Gamma value
G = atan2(((-Yp)/(sqrt(Xp^2 + Yp^2))),((-Xp)/(sqrt(Xp^2 + Yp^2))));

% Solve the non-linear equation for the first angle assuming an elbow up 
% or down given from B
Ta = G + B * acos(-(Xp^2+Yp^2+La^2-Lb^2)/(2*La*sqrt(Xp^2+Yp^2)));

% Solve the second angle given the now unique angle of Ta
Tb = (atan2(((Yp-La*sin(Ta))/(Lb)),((Xp-La*cos(Ta))/(Lb)))) - Ta;

% Solve the third angle given the now unique angle of Ta and Tb
Tc = P - (Ta + Tb);

% Convert the angles back to degrees
if (X >= 0)
    dTa = mod(360-radtodeg(Ta), 360);
else
    dTa = mod(180+radtodeg(Ta), 360);
end

if (X >= 0)
    dTb = -mod(-360+radtodeg(Tb), 360);
else
    dTb = mod(-360+radtodeg(Tb), 360);
end

if (X >= 0)
    dTc = -radtodeg(Tc);
else
    dTc = radtodeg(Tc);
end
end

