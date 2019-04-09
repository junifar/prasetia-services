package com.prasetia.services.finance.budgetproject.model.cme

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CmeProjectDetail(
        @Id
        val id: Long,
        val name:String,
        val year_project: Long,
        val project_type: String,
        val project_id: String,
        val nilai_po: Double,
        val no_po: String?,
        val nilai_invoice: Double,
        val nilai_budget: Double,
        val realisasi_budget: Double,
        val estimate_po: Double,
        val customer:String?,
        val customer_id:Long?,
        val site_type_id: Long?,
        val area: String?
){
    constructor(): this(0,"",0,
            "","",0.0,
            "",0.0,0.0,
            0.0,0.0,"",
            0, 0, "")
}
