% Copyright 2007 The MathWorks, Inc.
function adjustedImage = nonopt_fit(image, observedRed, observedGreen,...
                            observedBlue,chartRed, chartGreen, chartBlue)
% This function performs cubic spline interpolant curve fitting and applies
% it to the input data in "image".

method = 'splineinterp';
redOut = fit(observedRed, chartRed/2^8, method);
greenOut = fit(observedGreen, chartGreen/2^8, method);
blueOut = fit(observedBlue, chartBlue/2^8, method);

redCurve = uint8(redOut(1:observedRed(end)));
redCurve(end+1:2^8) = redOut(observedRed(end));

greenCurve = uint8(greenOut(1:observedGreen(end)));
greenCurve(end+1:2^8) = greenOut(observedGreen(end));

blueCurve = uint8(blueOut(1:observedBlue(end)));
blueCurve(end+1:2^8) = blueOut(observedBlue(end));

% apply curves to the squares

adjustedImage(:,:,1) = intlut(image(:,:,1), redCurve);
adjustedImage(:,:,2) = intlut(image(:,:,2), greenCurve);
adjustedImage(:,:,3) = intlut(image(:,:,3), blueCurve);