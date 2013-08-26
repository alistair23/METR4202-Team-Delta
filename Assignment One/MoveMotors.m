% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function MoveMotors( x_i, y_i, z_i )
%% Define Variables
global mUpOne;
global mDownOne;
global mUpTwo;
global mDownTwo;
global mUpThree;
global mDownThree;

%% Scale Inputs
x_s = 1.06*y_i;
y_s = 1.3*x_i;
z_s = -z_i;

%% Main movement
theta1 = InverseKin(x_s, y_s, z_s);
theta2 = InverseKin(x_s*cos((120/180)*pi()) + y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) - x_s*sin((120/180)*pi()), z_s);
theta3 = InverseKin(x_s*cos((120/180)*pi()) - y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) + x_s*sin((120/180)*pi()), z_s);

% Tell Motor One where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpOne.ReadFromNXT();
mUpOnepos  = data.Position;
data = mUpTwo.ReadFromNXT();
mUpTwopos  = data.Position;
data = mUpThree.ReadFromNXT();
mUpThreepos  = data.Position;

% Check for less then zero
if abs(int16(theta1) + mUpOnepos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta1 + mUpOnepos) < 0
    mUpOne.TachoLimit = abs(int16(theta1 + mUpOnepos));
    mUpOne.SendToNXT();
else
    mDownOne.TachoLimit = abs(int16(theta1 + mUpOnepos));
    mDownOne.SendToNXT();
end

% Tell Motor Two where to go
% Find current position - account for errors, i.e. if pos is not 0    
% Check for less then zero
if abs(int16(theta2) + mUpTwopos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta2 + mUpTwopos) < 0
    mUpTwo.TachoLimit = abs(int16(theta2 + mUpTwopos));
    mUpTwo.SendToNXT();
else
    mDownTwo.TachoLimit = abs(int16(theta2 + mUpTwopos));
    mDownTwo.SendToNXT();
end

% Tell Motor Three where to go
% Find current position - account for errors, i.e. if pos is not 0    
% Check for less then zero
if abs(int16(theta3) + mUpThreepos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta3 + mUpThreepos) < 0
    mUpThree.TachoLimit = abs(int16(theta3 + mUpThreepos));
    mUpThree.SendToNXT();
else
    mDownThree.TachoLimit = abs(int16(theta3 + mUpThreepos));
    mDownThree.SendToNXT();
end

% Wait for the last motor to finish
mDownOne.WaitFor();
mDownTwo.WaitFor();
mDownThree.WaitFor();
% mDownOne.ActionAtTachoLimit = 'HoldBrake';
% mDownTwo.ActionAtTachoLimit = 'HoldBrake';
% mDownThree.ActionAtTachoLimit = 'HoldBrake';
end