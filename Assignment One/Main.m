% METR4202 Team Project One
% Team: Delta (Group One)
% The University of Queensland

function Main()
%% Define Variables
global mUpOne;
global mDownOne;
global mUpTwo;
global mDownTwo;
global mUpThree;
global mDownThree;

HLU = 32;
VLU = 20;
currentPosition = [0, 0, 0];

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
power = 75;
port = [MOTOR_A; MOTOR_B; MOTOR_C];  % motorports to control the delta robot

%% Create motor objects
mUpOne    = NXTMotor(port(1), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownOne  = NXTMotor(port(1), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

mUpTwo    = NXTMotor(port(2), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownTwo  = NXTMotor(port(2), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

mUpThree    = NXTMotor(port(3), 'Power',  power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);
mDownThree  = NXTMotor(port(3), 'Power', -power, 'ActionAtTachoLimit', 'Brake', 'SmoothStart', true);

%% Main Program

%Get the data points in terms of lego
Message = 'Enter the six points as arrays';
disp(Message);
location(1, :) = input('\nOne: ');
 location(2, :) = input('\nTwo: ');
% location(3, :) = input('\nThree: ');
% location(4, :) = input('\nFour: ');
% location(5, :) = input('\nFive: ');
% location(6, :) = input('\nSix: ');

for i=1:2
    
    %Calculate the points in mm
    if location(i, 1) ~= 0
        location(i, 1) = (location(i, 1) - 1)*HLU + (HLU/2);
    end
    if location(i, 2) ~= 0 
        location(i, 2) = (location(i, 2) - 1)*HLU + (HLU/2);
    end
    if location(i, 3) ~= 0
        location(i, 3) = (location(i, 3) - 1)*VLU + (VLU/2);
    end
    
    % Move the Motors
    MoveMotors(location(i, 1) - currentPosition(1), location(i, 2) - currentPosition(2), location(i, 3) - currentPosition(3));
    
    %Update the current Position
    currentPosition(1) = location(i, 1) - currentPosition(1);
    currentPosition(2) = location(i, 2) - currentPosition(2);
    currentPosition(3) = location(i, 3) - currentPosition(3);
end


%% Clean up
mUpOne.Stop('off');
mDownOne.Stop('off');
mUpTwo.Stop('off');
mDownTwo.Stop('off');
mUpThree.Stop('off');
mDownThree.Stop('off');
COM_CloseNXT(h);
end

