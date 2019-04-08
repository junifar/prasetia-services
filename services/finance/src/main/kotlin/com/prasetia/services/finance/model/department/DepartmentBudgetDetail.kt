package com.prasetia.services.finance.model.department

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class DepartmentBudgetDetail(
        @Id
        val id:Long,
        val budget_id:Long,
        val line_id:Long?,
        val code:String?,
        val budget_item_view:String?,
        val nilai_budget:Double?,
        val realisasi_budget:Double?,
        val persent_budget: Double?
){
    constructor(): this(0,0,0,"","", 0.0,0.0,0.0)
}
