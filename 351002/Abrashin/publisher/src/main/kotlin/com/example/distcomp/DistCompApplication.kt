package com.example.distcomp

import com.example.distcomp.service.Hello
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DistCompApplication

fun main(args: Array<String>) {
    runApplication<DistCompApplication>(*args)
}

