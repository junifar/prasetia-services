package com.prasetia.services.finance.model.corrective

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CorrectiveSummary(
        @Id
        val id:Long,
        val jumlah_site: Long?,
        val year_project: Long?,
        val nilai_po: Double?,
        val nilai_inv: Double?,
        val realisasi_budget: Double?,
        val nilai_budget: Double?,
        val percentage:Double?,
        val persent_budget:Double?,
        val profit:Double?,
        val profit_percentage:Double?

){
    constructor(): this(0, 0, 0,
            0.0, 0.0,0.0,
            0.0, 0.0,0.0,
            0.0,0.0)
}
