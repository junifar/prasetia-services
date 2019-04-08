package com.prasetia.services.finance.model.department

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class DepartmentYear(
        @Id
        val id:Long,
        val department_name:String?,
        val department_id:Long?,
        val nilai_budget:Double?,
        val realisasi_budget:Double?,
        val persent_budget: Double?
){
    constructor(): this(0, "",0, 0.0, 0.0, 0.0)
}
