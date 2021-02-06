package partten;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
