package com.prasetia.services.finance.budgetmaintenance.pojo.corrective

class CorrectiveSOData(
        val id:Long,
        val year_project:Long?,
        val site_name:String?,
        val project_id:String?,
        val no_po:String?,
        val nilai_po:Double?,
        val nilai_invoice:Double?,
        val persent_invoice:Double?
){
    constructor(): this(0,0,"",
            "","",0.0,
            0.0,0.0)
}

class CorrectiveBudgetData(
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
            0,"","",
            "",0.0,0.0,0.0)
}

class CorrectiveDetailYearCustomerData(
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
        var sales_order: List<CorrectiveSOData>?,
        var budget: List<CorrectiveBudgetData>?
){
    constructor(): this(0,0,"",
            0,"",0.0,
            0.0,0.0,0.0,0.0,
            0.0,0.0,0.0, null, null)
}
