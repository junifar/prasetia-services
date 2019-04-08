package com.prasetia.services.finance.pojo.corrective

import javax.persistence.Id

class CorrectiveCustomerSummaryData(
        @Id
        val id: Long,
        val jumlah_site: Long?,
        val year_project: Long?,
        val nilai_po: Double?,
        val nilai_inv: Double?,
        val realisasi_budget: Double?,
        val nilai_budget: Double?,
        val percentage: Double?,
        val persent_budget: Double?,
        val profit: Double?,
        val profit_precentage: Double?
){
        constructor(): this(0,0,0,
                0.0,0.0,0.0,
                0.0,0.0, 0.0,
                0.0,0.0)
}
