% Copyright 2007 The MathWorks, Inc.
function chartSquares = getReferenceValues(refFile)
referenceValues = xlsread(refFile);
L = referenceValues(:,1);
a = referenceValues(:,2);
b = referenceValues(:,3);

L = reshape(L, 4,6);
a = reshape(a, 4,6);
b = reshape(b, 4,6);
chartSquares = arrayfun(@(x,y,z) reshape([x y z], 1,1,[]), L,a,b, 'UniformOutput', false);