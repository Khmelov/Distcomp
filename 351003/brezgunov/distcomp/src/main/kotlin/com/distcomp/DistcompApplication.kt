package com.distcomp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DistcompApplication

fun main(args: Array<String>) {
    runApplication<DistcompApplication>(*args)
}
