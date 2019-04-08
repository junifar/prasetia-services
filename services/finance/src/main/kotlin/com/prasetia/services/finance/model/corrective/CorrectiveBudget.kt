package com.prasetia.services.finance.model.corrective

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CorrectiveBudget(
        @Id
        val id:Long,
        val budget_id:Long?,
        val customer_id:Long?,
        val year_project:Long?,
        val site_name:String?,
        val project_id:String?,
        val nomor_budget:String?,
        val nilai_budget:Double?,
        val realisasi_budget:Double?,
        val persent_budget:Double?

){
    constructor(): this(0,0,0,
            0,"","","",
            0.0,0.0,0.0)
}
