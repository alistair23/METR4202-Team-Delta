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
x_s = y_i;
y_s = x_i;
z_s = z_i;

%% Main movement
theta1 = InverseKin(x_s, y_s, z_s);
theta2 = InverseKin(x_s*cos((120/180)*pi()) + y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) - x_s*sin((120/180)*pi()), z_s);
theta3 = InverseKin(x_s*cos((120/180)*pi()) - y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) + x_s*sin((120/180)*pi()), z_s);

% Tell Motor One where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpOne.ReadFromNXT();
pos  = data.Position;

% Check for less then zero
if abs(int16(theta1) + pos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta1 + pos) < 0
    mUpOne.ActionAtTachoLimit = 'Brake';
    mUpOne.TachoLimit = abs(int16(theta1) + pos);
    mUpOne.SendToNXT();
else
    mDownOne.ActionAtTachoLimit = 'Brake';
    mDownOne.TachoLimit = abs(int16(theta1) + pos);
    mDownOne.SendToNXT();
end

% Tell Motor Two where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpTwo.ReadFromNXT();
pos  = data.Position;
    
% Check for less then zero
if abs(int16(theta2) + pos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta2 + pos) < 0
    mUpTwo.ActionAtTachoLimit = 'Brake';
    mUpTwo.TachoLimit = abs(int16(theta2) + pos);
    mUpTwo.SendToNXT();
else
    mDownTwo.ActionAtTachoLimit = 'Brake';
    mDownTwo.TachoLimit = abs(int16(theta2) + pos);
    mDownTwo.SendToNXT();
end

% Tell Motor Three where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpThree.ReadFromNXT();
pos  = data.Position;
    
% Check for less then zero
if abs(int16(theta3) + pos) == 0
    % Do nothing
% Decide to move up or down
elseif (theta3 + pos) < 0
    mUpThree.ActionAtTachoLimit = 'Brake';
    mUpThree.TachoLimit = abs(int16(theta3) + pos);
    mUpThree.SendToNXT();
else
    mDownThree.ActionAtTachoLimit = 'Brake';
    mDownThree.TachoLimit = abs(int16(theta3) + pos);
    mDownThree.SendToNXT();
end

% Wait for the last motor to finish
mDownThree.WaitFor();
mDownOne.ActionAtTachoLimit = 'HoldBrake';
mDownTwo.ActionAtTachoLimit = 'HoldBrake';
mDownThree.ActionAtTachoLimit = 'HoldBrake';
end