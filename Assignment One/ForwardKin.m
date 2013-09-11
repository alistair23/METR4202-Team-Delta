% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function [ x_0, y_0, z_0 ] = ForwardKin( theta1, theta2, theta3 )
    base = 270;
    edge = 145;
    lengthTop = 143;
    lengthBot = 276;    

    t = (base - edge) * (0.5/sqrt(3));
 
    % Convert to radians
    theta1 = (theta1/5) * (pi/180);
    theta2 = (theta2/5) * (pi/180);
    theta3 = (theta3/5) * (pi/180);
 
    %% Calculate the sphere coords
     y1 = -(t + lengthTop * cos(theta1));
     z1 = -lengthTop * sin(theta1);
 
     y2 = (t + lengthTop * cos(theta2)) * 0.5;
     x2 = y2 * sqrt(3);
     z2 = -lengthTop * sin(theta2);
 
     y3 = (t + lengthTop * cos(theta3)) * 0.5;
     x3 = -y3 * sqrt(3);
     z3 = -lengthTop * sin(theta3);
 
     dnm = (y2-y1)*x3 -(y3-y1)*x2;
 
     w1 = y1^2 + z1^2;
     w2 = x2^2 + y2^2 + z2^2;
     w3 = x3^2 + y3^2 + z3^2;
     
     % x = (a1*z + b1)/dnm
     a1 = (z2-z1)*(y3-y1)-(z3-z1)*(y2-y1);
     b1 = -((w2-w1)*(y3-y1)-(w3-w1)*(y2-y1))/2;
 
     % y = (a2*z + b2)/dnm;
     a2 = -(z2-z1)*x3+(z3-z1)*x2;
     b2 = ((w2-w1)*x3 - (w3-w1)*x2)/2;
 
     % a*z^2 + b*z + c = 0
     a = a1^2 + a2^2 + dnm^2;
     b = 2*(a1*b1 + a2*(b2-y1*dnm) - z1*(dnm^2));
     c = (b2-y1*dnm)*(b2-y1*dnm) + b1^2 + dnm*dnm*(z1^2 - lengthBot^2);
  
     % discriminant
     d = b^2 - 4*a*c;
     if d < 0
        Message = 'Did not Work!!! Invlaid inputs';
        disp(Message);
        return
     end
 
     z_0 = -0.5*(b+sqrt(d))/a;
     x_0 = (a1*z_0 + b1)/dnm;
     y_0 = (a2*z_0 + b2)/dnm;
end

