package com.prasetia.services.finance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveSaleOrderInvoice(
        @Id
        val id:Long,
        val client_order_ref:String,
        val order_line_id:Long,
        val bulan_po:Long?,
        val nilai_invoice:Long,
        val name:String,
        val state:String,
        val month_invoice:Long,
        val year_invoice:Long
){
    constructor(): this(0, "", 0, 0, 0, "", "", 0, 0)
}
