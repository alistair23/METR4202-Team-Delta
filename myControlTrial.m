
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
port  = MOTOR_A;
dist  = 180;    % distance to move in degrees

%% Create motor objects
mUp    = NXTMotor(port, 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDown  = NXTMotor(port, 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

%% Prepare motor
mUp.Stop('off');
mUp.ResetPosition();

for j=1:3
    
	% Power level downed to 75%
	
	% SmoothStart added into motor init.
	
	% Mode changed to brake prior to sending movement to NXT
	% hold brake initiated once reached goal position.
	
	% Need to test to see if this causes slipping.
	% Should be fine since we don't have any sudden changes.
	% Initial tests 
	
    data = mUp.ReadFromNXT();
    pos  = data.Position;
    
    mDowm.ActionAtTachoLimit = 'Brake';
    mDown.TachoLimit = dist + pos;
    mDown.SendToNXT();
    mDown.WaitFor();
    mDowm.ActionAtTachoLimit = 'HoldBrake';
	
    data = mUp.ReadFromNXT(); % doesn't matter which object we use to read!
    pos  = data.Position;
    
    mDowm.ActionAtTachoLimit = 'Brake';
    mUp.TachoLimit = abs(pos);
    mUp.SendToNXT();
    mUp.WaitFor();
    mDowm.ActionAtTachoLimit = 'HoldBrake';
    
end%for

% mode was HOLDBRAKE, so don't forget this:
mUp.Stop('off');
COM_CloseNXT(h);