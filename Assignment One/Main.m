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

%% Prepare motor
mUpOne.Stop('off');
mUpTwo.Stop('off');
mUpThree.Stop('off');
mUpOne.ResetPosition();
mUpTwo.ResetPosition();
mUpThree.ResetPosition();

%% Create the button object
button = SENSOR_1();

%% Get the data points in terms of lego
Message = 'Enter the six points as arrays';
disp(Message);
inputCords(1, :) = input('\nOne: ');
inputCords(2, :) = input('\nTwo: ');
% inputCords(3, :) = input('\nThree: ');
% inputCords(4, :) = input('\nFour: ');
% inputCords(5, :) = input('\nFive: ');
% inputCords(6, :) = input('\nSix: ');

%% Calculate the distance
magnitude(1) = norm(inputCords(1));
magnitude(2) = norm(inputCords(2));
% magnitude(3) = norm(inputCords(3));
% magnitude(4) = norm(inputCords(4));
% magnitude(5) = norm(inputCords(5));
% magnitude(6) = norm(inputCords(6));

SortedMag = sort(magnitude);

location = ones(6, 3);

for i=1:2
    for j=1:2
        if magnitude(j) == SortedMag(i)
            location(i, :) = inputCords(j, :);
        end
    end
end

OpenSwitch(button);

for i=1:2
    
    %Calculate the points in mm
    if location(i, 1) ~= 0
        location(i, 1) = ((location(i, 1) - 1)*HLU + (HLU/2) - 167);
    end
    if location(i, 2) ~= 0 
        location(i, 2) = ((location(i, 2) - 1)*HLU + (HLU/2) - 98);
    end
    if location(i, 3) ~= 0
        location(i, 3) = ((location(i, 3) - 1)*VLU + (VLU/2) - 52);
    end
    
    % Move the motors to positive 6 VLU
    MoveMotors(currentPosition(1), currentPosition(2), 5*VLU);
    
    currentPosition(3) = 5*VLU;
    
    % Move the Motors to above the location
    MoveMotors(location(i, 1), location(i, 2), currentPosition(3));
    
    currentPosition(1) = location(i, 1);
    currentPosition(2) = location(i, 2);
    
    % Move the motors to one above the location
    MoveMotors(currentPosition(1), currentPosition(2), (location(i, 3) + VLU));
    
    tempOffset = VLU;
    k = 1;
    
    % Check to see if the block has been hit
    while GetSwitch(button) ~= true
        % Move the pen down 1/3 vertical block to hit the tower
        MoveMotors(currentPosition(1), currentPosition(2), (location(i, 3) + VLU - k*(VLU/3)));
        tempOffset = tempOffset - (VLU/3);
        k = k + 1;
    end
    
    %Update the current Position
    currentPosition(3) = currentPosition(3) + tempOffset;
    
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

