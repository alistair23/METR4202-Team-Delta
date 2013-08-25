% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function MoveMotors( y_s, x_s, z_s )
%% Define Variables
global mUpOne;
global mDownOne;
global mUpTwo;
global mDownTwo;
global mUpThree;
global mDownThree;

%% Prepare motor
mUpOne.Stop('off');
mUpTwo.Stop('off');
mUpThree.Stop('off');
mUpOne.ResetPosition();
mUpTwo.ResetPosition();
mUpThree.ResetPosition();

%% Main movement
theta1 = InverseKin(x_s, y_s, z_s);
theta2 = InverseKin(x_s*cos((120/180)*pi()) + y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) - x_s*sin((120/180)*pi()), z_s);
theta3 = InverseKin(x_s*cos((120/180)*pi()) - y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) + x_s*sin((120/180)*pi()), z_s);

% Tell Motor One where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpOne.ReadFromNXT();
pos  = data.Position;

% Decide to move up or down
if theta1 < 0
    mUpOne.ActionAtTachoLimit = 'Brake';
    mUpOne.TachoLimit = int16(abs(theta1)) + pos;
    mUpOne.SendToNXT();
else
    mDownOne.ActionAtTachoLimit = 'Brake';
    mDownOne.TachoLimit = int16(abs(theta1)) + pos;
    mDownOne.SendToNXT();
end

% Tell Motor Two where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpTwo.ReadFromNXT();
pos  = data.Position;
    
% Decide to move up or down
if theta2 < 0
    mUpTwo.ActionAtTachoLimit = 'Brake';
    mUpTwo.TachoLimit = int16(abs(theta2)) + pos;
    mUpTwo.SendToNXT();
else
    mDownTwo.ActionAtTachoLimit = 'Brake';
    mDownTwo.TachoLimit = int16(abs(theta2)) + pos;
    mDownTwo.SendToNXT();
end

% Tell Motor Three where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpThree.ReadFromNXT();
pos  = data.Position;
    
% Decide to move up or down
if theta3 < 0
    mUpThree.ActionAtTachoLimit = 'Brake';
    mUpThree.TachoLimit = int16(abs(theta3)) + pos;
    mUpThree.SendToNXT();
else
    mDownThree.ActionAtTachoLimit = 'Brake';
    mDownThree.TachoLimit = int16(abs(theta3)) + pos;
    mDownThree.SendToNXT();
end

% Wait for the last motor to finish
mDownThree.WaitFor();
mDownOne.ActionAtTachoLimit = 'HoldBrake';
mDownTwo.ActionAtTachoLimit = 'HoldBrake';
mDownThree.ActionAtTachoLimit = 'HoldBrake';
end