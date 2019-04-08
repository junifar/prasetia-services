package com.prasetia.services.finance.controller.api.department

import com.prasetia.services.finance.model.department.DepartmentBudget
import com.prasetia.services.finance.model.department.DepartmentBudgetDetail
import com.prasetia.services.finance.model.department.DepartmentBudgetRealisasi
import com.prasetia.services.finance.pojo.department.*
import com.prasetia.services.finance.repository.department.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DepartmentController{

    @Autowired
    lateinit var repository: DepartmentSummaryRepository

    @Autowired
    lateinit var repositoryDepartmentYear: DepartmentYearRepository

    @Autowired
    lateinit var repositoryDepartmentBudgetRepository: DepartmentBudgetRepository

    @Autowired
    lateinit var repositoryDepartmentBudgetDetail: DepartmentBudgetDetailRepository

    @Autowired
    lateinit var repositoryDepartmentBudgetRealisasiData: DepartmentBudgetRealisasiRepository

    lateinit var departmentBudgetData: Iterable<DepartmentBudget>
    lateinit var departmentBudgetDetailData: Iterable<DepartmentBudgetDetail>
    lateinit var departmentBudgetRealisasiData: Iterable<DepartmentBudgetRealisasi>

    @RequestMapping("/api/department_summary")
    fun getDepartmentSummary():MutableList<DepartmentSummaryData>{
        val data = repository.getDepartmentSummary()
        val departmentSummaryData: MutableList<DepartmentSummaryData> = mutableListOf()
        data.forEach {
            departmentSummaryData.add((DepartmentSummaryData(it.id, it.periode, it.nilai_budget, it.realisasi_budget, it.persent_budget)))
        }
        return departmentSummaryData
    }

    @RequestMapping("/api/department_year/{tahun}")
    fun getDepartmentYear(@PathVariable("tahun") tahun:Long):MutableList<DepartmentYearData>{
        val data = repositoryDepartmentYear.getDepartmentYear(tahun)
        val departmentYearData:MutableList<DepartmentYearData> = mutableListOf()
        data.forEach {
            departmentYearData.add(DepartmentYearData(it.id, it.department_name, it.department_id, it.nilai_budget,
                    it.realisasi_budget, it.persent_budget))
        }
        return departmentYearData
    }

    fun getDepartmentBudgetRealisasiData(budget_line_id:Long?):MutableList<DepartmentBudgetRealisasiData>{
        val data = departmentBudgetRealisasiData.filter { it.parent_id == budget_line_id }
        val departmentBudgetRealisasiData: MutableList<DepartmentBudgetRealisasiData> = mutableListOf()
        data.forEach {
            departmentBudgetRealisasiData.add(DepartmentBudgetRealisasiData(it.id,it.date, it.budget_id, it.parent_id, it.ref, it.narration, it.budget_realisasi))
        }
        return departmentBudgetRealisasiData
    }

    fun getDepartmentBudgetDetailData(budget_id:Long):MutableList<DepartmentBudgetDetailData>{
        val data = departmentBudgetDetailData.filter { it.budget_id == budget_id }
        val departmentBudgetDetailData: MutableList<DepartmentBudgetDetailData> = mutableListOf()
        data.forEach {
                departmentBudgetDetailData.add(DepartmentBudgetDetailData(it.id, it.budget_id, it.line_id, it.code, it.budget_item_view,
                        it.nilai_budget, it.realisasi_budget, it.persent_budget, getDepartmentBudgetRealisasiData(it.line_id)))
        }
        return departmentBudgetDetailData
    }

    fun getDepartmentBudgetData(): MutableList<DepartmentBudgetData> {
        val data = departmentBudgetData
        val departmentBudgetData: MutableList<DepartmentBudgetData> = mutableListOf()
        data.forEach {
            departmentBudgetData.add(DepartmentBudgetData(it.id, it.name, it.notes, it.periode_start, it.periode_end, it.nilai_budget, it.realisasi_budget,
                    it.persent_budget, getDepartmentBudgetDetailData(it.id)))
        }
        return departmentBudgetData
    }

    @RequestMapping("/api/department_budget/{tahun}/{department_id}")
    fun getDepartmentBudget(@PathVariable("tahun") tahun:Long, @PathVariable("department_id") department_id:Long):MutableList<DepartmentBudgetYearData>{
        val data = repositoryDepartmentYear.getDepartmentYearDeptID(tahun, department_id)
        departmentBudgetData = repositoryDepartmentBudgetRepository.getDepartmentBudget(tahun, department_id)
        departmentBudgetDetailData = repositoryDepartmentBudgetDetail.getDepartmentBudgetDetail(tahun, department_id)
        departmentBudgetRealisasiData = repositoryDepartmentBudgetRealisasiData.getDepartmentBudgetRealisasi(tahun, department_id)
        val departmentBudgetYearData: MutableList<DepartmentBudgetYearData> = mutableListOf()
        data.forEach {
            departmentBudgetYearData.add(DepartmentBudgetYearData(it.id, it.department_name,it.department_id,
                    it.nilai_budget, it.realisasi_budget, it.persent_budget, getDepartmentBudgetData()))
        }
        return departmentBudgetYearData
    }


}
