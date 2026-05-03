package com.example.entitiesapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class EntitiesAppApplication

fun main(args: Array<String>) {
	runApplication<EntitiesAppApplication>(*args)
}
