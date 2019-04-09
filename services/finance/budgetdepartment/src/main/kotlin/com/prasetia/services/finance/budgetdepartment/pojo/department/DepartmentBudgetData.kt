package com.prasetia.services.finance.budgetdepartment.pojo.department

import java.util.*

class DepartmentBudgetRealisasiData(
        val id:Long,
        val date: Date?,
        val budget_id:Long?,
        val parent_id:Long?,
        val ref:String?,
        val narration:String?,
        val budget_realisasi:Double?
){
    constructor(): this(0,null,0,0,"", "",0.0)
}

class DepartmentBudgetDetailData(
        val id:Long,
        val budget_id: Long?,
        val line_id:Long?,
        val code:String?,
        val budget_item_view:String?,
        val nilai_budget:Double?,
        val realisasi_budget:Double?,
        val persent_budget: Double?,
        var realisasi:List<DepartmentBudgetRealisasiData>?
){
    constructor(): this(0,0,0, "","",0.0,0.0,
            0.0, null)
}

class DepartmentBudgetYearData(
        val id:Long,
        val department_name:String?,
        val department_id:Long?,
        val nilai_budget:Double?,
        val realisasi_budget:Double?,
        val persent_budget: Double?,
        var department_budget: MutableList<DepartmentBudgetData>?
){
    constructor(): this(0,"", 0,0.0,
            0.0,0.0, null)
}

class DepartmentBudgetData(
        val id:Long,
        val name:String?,
        val notes:String?,
        val periode_start: Date?,
        val periode_end: Date?,
        val nilai_budget:Double?,
        val realisasi_budget: Double?,
        val persent_budget: Double?,
        var budget_detail: MutableList<DepartmentBudgetDetailData>?
){
    constructor(): this(0,"","", null, null,0.0,0.0,0.0, null)
}
