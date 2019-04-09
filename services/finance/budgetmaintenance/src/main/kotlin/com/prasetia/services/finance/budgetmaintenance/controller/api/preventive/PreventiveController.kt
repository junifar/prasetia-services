package com.prasetia.services.finance.budgetmaintenance.controller.api.preventive

import com.prasetia.services.finance.budgetmaintenance.model.preventive.PreventiveCustomer
import com.prasetia.services.finance.budgetmaintenance.pojo.preventive.PreventiveCustomerGroup
import com.prasetia.services.finance.budgetmaintenance.pojo.preventive.PreventiveCustomerGroupDetail
import com.prasetia.services.finance.budgetmaintenance.pojo.preventive.PreventiveCustomerYear
import com.prasetia.services.finance.budgetmaintenance.pojo.preventive.PreventiveSummaryData
import com.prasetia.services.finance.budgetmaintenance.repository.preventive.PreventiveCustomerRepository
import com.prasetia.services.finance.budgetmaintenance.repository.preventive.PreventiveSummaryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PreventiveController{

    @Autowired
    lateinit var repository: PreventiveCustomerRepository

    @Autowired
    lateinit var repositoryPreventiveSummary: PreventiveSummaryRepository

//    @Autowired
//    lateinit var repositorybyCustomerYearArea: PreventiveCustomerDetail

//    @RequestMapping("/api/preventive_customer1")
//    fun getAllData()= repository.getAllData()

//    @RequestMapping("/api/preventive_by_customer_year_area/{customer_id}/{tahun}/{area_id}")
//    fun getDataByCustomerYearArea(@PathVariable("customer_id") customer_id: Long, @PathVariable("tahun") tahun: String,
//                                  @PathVariable("area_id") area_id: String)

    @RequestMapping("/api/preventive_summary")
    fun getAllPreventiveSummary(): MutableList<PreventiveSummaryData>{
        val data = repositoryPreventiveSummary.getAllPreventiveSummaryData()
        val preventiveSummaryData: MutableList<PreventiveSummaryData> = mutableListOf()
        data.forEach {
            preventiveSummaryData.add((PreventiveSummaryData(it.id, it.tahun, it.nilai_po,
                    it.nilai_penagihan, it.nilai_budget, it.realisasi_budget,it.laba_rugi, it.persent_penagihan,
                    it.persent_budget, it.persent_laba_rugi)))
        }
        return preventiveSummaryData
    }

    @RequestMapping("/api/preventive_customer/{tahun}")
    fun getAllData(@PathVariable("tahun") tahun: String): MutableList<PreventiveCustomerYear> {
        val data = repository.getAllData(tahun)
        var yearGroup: MutableList<PreventiveCustomerYear> = mutableListOf()
        var id:Long = 1
        data.forEach {
            item->
            var found = false
            yearGroup.forEach {
                itemDetail ->
                if(itemDetail.tahun == item.tahun) found = true
            }

            if (!found) yearGroup.add(PreventiveCustomerYear(id++, item.tahun, getCustomerItemDetail(item.tahun, data)))
        }
        return  yearGroup
    }

    private fun getSumTotalItemDetail(data:Iterable<PreventiveCustomer>): ArrayList<Double> {
        val list = arrayListOf<Double>()
        var nilai_po = 0.0
        var nilai_penagihan = 0.0
        var nilai_budget = 0.0
        var realisasi_budget = 0.0

        data.forEach {
            nilai_po = it.nilai_po?.let { it1 -> nilai_po.plus(it1) }?:nilai_po
            nilai_penagihan = it.nilai_penagihan?.let(nilai_penagihan::plus) ?:nilai_penagihan
            nilai_budget = it.nilai_budget?.let { it1 -> nilai_budget.plus(it1) }?:nilai_budget
            realisasi_budget = it.realisasi_budget?.let { it1 -> realisasi_budget.plus(it1) }?:realisasi_budget
        }
        list.addAll(listOf(nilai_po.toDouble(), nilai_penagihan.toDouble(), nilai_budget.toDouble(), realisasi_budget))
        return list
    }

    fun getCustomerItemDetail(tahun: String, data:Iterable<PreventiveCustomer>):MutableList<PreventiveCustomerGroup>{
        val customerItemDetail:MutableList<PreventiveCustomerGroup> = mutableListOf()
        var id:Long = 1
        data.forEach {
            item->
            if(item.tahun == tahun){
                var found = false
                var nilai_po:Long = 0
                var nilai_penagihan:Long = 0
                var nilai_budget:Long = 0
                var realisasi_budget:Double = 0.0

                customerItemDetail.forEach {
                    itemDetail ->
                    if (itemDetail.customer_id == item.customer_id) found = true
                }

                if(!found){
                    val data_sum = getSumTotalItemDetail(data.filter { it.customer_id == item.customer_id })
//                    nilai_po = data.filter { it.customer_id == item.customer_id }.sumBy { it.nilai_po?.toInt()?:0 }.toLong()
//                    nilai_penagihan = data.filter { it.customer_id == item.customer_id }.sumBy { it.nilai_penagihan?.toInt()?:0 }.toLong()
//                    nilai_budget = data.filter { it.customer_id == item.customer_id }.sumBy { it.nilai_budget?.toInt()?:0 }.toLong()
//                    realisasi_budget = data.filter { it.customer_id == item.customer_id }.sumBy { it.realisasi_budget?.toInt()?:0 }.toLong()
                    nilai_po = data_sum[0].toLong()
                    nilai_penagihan = data_sum[1].toLong()
                    nilai_budget = data_sum[2].toLong()
                    realisasi_budget = data_sum[3]
                }

                if (!found) customerItemDetail.add(PreventiveCustomerGroup(id++, item.customer_name, item.customer_id, nilai_po, nilai_penagihan, nilai_budget, realisasi_budget, getCustomerSubItemDetail(tahun, item.customer_id, data)))
            }
        }
        return customerItemDetail
    }
    fun getCustomerSubItemDetail(tahun: String, customer_id: Long?, data: Iterable<PreventiveCustomer>):MutableList<PreventiveCustomerGroupDetail>{
        val customerSubItemDetail: MutableList<PreventiveCustomerGroupDetail> = mutableListOf()
        data.forEach {
            item->
            if((item.tahun == tahun) and (item.customer_id == customer_id)){
                customerSubItemDetail.add(PreventiveCustomerGroupDetail(item.id, item.area, item.nilai_po, item.nilai_penagihan, item.nilai_budget, item.realisasi_budget, item.laba_rugi, item.persent_penagihan, item.persent_budget, item.persent_laba_rugi, item.area_id))
            }
        }
        return customerSubItemDetail
    }
}
