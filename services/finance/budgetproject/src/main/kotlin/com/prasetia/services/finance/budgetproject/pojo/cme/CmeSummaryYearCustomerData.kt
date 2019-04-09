package com.prasetia.services.finance.budgetproject.pojo.cme

import javax.persistence.Id

class CmeSummaryYearCustomerData(
        @Id
        val id: Long,
        val year_project: Long,
        val jumlah_site: Long,
        val partner_id: Long?,
        val customer: String?,
        val site_cancel: Long,
        val nilai_po: Double,
        val nilai_invoice: Double,
        val nilai_budget: Double,
        val realisasi_budget: Double,
        val estimate_po: Double,
        val percentage: Double,
        val remaining_invoice: Double,
        val percentage_realization: Double,
        val profit_loss: Double,
        val percentage_profit_realization: Double,
        val percentage_profit_po: Double
){
    constructor(): this(0,0,0, 0,"",0,0.0, 0.0,0.0,
            0.0, 0.0, 0.0, 0.0, 0.0,
            0.0,0.0, 0.0)
}
