package partern.single;

public class SimpleSingle {
    private static volatile SimpleSingle simpleSingle;
    public static SimpleSingle getSingle(){
//        return simpleSingle;
        if(simpleSingle!=null){
            return simpleSingle;
        }
        synchronized (SimpleSingle.class){
            if(simpleSingle==null){
                simpleSingle=new SimpleSingle();
            }
        }
        return simpleSingle;
    }
}
