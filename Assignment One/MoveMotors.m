% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function MoveMotors( x_s, y_s, z_s )
% verify that the RWTH - Mindstorms NXT toolbox is installed.
if verLessThan('RWTHMindstormsNXT', '4.01');
    error(strcat('This program requires the RWTH - Mindstorms NXT Toolbox ' ...
    ,'version 4.01 or greater. Go to http://www.mindstorms.rwth-aachen.de ' ...
    ,'and follow the installation instructions!'));
end%if


%% Prepare
COM_CloseNXT all
close all
clear all

%% Connect to NXT via USB
h = COM_OpenNXT('USB.ini');
COM_SetDefaultNXT(h);

%% Set params
power = 75;
port = [MOTOR_A; MOTOR_B; MOTOR_C];  % motorports to control the delta robot

%% Create motor objects
% we use holdbrake, make sense for robotic arms
mUpOne    = NXTMotor(port(1), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownOne  = NXTMotor(port(1), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

mUpTwo    = NXTMotor(port(2), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownTwo  = NXTMotor(port(2), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

mUpThree    = NXTMotor(port(3), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownThree  = NXTMotor(port(3), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);


%% Prepare motor
mUpOne.Stop('off');
mUpTwo.Stop('off');
mUpThree.Stop('off');
mUpOne.ResetPosition();
mUpTwo.ResetPosition();
mUpThree.ResetPosition();

%% Main movement
  
theta1 = Inv(x_s, y_s, z_s);
theta2 = Inv(x_s*cos((120/180)*pi()) + y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) - x_s*sin((120/180)*pi()), z_s);
theta3 = Inv(x_s*cos((120/180)*pi()) - y_s*sin((120/180)*pi()), y_s*cos((120/180)*pi()) + x_s*sin((120/180)*pi()), z_s);

% Tell Motor One where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpOne.ReadFromNXT();
pos  = data.Position;

% Decide to move up or down
if theta1 < 0
    mUpOne.ActionAtTachoLimit = 'Brake';
    mUpOne.TachoLimit = int8(abs(theta1)) + pos;
    mUpOne.SendToNXT();
else
    mDownOne.ActionAtTachoLimit = 'Brake';
    mDownOne.TachoLimit = int8(abs(theta1)) + pos;
    mDownOne.SendToNXT();
end

% Tell Motor Two where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpTwo.ReadFromNXT();
pos  = data.Position;
    
% Decide to move up or down
if theta2 < 0
    mUpTwo.ActionAtTachoLimit = 'Brake';
    mUpTwo.TachoLimit = int8(abs(theta2)) + pos;
    mUpTwo.SendToNXT();
else
    mDownTwo.ActionAtTachoLimit = 'Brake';
    mDownTwo.TachoLimit = int8(abs(theta2)) + pos;
    mDownTwo.SendToNXT();
end

% Tell Motor Three where to go
% Find current position - account for errors, i.e. if pos is not 0
data = mUpThree.ReadFromNXT();
pos  = data.Position;
    
% Decide to move up or down
if theta3 < 0
    mUpThree.ActionAtTachoLimit = 'Brake';
    mUpThree.TachoLimit = int8(abs(theta3)) + pos;
    mUpThree.SendToNXT();
else
    mDownThree.ActionAtTachoLimit = 'Brake';
    mDownThree.TachoLimit = int8(abs(theta3)) + pos;
    mDownThree.SendToNXT();
end

% Wait for the last motor to finish
mDownThree.WaitFor();
mDownOne.ActionAtTachoLimit = 'HoldBrake';
mDownTwo.ActionAtTachoLimit = 'HoldBrake';
mDownThree.ActionAtTachoLimit = 'HoldBrake';
    

%% Clean up
mUpOne.Stop('off');
mUpTwo.Stop('off');
mUpThree.Stop('off');
COM_CloseNXT(h);
end