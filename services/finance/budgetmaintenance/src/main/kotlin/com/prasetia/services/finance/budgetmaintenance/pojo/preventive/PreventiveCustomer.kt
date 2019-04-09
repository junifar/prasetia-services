package com.prasetia.services.finance.budgetmaintenance.pojo.preventive

import javax.persistence.Id

class PreventiveCustomerYear(
        @Id
        val id: Long,
        var tahun: String,
        var detail: MutableList<PreventiveCustomerGroup>?
){
    constructor(): this(0,"", null)
}

class PreventiveCustomerGroup(
        @Id
        val id:Long,
        var customer: String?,
        var customer_id: Long?,
        val nilai_po: Long?,
        val nilai_penagihan: Long?,
        val nilai_budget: Long?,
        val realisasi_budget: Double?,
        var detail: MutableList<PreventiveCustomerGroupDetail>?
){
    constructor(): this(0, "", 0, 0,0,0,0.0, null)
}

class PreventiveCustomerGroupDetail(
        @Id
        val id: Long,
        val area: String?,
        val nilai_po: Double?,
        val nilai_penagihan: Double?,
        val nilai_budget: Double?,
        val realisasi_budget: Double?,
        val laba_rugi: Double?,
        val persent_penagihan:Double?,
        val persent_budget:Double?,
        val persent_laba_rugi:Double?,
        val area_id:Long?
){
    constructor(): this(0, "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)
}
