package com.prasetia.services.finance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveSummary(
        @Id
        val id: Long,
        val tahun: String,
        val nilai_po: Double?,
        val nilai_penagihan: Double?,
        val nilai_budget: Double?,
        val realisasi_budget: Double,
        val laba_rugi: Double?,
        val persent_penagihan:Double?,
        val persent_budget:Double?,
        val persent_laba_rugi:Double?
){
    constructor(): this(0, "", 0.0, 0.0, 0.0, 0.0, 0.0,
            0.0,0.0,0.0)
}
