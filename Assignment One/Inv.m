function theta = Inv( x_0, y_0, z_0 )
based = 230;
edge = 70;
lengthTop = 135;
lengthBot = 265;
startHeight = 75;


z_0 = (z_0 + startHeight); %Re calibrate based on the starting height

y_1 = -0.5 * 0.57735 * based;
y_0 = (y_0 - 0.5 * 0.57735 * edge);

a = (x_0^2 + y_0^2 + z_0^2 + lengthTop^2 - lengthBot^2 - y_1^2)/(2*z_0);
b = (y_1 - y_0)/z_0;

% Caclulate the discriminant
d = (a + b*y_1) * (a + b*y_1) + lengthTop*(b^2 * lengthTop + lengthTop);
if d < 0
    Message = 'Did not Work!!! Invlaid inputs';
    disp(Message);
    return
end

y_j = (y_1 - a*b - sqrt(d))/(b^2 + 1);
z_j = a + b*y_j;

if y_j > y_1
    theta = 180 * atan(-z_j/(y_1 - y_j))/pi + 180;
else
    theta = 180 * atan(-z_j/(y_1 - y_j))/pi;
end

theta = theta*5; % Gear Ratio

end
