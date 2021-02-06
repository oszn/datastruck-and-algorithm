# struct

**前缀树**

```java
public class tireTree {
    int endCount;
    int prefixCount;
    char self;
    private void _insert(String word,int index){
        if(index==word.length()){
            prefixCount++;
            endCount++;
            return;
        }
        this.prefixCount++;
        int x=word.charAt(index)-'a';
        if(sons[x]==null) {
            sons[x] = new tireTree();
            sons[x].self=word.charAt(index);
        }
        sons[x]._insert(word,index+1);

    }
    public void insert(String word){
        _insert(word,0);
    }
    tireTree[] sons;
    public tireTree(){
        sons=new tireTree[26];
        this.prefixCount=0;
        this.endCount=0;
    }

    public static void main(String[] args) {
        tireTree t=new tireTree();
        t.insert("his");
        t.insert("her");
        t.insert("hello");
        t.insert("do");
        int a=10;
        System.out.println("error");
    }
}

```

**并查集**

上个月是leetcode 并查集月，我月末2天做了2题感觉并查集也很强。

并查集是检查连通性和块问题的。如何使用结合实际

```java
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


```


# algorithm

**KMP**

KMP算法是一个字符串匹配算，我觉得核心思想是如果存在前缀和后缀相同，那么我的前缀可以直接移动到后缀上面，而不需要匹配中间不可能的字符串。所以如何求前缀和后缀相同就很重要。这个思路要分析，我一个短匹配串s长度是10的 话。我如果已经匹配到了5，那么我的前面5个肯定相同和长匹配串b。这里问题是我肯定是从前面开始比较的，那么我只要找到s的前5个字符串的前缀和自身的后缀和。就可以进行移动。如果没有就直接走过这5个继续匹配。

```java
public class KMP {
    public List<Integer> cal_next(String s){
        List<Integer> next=Arrays.asList(new Integer[s.length()]);
        next.set(0,-1);
        int k=-1;
        for(int i=1;i<s.length();i++){

            while(k>-1&&s.charAt(i)!=s.charAt(k+1)){
                k=next.get(k);
            }
            if(s.charAt(i)!=s.charAt(k+1))
                k++;
            System.out.println(i);
            next.set(i,k);
        }
        return next;
    };

    public int _KMP(String shortStr,String longStr){
        int match=0;
        List<Integer> next=cal_next(shortStr);
        int k=-1;
        for(int i=0;i<longStr.length();i++)
        {
            while (k>-1&&shortStr.charAt(k+1)!=longStr.charAt(i)){
                k=next.get(k);
            }
            if(shortStr.charAt(k+1)==longStr.charAt(i))
                k=k+1;
            if(k==shortStr.length()-1)
                return i-shortStr.length()+1;
        }
        return -1;
    };

    public static void main(String[] args) {
        KMP kmp=new KMP();
        System.out.println(kmp._KMP("ababaca","bacbababadababacambabacaddababacasdsd"));
    }
}
```