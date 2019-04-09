package com.prasetia.services.finance.budgetmaintenance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveRealisasiBudget(
       @Id
       val id:Long,
       val area_id:Long?,
       val bulan:Long,
       val tahun:Long,
       val customer_id: Long?,
       val name:String,
       val realisasi_budget: Long?,
       val area_detail: String?
){
    constructor(): this(0, 0, 0, 0, 0, "", 0, "")
}
