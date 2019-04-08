package com.prasetia.services.finance.model.cme

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CmeSummaryYear(
        @Id
        val id: Long,
        val year_project: Long,
        val jumlah_site: Long,
        val site_cancel: Long,
        val nilai_po: Double,
        val nilai_invoice: Double,
        val nilai_budget: Double,
        val realisasi_budget: Double,
        val estimate_po: Double
){
    constructor(): this(0,0,0,
            0,0.0,0.0,
            0.0, 0.0, 0.0)
}
