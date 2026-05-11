package com.example.distcomp

import org.springframework.cache.annotation.EnableCaching
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableCaching
class DistCompApplication

fun main(args: Array<String>) {
    runApplication<DistCompApplication>(*args)
}

