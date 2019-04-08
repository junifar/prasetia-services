package com.prasetia.services.finance.pojo.preventive

import javax.persistence.Id

class PreventiveCustomerDetailHeader(
        @Id
        val id:Long,
        val customer_id:Long?,
        val customer_name:String?,
        val area:String?,
        val area_id:Long?,
        val tahun:String,
        var sale_order: MutableList<PreventiveSaleOrder>?,
        var invoice: MutableList<PreventiveInvoice>?,
        var budget_area: MutableList<PreventiveBudgetArea>?,
        var realisasi_budget_area: MutableList<PreventiveRealisasiBudgetArea>?
){
    constructor(): this(0, 0, "", "", 0, "", null, null, null, null)
}

class PreventiveSaleOrder(
        @Id
        val id:Long,
        val client_order_ref: String,
        val i:Long?,
        val ii:Long?,
        val iii:Long?,
        val iv:Long?,
        val v:Long?,
        val vi:Long?,
        val vii:Long?,
        val viii:Long?,
        val ix:Long?,
        val x:Long?,
        val xi:Long?,
        val xii:Long?,
        val total: Long?,
        val i_inv: Long?,
        val ii_inv: Long?,
        val iii_inv: Long?,
        val iv_inv: Long?,
        val v_inv: Long?,
        val vi_inv: Long?,
        val vii_inv: Long?,
        val viii_inv: Long?,
        val ix_inv: Long?,
        val x_inv: Long?,
        val xi_inv: Long?,
        val xii_inv: Long?,
        val i_inv_precentage: Float?,
        val ii_inv_precentage: Float?,
        val iii_inv_precentage: Float?,
        val iv_inv_precentage: Float?,
        val v_inv_precentage: Float?,
        val vi_inv_precentage: Float?,
        val vii_inv_precentage: Float?,
        val viii_inv_precentage: Float?,
        val ix_inv_precentage: Float?,
        val x_inv_precentage: Float?,
        val xi_inv_precentage: Float?,
        val xii_inv_precentage: Float?,
        var sale_order_invoice: MutableList<PreventiveSaleOrderInvoice>?
){
    constructor(): this(0, "", 0, 0,0,0,0,0,0,0,0,0,0,0, 0,
            0,0,0,0,0,0,0,0,0,0,0,0,
            0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,
            null)
}

class PreventiveInvoice(
        @Id
        val id:Long,
        val client_order_ref: String,
        val i:Long?,
        val ii:Long?,
        val iii:Long?,
        val iv:Long?,
        val v:Long?,
        val vi:Long?,
        val vii:Long?,
        val viii:Long?,
        val ix:Long?,
        val x:Long?,
        val xi:Long?,
        val xii:Long?,
        val total: Long?
){
    constructor(): this(0, "", 0, 0,0,0,0,0,0,0,0,0,0,0, 0)
}

class PreventiveBudgetArea(
        @Id
        val id:Long,
        val area_detail: String?,
        var budget: MutableList<PreventiveBudget>?
){
    constructor(): this(0, "", null)
}

class PreventiveBudget(
        @Id
        val id:Long,
        val name: String,
        val i:Long?,
        val ii:Long?,
        val iii:Long?,
        val iv:Long?,
        val v:Long?,
        val vi:Long?,
        val vii:Long?,
        val viii:Long?,
        val ix:Long?,
        val x:Long?,
        val xi:Long?,
        val xii:Long?,
        val total: Long?,
        val i_realisasi: Long?,
        val ii_realisasi: Long?,
        val iii_realisasi: Long?,
        val iv_realisasi: Long?,
        val v_realisasi: Long?,
        val vi_realisasi: Long?,
        val vii_realisasi: Long?,
        val viii_realisasi: Long?,
        val ix_realisasi: Long?,
        val x_realisasi: Long?,
        val xi_realisasi: Long?,
        val xii_realisasi: Long?,
        val i_realisasi_precentage: Float?,
        val ii_realisasi_precentage: Float?,
        val iii_realisasi_precentage: Float?,
        val iv_realisasi_precentage: Float?,
        val v_realisasi_precentage: Float?,
        val vi_realisasi_precentage: Float?,
        val vii_realisasi_precentage: Float?,
        val viii_realisasi_precentage: Float?,
        val ix_realisasi_precentage: Float?,
        val x_realisasi_precentage: Float?,
        val xi_realisasi_precentage: Float?,
        val xii_realisasi_precentage: Float?,
        var realisasi_budget: MutableList<PreventiveRealisasiBudget>?
){
    constructor(): this(0, "", 0, 0,0,0,0,0,0,0,0,0,0,0, 0,
            0,0,0,0,0,0,0,0,0,0,0,0,
            0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,
            null)
}

class PreventiveRealisasiBudgetArea(
        @Id
        val id:Long,
        val area_detail: String?,
        var realisasi_budget: MutableList<PreventiveRealisasiBudget>?
){
    constructor(): this(0, "", null)
}

class PreventiveRealisasiBudget(
        @Id
        val id:Long,
        val name: String,
        val i:Long?,
        val ii:Long?,
        val iii:Long?,
        val iv:Long?,
        val v:Long?,
        val vi:Long?,
        val vii:Long?,
        val viii:Long?,
        val ix:Long?,
        val x:Long?,
        val xi:Long?,
        val xii:Long?,
        val total: Long?
){
    constructor(): this(0, "", 0, 0,0,0,0,0,0,0,0,0,0,0, 0)
}

class PreventiveSaleOrderInvoice(
        @Id
        val id:Long,
        val client_order_ref: String,
        val state:String,
        val i:Long?,
        val ii:Long?,
        val iii:Long?,
        val iv:Long?,
        val v:Long?,
        val vi:Long?,
        val vii:Long?,
        val viii:Long?,
        val ix:Long?,
        val x:Long?,
        val xi:Long?,
        val xii:Long?,
        val i_val:String,
        val ii_val:String,
        val iii_val:String,
        val iv_val:String,
        val v_val:String,
        val vi_val:String,
        val vii_val:String,
        val viii_val:String,
        val ix_val:String,
        val x_val:String,
        val xi_val:String,
        val xii_val:String,
        val i_state:String,
        val ii_state:String,
        val iii_state:String,
        val iv_state:String,
        val v_state:String,
        val vi_state:String,
        val vii_state:String,
        val viii_state:String,
        val ix_state:String,
        val x_state:String,
        val xi_state:String,
        val xii_state:String,
        val total: Long?
){
    constructor(): this(0,"", "",0,0,0,0,
            0,0,0,0,0,0,0,0,"","",
            "","","","","","","","","","",
            "","","","","","","","","",
            "","","", 0)
}
