package com.prasetia.services.finance.budgetmaintenance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveCustomer(
        @Id
        val id: Long,
        val tahun: String,
        val customer_name: String,
        val area: String,
        val nilai_po: Double?,
        val nilai_penagihan: Double?,
        val nilai_budget: Double?,
        val realisasi_budget: Double?,
        val laba_rugi: Double?,
        val customer_id:Long?,
        val persent_penagihan:Double?,
        val persent_budget:Double?,
        val persent_laba_rugi:Double?,
        val area_id:Long?
){
    constructor(): this(0, "", "", "", 0.0, 0.0, 0.0, 0.0,
            0.0, 0, 0.0, 0.0, 0.0, 0)
}
