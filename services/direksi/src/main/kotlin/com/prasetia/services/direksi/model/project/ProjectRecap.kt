package com.prasetia.services.direksi.model.project

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ProjectRecap(
        @Id
        val id:Long,
        val site_type_id:Long,
        val site_type:String?,
        val po:Double?,
        val invoiced:Double?,
        val bast:Double?,
        val paid:Double?,
        val nilai_po:Double?,
        val realisasi_budget:Double?,
        val invoice_open:Double?,
        val invoice_paid:Double?
){
    constructor(): this(0,0, null, 0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0)
}
