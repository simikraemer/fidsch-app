package de.fidsch.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FidschApiApplication

fun main(args: Array<String>) {
	runApplication<FidschApiApplication>(*args)
}
