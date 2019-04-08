package com.prasetia.services.finance.model.corrective

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CorrectiveProject(
        @Id
        val id:Long,
        val year_project:String,
        val site_name:String,
        val customer:String,
        val customer_id:Long?
){
    constructor(): this(0,"","","",0)
}
