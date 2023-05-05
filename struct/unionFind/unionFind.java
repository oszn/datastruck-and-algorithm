package partten;

import java.util.function.Function;

public class unionFind {
    private int[]arr;
    private int[]size;
    private int part;
    private int length;
    private int cow;
    private int row;
    public unionFind(int _cow,int _row){
        cow=_cow;
        row=_row;
        int len=cow*row;
        init(len);
    }
    public void change_size(int index,int val){
        size[index]=val;
    }
    public int get_size(int index){
        return size[index];
    }
    public unionFind(int len){
        init(len);
    }
    void init(int len){
        length=len;
        part=len;
        arr=new int[len];
        size=new int[len];
        for(int i=0;i<arr.length;i++){
            size[i]=1;
            arr[i]=i;
        }
    }
    public int twoaix_one(int x,int y){
        return x*row+y;
    }
    public int find(int x){
        return x==arr[x]?x:find(arr[x]);
    }
    public boolean union(int x,int y){
        x=find(x);
        y=find(y);
        if(x==y)
            return false;
        if(size[x]<size[y]){
            int tmp=x;
            x=y;
            y=tmp;
        }
        part--;
        size[x]+=size[y];
        size[y]=0;
        arr[y]=x;
        return true;
    }
    public boolean connect(int x,int y){
        x=find(x);
        y=find(y);
        return x==y;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }
    public static int numIslands(char[][] grid) {
        if(grid.length==0||grid[0].length==0)
            return 0;
        int cow=grid.length;
        int row=grid[0].length;
        unionFind u=new unionFind(grid.length,grid[0].length);
        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[0].length;j++){
                if(grid[i][j]=='1'){
                    if(j<row-1&&grid[i][j+1]=='1')
                    {
                        u.union(u.twoaix_one(i,j),u.twoaix_one(i,j+1));
                    }
                    if(i<cow-1&&grid[i+1][j]=='1'){
                        u.union(u.twoaix_one(i+1,j),u.twoaix_one(i,j));
                    }

                }
                if(grid[i][j]=='0')
                    u.change_size(u.twoaix_one(i,j),0);
            }
        }
        int ans=0;
        for(int i=0;i<cow*row;i++){
            if(u.find(i)!=i)
                u.change_size(i,0);
            if(u.get_size(i)!=0)
                ans+=1;
        }
        return ans;
    }
    public static void main(String[] args) {
        char [][]c={
                {'1','1','0','0','0'},{'1','1','0','0','0'},{'0','0','1','0','0'},{'0','0','0','1','1'}};
        numIslands(c);
    }
}
