package com.example.demo.component.rpc.scan;

import com.example.demo.component.rpc.annotation.Remote;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class RpcBeanScanner extends ClassPathBeanDefinitionScanner {
    public RpcBeanScanner(BeanDefinitionRegistry registry,boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }
    public void registerFilters() {
        addIncludeFilter(new AnnotationTypeFilter(Remote.class));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }
}
