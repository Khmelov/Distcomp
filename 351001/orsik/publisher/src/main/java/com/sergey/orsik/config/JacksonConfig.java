package com.sergey.orsik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Как только Spring создает какой-либо бин типа ObjectMapper, мы его перехватываем
        if (bean instanceof ObjectMapper objectMapper) {

            // И применяем наши строгие правила
            objectMapper.coercionConfigFor(LogicalType.Textual)
                    .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                    .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);
        }

        // Возвращаем бин обратно Spring'у
        return bean;
    }
}