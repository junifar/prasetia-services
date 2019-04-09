package com.prasetia.services.finance.budgetproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.jmx.support.RegistrationPolicy

@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication
class BudgetProjectApplication:SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<BudgetProjectApplication>(*args)
}
