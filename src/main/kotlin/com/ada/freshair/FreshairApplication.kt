package com.ada.freshair

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FreshairApplication

fun main(args: Array<String>) {
	runApplication<FreshairApplication>(*args)
}
