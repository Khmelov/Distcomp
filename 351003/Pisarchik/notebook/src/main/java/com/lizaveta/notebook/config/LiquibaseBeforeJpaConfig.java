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

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) {
        if (!beanFactory.containsBeanDefinition(ENTITY_MANAGER_FACTORY_BEAN)) {
            return;
        }
        if (!beanFactory.containsBeanDefinition(LiquibaseMigrationConfig.LIQUIBASE_RUNNER_BEAN)) {
            return;
        }
        String liquibaseBeanName = LiquibaseMigrationConfig.LIQUIBASE_RUNNER_BEAN;
        BeanDefinition emfBd = beanFactory.getBeanDefinition(ENTITY_MANAGER_FACTORY_BEAN);
        if (!(emfBd instanceof AbstractBeanDefinition)) {
            return;
        }
        AbstractBeanDefinition abd = (AbstractBeanDefinition) emfBd;
        List<String> dependsOn = new ArrayList<>();
        if (abd.getDependsOn() != null) {
            dependsOn.addAll(Arrays.asList(abd.getDependsOn()));
        }
        if (!dependsOn.contains(liquibaseBeanName)) {
            dependsOn.add(0, liquibaseBeanName);
            abd.setDependsOn(dependsOn.toArray(new String[0]));
        }
    }
}
