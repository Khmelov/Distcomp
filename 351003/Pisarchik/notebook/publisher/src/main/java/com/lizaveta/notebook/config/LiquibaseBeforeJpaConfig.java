package com.lizaveta.notebook.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class LiquibaseBeforeJpaConfig implements BeanFactoryPostProcessor {

    private static final String ENTITY_MANAGER_FACTORY_BEAN = "entityManagerFactory";
    private static final String LIQUIBASE_BEAN = "liquibase";

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
        if (!beanFactory.containsBeanDefinition(ENTITY_MANAGER_FACTORY_BEAN)) {
            return;
        }
        if (!beanFactory.containsBeanDefinition(LIQUIBASE_BEAN)) {
            return;
        }
        BeanDefinition emfBd = beanFactory.getBeanDefinition(ENTITY_MANAGER_FACTORY_BEAN);
        if (!(emfBd instanceof AbstractBeanDefinition)) {
            return;
        }
        AbstractBeanDefinition abd = (AbstractBeanDefinition) emfBd;
        List<String> dependsOn = new ArrayList<>();
        if (abd.getDependsOn() != null) {
            dependsOn.addAll(Arrays.asList(abd.getDependsOn()));
        }
        if (!dependsOn.contains(LIQUIBASE_BEAN)) {
            dependsOn.add(0, LIQUIBASE_BEAN);
            abd.setDependsOn(dependsOn.toArray(new String[0]));
        }
    }
}
