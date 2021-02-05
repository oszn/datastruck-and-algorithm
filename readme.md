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



# algorithm
