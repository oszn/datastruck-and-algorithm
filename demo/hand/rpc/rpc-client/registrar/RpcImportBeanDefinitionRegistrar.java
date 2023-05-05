package com.example.demo.component.rpc.registrar;

import com.example.demo.component.rpc.annotation.Remote;
import com.example.demo.component.rpc.scan.RpcBeanScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
//https://blog.csdn.net/lichuangcsdn/article/details/89694363
public class RpcImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar , ResourceLoaderAware, BeanFactoryAware {

    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//        importingClassMetadata.getAnnotatedMethods(Remote);
        RpcBeanScanner scanner=new RpcBeanScanner(registry,false);
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(Remote.class.getName());
        scanner.setResourceLoader(resourceLoader);
        scanner.registerFilters();
        scanner.doScan("com.zoo.javalearn.ImportBeanDefinitionRegistrar");

        int a=10;
    }
}
