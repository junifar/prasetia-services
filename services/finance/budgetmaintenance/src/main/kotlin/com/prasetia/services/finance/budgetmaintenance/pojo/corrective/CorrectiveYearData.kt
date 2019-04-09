package com.prasetia.services.finance.budgetmaintenance.pojo.corrective

import java.util.*
import javax.persistence.Id

class CorrectiveYearData(
        @Id
        val id: Long,
        val customer_id:Long,
        val code:String?,
        val jumlah_site: Long?,
        val year_project: String?,
        val nilai_po: Double?,
        val nilai_inv: Double?,
        val realisasi_budget: Double?,
        val nilai_budget: Double?,
        val percentage:Double?,
        val persent_budget:Double?,
        val profit: Double?,
        val profit_precentage: Double?,
        var projects : MutableList<CorrectiveProjectData>?
){
    constructor(): this(0,  0, "",
            0, "", 0.0,
            0.0, 0.0,
            0.0,0.0,0.0,
            0.0,0.0, null)
}

class CorrectiveProjectData(
        @Id
        val id:Long,
        val year_project:String,
        val site_name:String,
        val customer:String?,
        var budget_used: MutableList<CorrectiveBudgetUsedData>?,
        var cash_advance: MutableList<CorrectiveAdvanceData>?
){
    constructor(): this(0, "", "","",
            null, null)
}

class CorrectiveBudgetUsedData(
        @Id
        val id:Long,
        val year_project:String?,
        val project_id:Long,
        val amount:Double,
        val narration:String?,
        val ref:String?,
        val pic:String?,
        val penerima_dana:String?,
        val tanggal: Date?
){
    constructor(): this(0, "",
            0, 0.0, "",
            "", "", "", null)
}

class CorrectiveAdvanceData(
        @Id
        val id:Long,
        val year_project:String,
        val project_id:Long,
        val amount:Double,
        val narration:String?,
        val ref:String,
        val pic:String,
        val penerima_dana:String?,
        val tanggal: Date?,
        val ca_id: Long,
        val no_mi: String?,
        val no_po:String?,
        val nilai_po:Double?,
        var advance_invoice:List<CorrectiveAdvanceInvoiceData>?
){
    constructor(): this(0, "", 0,
            0.0, "","",
            "", "", null, 0,
            "", "", 0.0, null)
}

class CorrectiveAdvanceInvoiceData(
        @Id
        val id:Long,
        val nilai_invoice:Double,
        val invoice_state:String,
        val no_inv:String,
        val year_project:String
){
    constructor(): this(0, 0.0, "", "","")
}
