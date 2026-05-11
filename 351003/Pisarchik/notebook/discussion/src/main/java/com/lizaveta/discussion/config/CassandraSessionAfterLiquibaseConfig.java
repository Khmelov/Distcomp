package com.lizaveta.discussion.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class CassandraSessionAfterLiquibaseConfig {

    public static final String KEYSPACE_INITIALIZER_BEAN = "distcompKeyspaceInitializer";

    @Bean
    public static BeanFactoryPostProcessor cassandraSessionDependsOnLiquibase() {
        return new CassandraSessionLiquibaseOrdering();
    }

    private static final class CassandraSessionLiquibaseOrdering implements BeanFactoryPostProcessor, PriorityOrdered {

        @Override
        public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
            Set<String> liquibaseBeans = collectLiquibaseBeanNames(beanFactory);
            if (beanFactory.containsBeanDefinition(KEYSPACE_INITIALIZER_BEAN)) {
                String[] keyspaceFirst = new String[]{KEYSPACE_INITIALIZER_BEAN};
                for (String liquibaseBean : liquibaseBeans) {
                    mergeDependsOn(beanFactory, liquibaseBean, keyspaceFirst);
                }
            }
            Set<String> sessionDeps = new LinkedHashSet<>();
            if (beanFactory.containsBeanDefinition(KEYSPACE_INITIALIZER_BEAN)) {
                sessionDeps.add(KEYSPACE_INITIALIZER_BEAN);
            }
            sessionDeps.addAll(liquibaseBeans);
            if (sessionDeps.isEmpty()) {
                return;
            }
            String[] deps = sessionDeps.toArray(new String[0]);
            mergeDependsOn(beanFactory, "cassandraSession", deps);
            mergeDependsOn(beanFactory, "cqlSession", deps);
        }

        @Override
        public int getOrder() {
            return PriorityOrdered.LOWEST_PRECEDENCE;
        }

        private static Set<String> collectLiquibaseBeanNames(final ConfigurableListableBeanFactory beanFactory) {
            Set<String> liquibaseBeans = new LinkedHashSet<>();
            for (String name : beanFactory.getBeanNamesForType(SpringLiquibase.class, false, false)) {
                liquibaseBeans.add(name);
            }
            if (liquibaseBeans.isEmpty() && beanFactory.containsBeanDefinition("liquibase")) {
                liquibaseBeans.add("liquibase");
            }
            return liquibaseBeans;
        }

        private static void mergeDependsOn(
                final ConfigurableListableBeanFactory beanFactory,
                final String beanName,
                final String[] additionalDependsOn) {
            if (!beanFactory.containsBeanDefinition(beanName)) {
                return;
            }
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            Set<String> merged = new LinkedHashSet<>();
            if (bd.getDependsOn() != null) {
                for (String existing : bd.getDependsOn()) {
                    merged.add(existing);
                }
            }
            for (String d : additionalDependsOn) {
                merged.add(d);
            }
            bd.setDependsOn(merged.toArray(new String[0]));
        }
    }
}
