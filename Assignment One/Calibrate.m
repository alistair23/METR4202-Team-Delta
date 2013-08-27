% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function Calibrate()
%% Define Variables
global mUpOne;
global mDownOne;
global mUpTwo;
global mDownTwo;
global mUpThree;
global mDownThree;

%% verify that the RWTH - Mindstorms NXT toolbox is installed.
if verLessThan('RWTHMindstormsNXT', '4.01');
    error(strcat('This program requires the RWTH - Mindstorms NXT Toolbox ' ...
    ,'version 4.01 or greater. Go to http://www.mindstorms.rwth-aachen.de ' ...
    ,'and follow the installation instructions!'));
end%if


%% Prepare
COM_CloseNXT all
close all

%% Connect to NXT via USB
h = COM_OpenNXT('USB.ini');
COM_SetDefaultNXT(h);

%% Set params
power = 80;
port = [MOTOR_A; MOTOR_B; MOTOR_C];  % motorports to control the delta robot

%% Create motor objects
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

%% BREAKPOINT!
%% Manually move the motors to the origin

%% Find current position
data = mUpOne.ReadFromNXT();
theta1  = data.Position;
data = mUpTwo.ReadFromNXT();
theta2  = data.Position;
data = mUpThree.ReadFromNXT();
theta3  = data.Position;

%% BREAKPOINT! - Write down the values above

%% Return the motors to zero
MoveMotors(0, 0, 40);
MoveMotors(0, 0, 0);

% Use the forward kinematics to calculate a x, y and z offset and enter
% that into the main function

end

