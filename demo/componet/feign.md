# feign

目的，学习下微服务。http调用方面。

feign相当于包装了你的请求，转化为http请求。

```java
@FeignClient(value = "localhost:8080")
public interface HelloServiceFeign {
    @RequestMapping(value = "/hello/say",method = RequestMethod.GET)
    public String sayHi(String name);
}
```

## 单feign的情况

feign相当于包装http协议。所以上面的代码相当于在组成request包。

url是localhost:8080/hello/say

method:get

其实很好理解，我调用你http服务，也会经过解析步骤后调用你sayHi函数。那我在远程调用此过程，可以屏蔽掉细节，做到一个对称的过程。



## 使用主机情况

