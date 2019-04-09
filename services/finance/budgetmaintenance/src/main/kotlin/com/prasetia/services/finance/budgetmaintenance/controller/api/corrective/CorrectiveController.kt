package com.prasetia.services.finance.budgetmaintenance.controller.api.corrective

import com.prasetia.services.finance.budgetmaintenance.model.corrective.*
import com.prasetia.services.finance.budgetmaintenance.pojo.corrective.*
import com.prasetia.services.finance.budgetmaintenance.repository.corrective.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CorrectiveController{

    @Autowired
    lateinit var repository: CorrectiveSummaryRepository

    @Autowired
    lateinit var repositoryCorrectiveYear: CorrectiveYearRepository

    @Autowired
    lateinit var repositoryCorrectiveProject: CorrectiveProjectRepository

    @Autowired
    lateinit var repositoryCorrectiveBudgetUsed: CorrectiveBudgetUsedRepository

    @Autowired
    lateinit var repositoryCorrectiveAdvance: CorrectiveAdvanceRepository

    @Autowired
    lateinit var repositoryCorrectiveAdvanceInvoice: CorrectiveAdvanceInvoiceRepository

    @Autowired
    lateinit var repositoryCorrectiveSO: CorrectiveSORepository

    @Autowired
    lateinit var repositoryCorrectiveBudget: CorrectiveBudgetRepository

    lateinit var correctiveProjectDataRepository: Iterable<CorrectiveProject>

    lateinit var correctiveBudgetUsedDataRepository: Iterable<CorrectiveBudgetUsed>

    lateinit var correctiveAdvanceDataRepository: Iterable<CorrectiveAdvance>

    lateinit var correctiveAdvanceInvoiceDataRepository: Iterable<CorrectiveAdvanceInvoice>

    lateinit var correctiveSODataRepository: Iterable<CorrectiveSO>

    lateinit var correctiveBudgetDataRepository: Iterable<CorrectiveBudget>

    @RequestMapping("/api/corrective_summary")
//    fun getAllData(): Iterable<CorrectiveSummary> = repository.getCorrectiveSummary()
    fun getAllData(): MutableList<CorrectiveCustomerSummaryData>{
        val data = repository.getCorrectiveSummary()
        val correctiveCustomerSummaryData:MutableList<CorrectiveCustomerSummaryData> = mutableListOf()
        data.forEach {
            correctiveCustomerSummaryData.add(CorrectiveCustomerSummaryData(it.id, it.jumlah_site,
                    it.year_project, it.nilai_po, it.nilai_inv, it.realisasi_budget, it.nilai_budget, it.percentage, it.persent_budget, it.profit,
                    it.profit_percentage))
        }
        return correctiveCustomerSummaryData
    }


    fun getCorrectiveProject(tahun:String, customer_id:Long?): MutableList<CorrectiveProjectData>{
//        val data = repositoryCorrectiveProject.getCorrectiveProject(tahun)
        val data = correctiveProjectDataRepository.filter { it.year_project == tahun }.
                filter { it.customer_id == customer_id }
        val correctiveProjectData: MutableList<CorrectiveProjectData> = mutableListOf()
        data.forEach {
            correctiveProjectData.add(CorrectiveProjectData(it.id, it.year_project, it.site_name,
                    it.customer, getCorrectiveBudgetUsed(tahun, it.id), getCorrectiveAdvance(tahun, it.id)))
        }
        return correctiveProjectData
    }

//    fun getCorrectiveProject(tahun:String, customer:String?): MutableList<CorrectiveProjectData>{
////        val data = repositoryCorrectiveProject.getCorrectiveProject(tahun)
//        val data = correctiveProjectDataRepository
//        val correctiveProjectData: MutableList<CorrectiveProjectData> = mutableListOf()
//        data.forEach {
//            if((it.year_project == tahun) and (it.customer == customer))
//                correctiveProjectData.add(CorrectiveProjectData(it.id, it.year_project, it.site_name,
//                        it.customer, getCorrectiveBudgetUsed(tahun, it.id), getCorrectiveAdvance(tahun, it.id)))
//        }
//        return correctiveProjectData
//    }

    fun getCorrectiveBudgetUsed(tahun:String, project_id:Long): MutableList<CorrectiveBudgetUsedData>{
//        val data = repositoryCorrectiveBudgetUsed.getCorrectiveBudgetUsed(tahun)
        val data = correctiveBudgetUsedDataRepository
        val correctiveBudgetUsedData: MutableList<CorrectiveBudgetUsedData> = mutableListOf()
        data.forEach {
            if((it.year_project == tahun) and (it.project_id == project_id))
                correctiveBudgetUsedData.add(CorrectiveBudgetUsedData(it.id, it.year_project, it.project_id,
                        it.amount, it.narration, it.ref, it.pic, it.penerima_dana, it.tanggal))
        }
        return correctiveBudgetUsedData
    }

    fun getCorrectiveAdvanceInvoice(tahun: String, project_id: Long):MutableList<CorrectiveAdvanceInvoiceData>{
        val data = correctiveAdvanceInvoiceDataRepository
        val correctiveAdvanceInvoiceData:MutableList<CorrectiveAdvanceInvoiceData> = mutableListOf()
        data.forEach {
            if((it.year_project == tahun) and (it.id == project_id))
                correctiveAdvanceInvoiceData.add(CorrectiveAdvanceInvoiceData(it.id, it.nilai_invoice, it.invoice_state, it.no_inv, it.year_project))
        }
        return correctiveAdvanceInvoiceData
    }

    fun getCorrectiveAdvance(tahun: String, project_id: Long): MutableList<CorrectiveAdvanceData>{
        val data = correctiveAdvanceDataRepository
        val correctiveAdvanceData: MutableList<CorrectiveAdvanceData> = mutableListOf()
        data.forEach {
            if((it.year_project == tahun) and (it.project_id == project_id))
                correctiveAdvanceData.add(CorrectiveAdvanceData(it.id, it.year_project, it.project_id, it.amount,
                        it.narration, it.ref, it.pic, it.penerima_dana, it.tanggal, it.ca_id, it.no_mi, it.no_po,
                        it.nilai_po, getCorrectiveAdvanceInvoice(tahun, project_id)))
        }
        return correctiveAdvanceData
    }

    @RequestMapping("/api/corrective_year/{tahun}")
    fun getDetailData(@PathVariable("tahun") tahun:Long): MutableList<CorrectiveYearData> {
        val data = repositoryCorrectiveYear.getCorrectiveYear(tahun)
        correctiveProjectDataRepository = repositoryCorrectiveProject.getCorrectiveProject(tahun)
        correctiveBudgetUsedDataRepository = repositoryCorrectiveBudgetUsed.getCorrectiveBudgetUsed(tahun)
        correctiveAdvanceDataRepository = repositoryCorrectiveAdvance.getCorrectiveAdvance(tahun)
        correctiveAdvanceInvoiceDataRepository = repositoryCorrectiveAdvanceInvoice.getCorrectiveAdvanceInvoice(tahun)
        val correctiveYearData:MutableList<CorrectiveYearData> = mutableListOf()
        data.forEach {
            correctiveYearData.add((CorrectiveYearData(it.id, it.customer_id, it.code, it.jumlah_site, it.year_project,
                    it.nilai_po, it.nilai_inv, it.realisasi_budget, it.nilai_budget, it.percentage, it.persent_budget,
                    it.profit, it.profit_percentage, getCorrectiveProject(tahun.toString(), it.customer_id))))
        }
        return correctiveYearData
    }

    fun getCorrectiveSO(): MutableList<CorrectiveSOData>{
        val data = correctiveSODataRepository
        val correctiveSOData: MutableList<CorrectiveSOData> = mutableListOf()
        data.forEach {
            correctiveSOData.add(CorrectiveSOData(it.id, it.year_project, it.site_name, it.project_id, it.no_po,
                    it.nilai_po, it.nilai_invoice, it.persent_invoice))
        }
        return correctiveSOData
    }

    fun getCorrectiveBudget(): MutableList<CorrectiveBudgetData>{
        val data = correctiveBudgetDataRepository
        val correctiveBudgetData: MutableList<CorrectiveBudgetData> = mutableListOf()
        data.forEach {
            correctiveBudgetData.add(CorrectiveBudgetData(it.id, it.budget_id, it.customer_id,
                    it.year_project, it.site_name, it.project_id, it.nomor_budget, it.nilai_budget,
                    it.realisasi_budget, it.persent_budget))
        }
        return correctiveBudgetData
    }

    @RequestMapping("/api/corrective_detail/{customer_id}/{tahun}")
    fun getCorrectiveDetail(@PathVariable("customer_id") customer_id:Long, @PathVariable("tahun") tahun:Long):MutableList<CorrectiveDetailYearCustomerData>{
        val data = repositoryCorrectiveYear.getCorrectiveYearCustomer(tahun, customer_id)
        correctiveSODataRepository = repositoryCorrectiveSO.getCorrectiveSO(tahun, customer_id)
        correctiveBudgetDataRepository = repositoryCorrectiveBudget.getCorrectiveBudget(tahun, customer_id)
        val correctiveDetailYearCustomerData: MutableList<CorrectiveDetailYearCustomerData> = mutableListOf()
        data.forEach {
            correctiveDetailYearCustomerData.add(CorrectiveDetailYearCustomerData(it.id,
                    it.customer_id,it.code, it.jumlah_site, it.year_project, it.nilai_po, it.nilai_inv,
                    it.realisasi_budget, it.nilai_budget, it.percentage,it.persent_budget,it.profit, it.profit_percentage, getCorrectiveSO(),
                    getCorrectiveBudget()))
        }
        return correctiveDetailYearCustomerData
    }
}
