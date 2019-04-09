package com.prasetia.services.finance.budgetmaintenance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveSaleOrder(
        @Id
        val id: Long,
        val client_order_ref: String,
        val project_id: String,
        val area_id:Long?,
        val bulan:Long,
        val tahun:Long,
        val customer_id: Long?,
        val nilai_po: Long?
){
    constructor(): this(0, "", "", 0, 0, 0, 0, 0)
}
