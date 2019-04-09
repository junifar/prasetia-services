package com.prasetia.services.finance.budgetproject.model.cme

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class CmeInvoiceProject(
        @Id
        val id:Long,
        val project_id:Long?,
        val name:String?,
        val state:String?,
        val nilai_invoice:Double?
){
    constructor(): this(0,0,"","",0.0)
}
