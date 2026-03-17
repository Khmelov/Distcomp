package com.example.distcomp.config

import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class JacksonConfig : WebMvcConfigurer {
    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.filterIsInstance<MappingJackson2HttpMessageConverter>().forEach {
            it.objectMapper.disable(SerializationFeature.WRAP_ROOT_VALUE)
        }
    }
}
