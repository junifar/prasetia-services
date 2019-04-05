package com.prasetia.services.direksi

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DireksiController{

    @RequestMapping("/direksi")
    fun index():String{
        return "Direksi"
    }
}
