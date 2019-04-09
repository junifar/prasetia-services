package com.prasetia.services.finance.budgetproject.controller.api.cme

import com.prasetia.services.finance.budgetproject.model.cme.CmeInvoiceProject
import com.prasetia.services.finance.budgetproject.model.cme.CmeProjectDetail
import com.prasetia.services.finance.budgetproject.pojo.cme.*
import com.prasetia.services.finance.budgetproject.repository.cme.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CmeController{

    @Autowired
    lateinit var repositoryCmeSummary: CmeSummaryYearRepository

    @Autowired
    lateinit var repositoryCmeSummaryYearProjectType: CmeSummaryYearProjectTypeRepository

    @Autowired
    lateinit var repositoryCmeSummaryYearCustomer: CmeSummaryYearCustomerRepository

    @Autowired
    lateinit var repositoryCmeSummaryYearProjectTypeCust: CmeSummaryYearProjectTypeCustRepository

    @Autowired
    lateinit var repositoryCmeSummaryYearCustomerProjectType: CmeSummaryYearCustomerProjectTypeRepository

    @Autowired
    lateinit var repositoryCmeProjectDetail: CmeProjectDetailRepository

    @Autowired
    lateinit var repositoryCmeInvoiceProject: CmeInvoiceProjectRepository

    @Autowired
    lateinit var repositoryCmeProjectDetailCustomer: CmeProjectDetailCustomerRepository

    lateinit var cmeProjectDetailDataRepository: Iterable<CmeProjectDetail>
    lateinit var cmeProjectDetailCustomerDataRepository: Iterable<CmeProjectDetail>
    lateinit var cmeInvoiceProjectDataRepository: Iterable<CmeInvoiceProject>

    @RequestMapping("/api/project_summary_year")
    fun getSummaryCmeByYear():MutableList<CmeSummaryYearData>{
        val data = repositoryCmeSummary.getCmeSummaryYear()
        val cmeSummaryYearData:MutableList<CmeSummaryYearData> = mutableListOf()
        data.forEach {
            val percentage= if (it.nilai_po == 0.0) 0.0 else it.nilai_invoice.div(it.nilai_po)
            val remainingInvoice = it.nilai_po - it.nilai_invoice
            val percentageRealization = if (it.nilai_budget == 0.0) 0.0 else it.realisasi_budget.div(it.nilai_budget)
            val profitLoss = it.nilai_invoice - it.realisasi_budget
            val percentageProfitRealization = if (it.realisasi_budget == 0.0) 0.0 else profitLoss.div(it.realisasi_budget)
            val percentageProfitPO = if(it.nilai_po == 0.0) 0.0 else profitLoss.div(it.nilai_po)

            cmeSummaryYearData.add(CmeSummaryYearData(it.id, it.year_project, it.jumlah_site, it.site_cancel,
                    it.nilai_po, it.nilai_invoice, it.nilai_budget, it.realisasi_budget, it.estimate_po, percentage, remainingInvoice,
                    percentageRealization, profitLoss, percentageProfitRealization, percentageProfitPO))
        }
        return cmeSummaryYearData
    }

    @RequestMapping("/api/project_summary_year/{tahun}")
    fun getSummaryCmeByYearProjectType(@PathVariable("tahun") tahun:Long): MutableList<CmeSummaryYearProjectTypeData>{
        val data = repositoryCmeSummaryYearProjectType.getCmeSummaryYearProjectType(tahun)
        val cmeSummaryYearProjectTypeData:MutableList<CmeSummaryYearProjectTypeData> = mutableListOf()
        data.forEach {
            val percentage= if (it.nilai_po == 0.0) 0.0 else it.nilai_invoice.div(it.nilai_po)
            val remainingInvoice = it.nilai_po - it.nilai_invoice
            val percentageRealization = if (it.nilai_budget == 0.0) 0.0 else it.realisasi_budget.div(it.nilai_budget)
            val profitLoss = it.nilai_invoice - it.realisasi_budget
            val percentageProfitRealization = if (it.realisasi_budget == 0.0) 0.0 else profitLoss.toFloat().div(it.realisasi_budget)
            val percentageProfitPO = if(it.nilai_po == 0.0) 0.0 else profitLoss.div(it.nilai_po)

            cmeSummaryYearProjectTypeData.add(CmeSummaryYearProjectTypeData(it.id, it.year_project, it.jumlah_site, it.project_type, it.site_cancel,
                    it.nilai_po, it.nilai_invoice, it.nilai_budget, it.realisasi_budget, it.estimate_po, it.site_type_id, percentage, remainingInvoice,
                    percentageRealization, profitLoss, percentageProfitRealization, percentageProfitPO))
        }
        return cmeSummaryYearProjectTypeData
    }

    @RequestMapping("/api/project_summary_year_customer/{tahun}")
    fun getSummaryCmeByYearCustomer(@PathVariable("tahun") tahun:Long): MutableList<CmeSummaryYearCustomerData>{
        val data = repositoryCmeSummaryYearCustomer.getCmeSummaryYearCustomer(tahun)
        val cmeSummaryYearCustomerData:MutableList<CmeSummaryYearCustomerData> = mutableListOf()
        data.forEach {
            val percentage= if (it.nilai_po == 0.0) 0.0 else it.nilai_invoice.div(it.nilai_po)
            val remainingInvoice = it.nilai_po - it.nilai_invoice
            val percentageRealization = if (it.nilai_budget == 0.0) 0.0 else it.realisasi_budget.div(it.nilai_budget)
            val profitLoss = it.nilai_invoice - it.realisasi_budget
            val percentageProfitRealization = if (it.realisasi_budget == 0.0) 0.0 else profitLoss.toFloat().div(it.realisasi_budget)
            val percentageProfitPO = if(it.nilai_po == 0.0) 0.0 else profitLoss.div(it.nilai_po)

            cmeSummaryYearCustomerData.add(CmeSummaryYearCustomerData(it.id, it.year_project, it.jumlah_site, it.partner_id, it.customer,
                    it.site_cancel, it.nilai_po, it.nilai_invoice, it.nilai_budget, it.realisasi_budget, it.estimate_po, percentage, remainingInvoice, percentageRealization, profitLoss, percentageProfitRealization,
                    percentageProfitPO))
        }

        return cmeSummaryYearCustomerData
    }

    @RequestMapping("/api/project_summary_year_customer/{tahun}/{customer_id}")
    fun getSummaryCmeByYearProjectCustomerProjectType(@PathVariable("tahun") tahun:Long,
                                                      @PathVariable("customer_id") customer_id: String):
            MutableList<CmeSummaryYearCustomerProjectTypeData>{
        val data = if (customer_id != "null") repositoryCmeSummaryYearCustomerProjectType.getCmeSummaryYearProjectTypeCust(tahun,
                customer_id.toLong()) else repositoryCmeSummaryYearCustomerProjectType.getCmeSummaryYearProjectTypeCust(tahun)
        val cmeSummaryYearCustomerProjectTypeData: MutableList<CmeSummaryYearCustomerProjectTypeData> = mutableListOf()

        cmeProjectDetailCustomerDataRepository = if (customer_id != "null") repositoryCmeProjectDetailCustomer.getCmeProjectDetailCustomerRepository(tahun, customer_id.toLong()) else
            repositoryCmeProjectDetailCustomer.getCmeProjectDetailCustomerRepository(tahun)
        cmeInvoiceProjectDataRepository = if (customer_id != "null") repositoryCmeInvoiceProject.getCmeInvoiceProjectCustomerRepository(tahun, customer_id.toLong()) else
            repositoryCmeInvoiceProject.getCmeInvoiceProjectCustomerRepository(tahun)

        data.forEach {
            val percentage= if (it.nilai_po == 0.0) 0.0 else it.nilai_invoice.div(it.nilai_po)
            val remainingInvoice = it.nilai_po - it.nilai_invoice
            val percentageRealization = if (it.nilai_budget == 0.0) 0.0 else it.realisasi_budget.div(it.nilai_budget)
            val profitLoss = it.nilai_invoice - it.realisasi_budget
            val percentageProfitRealization = if (it.realisasi_budget == 0.0) 0.0 else profitLoss.div(it.realisasi_budget)
            val percentageProfitPO = if(it.nilai_po == 0.0) 0.0 else profitLoss.div(it.nilai_po)
            cmeSummaryYearCustomerProjectTypeData.add(CmeSummaryYearCustomerProjectTypeData(it.id,it.year_project,
                    it.jumlah_site, it.project_type, it.site_cancel, it.nilai_po, it.nilai_invoice, it.nilai_budget,
                    it.realisasi_budget, it.estimate_po, it.site_type_id, it.customer, it.customer_id, percentage,
                    remainingInvoice, percentageRealization, profitLoss, percentageProfitRealization, percentageProfitPO,
                    getCmeProjectDetailCustomer(tahun, it.site_type_id, if(customer_id != "null") customer_id.toLong() else null)))
        }

        return cmeSummaryYearCustomerProjectTypeData
    }

    fun getCmeProjectDetailCustomer(tahun:Long, site_type_id: Long?, customer_id: Long?): MutableList<CmeYearProjectTypeCustProjectDetailData>{
        val data = cmeProjectDetailCustomerDataRepository.filter { it.year_project == tahun }
                .filter { it.site_type_id == site_type_id }.filter { it.customer_id == customer_id }
        val cmeYearProjectTypeCustProjectDetailData: MutableList<CmeYearProjectTypeCustProjectDetailData> = mutableListOf()
        data.forEach {
                cmeYearProjectTypeCustProjectDetailData.add(CmeYearProjectTypeCustProjectDetailData(it.id,
                        it.name, it.year_project, it.project_type, it.project_id, it.nilai_po, it.no_po, it.nilai_invoice,
                        it.nilai_budget, it.realisasi_budget, it.estimate_po, it.customer, it.customer_id, it.site_type_id, it.area,
                        getCmeProjectCustomerInvoice(it.id)))
        }
        return cmeYearProjectTypeCustProjectDetailData
    }

    fun getCmeProjectCustomerInvoice(project_id:Long):MutableList<CmeInvoiceProjectData>{
        val data = cmeInvoiceProjectDataRepository.filter { it.project_id == project_id }
        val cmeInvoiceProjectDetailData: MutableList<CmeInvoiceProjectData> = mutableListOf()
        data.forEach{
            cmeInvoiceProjectDetailData.add(CmeInvoiceProjectData(it.id, it.project_id, it.name, it.state, it.nilai_invoice))
        }
        return cmeInvoiceProjectDetailData
    }

    @RequestMapping("/api/project_summary_year/{tahun}/{site_type_id}")
    fun getSummaryCmeByYearProjectTypeCust(@PathVariable("tahun") tahun:Long, @PathVariable("site_type_id") site_type_id: Long): MutableList<CmeSummaryYearProjectTypeCustData>{
        val data = repositoryCmeSummaryYearProjectTypeCust.getCmeSummaryYearProjectTypeCust(tahun, site_type_id)
        val cmeSummaryYearProjectTypeCustData:MutableList<CmeSummaryYearProjectTypeCustData> = mutableListOf()
        cmeInvoiceProjectDataRepository = repositoryCmeInvoiceProject.getCmeInvoiceProjectRepository(tahun, site_type_id)

        cmeProjectDetailDataRepository = repositoryCmeProjectDetail.getCmeProjectDetailRepository(tahun, site_type_id)

        data.forEach {
            val percentage= if (it.nilai_po == 0.0) 0.0 else it.nilai_invoice.div(it.nilai_po)
            val remainingInvoice = it.nilai_po - it.nilai_invoice
            val percentageRealization = if (it.nilai_budget == 0.0) 0.0 else it.realisasi_budget.div(it.nilai_budget)
            val profitLoss = it.nilai_invoice - it.realisasi_budget
            val percentageProfitRealization = if (it.realisasi_budget == 0.0) 0.0 else profitLoss.div(it.realisasi_budget)
            val percentageProfitPO = if(it.nilai_po == 0.0) 0.0 else profitLoss.div(it.nilai_po)
            cmeSummaryYearProjectTypeCustData.add(CmeSummaryYearProjectTypeCustData(it.id, it.year_project, it.jumlah_site, it.project_type,
                    it.site_cancel, it.nilai_po, it.nilai_invoice, it.nilai_budget, it.realisasi_budget, it.estimate_po,
                    it.site_type_id, it.customer, it.customer_id, percentage, remainingInvoice, percentageRealization,
                    profitLoss, percentageProfitRealization, percentageProfitPO, getCmeProjectDetail(tahun, site_type_id, it.customer_id)))
        }
        return cmeSummaryYearProjectTypeCustData
    }

    fun getCmeProjectDetail(tahun:Long, site_type_id: Long, customer_id: Long?): MutableList<CmeYearProjectTypeCustProjectDetailData>{
        val data = cmeProjectDetailDataRepository
        val cmeYearProjectTypeCustProjectDetailData: MutableList<CmeYearProjectTypeCustProjectDetailData> = mutableListOf()
        data.forEach {
            if((it.year_project == tahun) and (it.site_type_id == site_type_id) and (it.customer_id == customer_id))
                cmeYearProjectTypeCustProjectDetailData.add(CmeYearProjectTypeCustProjectDetailData(it.id,
                        it.name, it.year_project, it.project_type, it.project_id, it.nilai_po, it.no_po, it.nilai_invoice,
                        it.nilai_budget, it.realisasi_budget, it.estimate_po, it.customer, it.customer_id, it.site_type_id, it.area,
                        getCmeProjectInvoice(it.id)))
        }
        return cmeYearProjectTypeCustProjectDetailData
    }

    fun getCmeProjectInvoice(project_id:Long):MutableList<CmeInvoiceProjectData>{
        val data = cmeInvoiceProjectDataRepository.filter { it.project_id == project_id }
        val cmeInvoiceProjectDetailData: MutableList<CmeInvoiceProjectData> = mutableListOf()
        data.forEach{
            cmeInvoiceProjectDetailData.add(CmeInvoiceProjectData(it.id, it.project_id, it.name, it.state, it.nilai_invoice))
        }
        return cmeInvoiceProjectDetailData
    }
}
