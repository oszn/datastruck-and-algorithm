# 求质数

一个数是质数，简单来说，除了1*它本身，没有其他的得到他的方法。

**判断是不是素数**

```java
for(int i=2;i*i<n;i++){
	if(n%i==0){
		return false;
	}
}
```

**小于n的素数**

在上面的基础上进行循环遍历即可。

其中k相当于上面的n。

```java
int[] arr = new int[max + 1];
        for (int j = 2; j * j <= max; j++) {
            for (int k = 2 * j; k <= max; k++) {
                if (k % j == 0) {
                    arr[k] = 1;
                }
            }
        }
```

