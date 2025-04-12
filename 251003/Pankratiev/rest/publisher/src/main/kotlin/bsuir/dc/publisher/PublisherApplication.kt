package bsuir.dc.publisher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PublisherApplication

fun main(args: Array<String>) {
	runApplication<PublisherApplication>(*args)
}
