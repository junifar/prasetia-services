package com.prasetia.services.finance.model.department

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class DepartmentBudgetRealisasi(
        @Id
        val id:Long,
        val date: Date?,
        val budget_id:Long?,
        val parent_id:Long?,
        val ref:String?,
        val narration:String?,
        val budget_realisasi:Double?
){
    constructor(): this(0,null,0,0,"","",0.0)
}
