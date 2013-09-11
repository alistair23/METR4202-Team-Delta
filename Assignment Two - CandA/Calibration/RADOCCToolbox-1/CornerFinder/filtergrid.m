function gridout=filtergrid(grid)
% FILTERGRID removes spur rows and columns existing after the grid arrangement process.
% 
% FITLERGRID processes the input grid and iteratively removes rows and
% columns until a rectangular grid is obtained.
% 
% INPUTS:
%     grid: MxNx2 matrix output by GETGRID
% 
% OUTPUTS:
%     gridout: VxWx2 matrix containing the filtered grid

gridout=grid;
while 1
    
    % remove row or column with the lowest percentage of points
    row1count=nnz(gridout(1,:,1))/size(gridout,2);
    row2count=nnz(gridout(end,:,1))/size(gridout,2);
    col1count=nnz(gridout(:,1,1))/size(gridout,1);
    col2count=nnz(gridout(:,end,1))/size(gridout,1);
    
    [mincount,indx]=min([row1count,row2count,col1count,col2count]);
    if mincount<0.5
        switch indx
            case 1
                gridout(1,:,:)=[];
            case 2
                gridout(end,:,:)=[];
            case 3                
                gridout(:,1,:)=[];
            case 4
                gridout(:,end,:)=[];
        end
    else
        break;
    end
end