package com.prasetia.services.finance.budgetmaintenance.model.preventive

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class PreventiveCustomerDetail(
        @Id
        val id:Long,
        val customer_id:Long?,
        val customer_name:String,
        val area:String,
        val area_id:Long?,
        val tahun:Long
){
    constructor(): this(0, 0, "", "", 0, 0)
}
