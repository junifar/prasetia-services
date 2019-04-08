package com.prasetia.services.finance.model.corrective

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CorrectiveSO(
        @Id
        val id:Long,
        val year_project:Long,
        val site_name:String,
        val project_id:String,
        val no_po:String,
        val nilai_po:Double,
        val nilai_invoice:Double,
        val persent_invoice:Double
){
    constructor(): this(0,0,"",
            "","",0.0,
            0.0,0.0)
}
