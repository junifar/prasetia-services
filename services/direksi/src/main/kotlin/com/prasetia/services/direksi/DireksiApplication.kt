package com.prasetia.services.direksi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class DireksiApplication:SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<DireksiApplication>(*args)
}
