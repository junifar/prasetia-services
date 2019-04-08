package com.prasetia.services.finance.model.department

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class DepartmentBudget(
       @Id
       val id:Long,
       val name:String?,
       val notes:String?,
       val periode_start: Date?,
       val periode_end: Date?,
       val nilai_budget:Double?,
       val realisasi_budget: Double?,
       val persent_budget: Double?
){
    constructor(): this(0,"","",null, null, 0.0,0.0,0.0)
}
