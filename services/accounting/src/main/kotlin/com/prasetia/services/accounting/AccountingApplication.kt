package com.prasetia.services.accounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class AccountingApplication: SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
