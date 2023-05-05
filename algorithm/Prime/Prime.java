package base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prime {
    public static int[] getPrime(int max) {
        int[] arr = new int[max + 1];
        for (int j = 2; j * j <= max; j++) {
            for (int k = 2 * j; k <= max; k++) {
                if (k % j == 0) {
                    arr[k] = 1;
                }
            }
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if (arr[i] == 0) {
                list.add(i);
            }
        }

        int[] ans = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ans[i] = list.get(i);
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getPrime(1000)));
    }
}
