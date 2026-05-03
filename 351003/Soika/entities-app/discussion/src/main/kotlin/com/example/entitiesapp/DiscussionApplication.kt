package com.example.entitiesapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class DiscussionApplication

fun main(args: Array<String>) {
    runApplication<DiscussionApplication>(*args)
}