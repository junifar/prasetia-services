package com.prasetia.services.accounting.controller.api.taxinfo

import com.prasetia.services.accounting.pojo.taxinfo.TaxInvoiceData
import com.prasetia.services.accounting.pojo.taxinfo.TaxInvoicePeriodeData
import com.prasetia.services.accounting.repository.taxinfo.TaxInvoicePeriodeRepository
import com.prasetia.services.accounting.repository.taxinfo.TaxInvoiceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaxInfoController {

    @Autowired
    lateinit var repositoryTaxInvoiceData: TaxInvoiceRepository

    @Autowired
    lateinit var repositoryTaxInvoicePeriode: TaxInvoicePeriodeRepository

    @RequestMapping("/api/taxinfo")
    fun getTaxInvoice():MutableList<TaxInvoiceData>{
        val data = repositoryTaxInvoiceData.getTaxInvoice()
        val taxInvoiceData:MutableList<TaxInvoiceData> = mutableListOf()
        data.forEach {
            taxInvoiceData.add(TaxInvoiceData(it.id, it.invoice_id, it.date_invoice, it.nomor_faktur, it.invoice_no, it.customer_name,
                    it.state, it.tax_amount, it.name, it.tax_percentage, it.total, it.subtotal_original, it.subtotal, it.tanggal_pembayaran,
                    it.bank, it.ref, it.voucher_id))
        }
        return taxInvoiceData
    }

    @RequestMapping("/api/taxinfo/{tahun}")
    fun getTaxInvoiceTahun(@PathVariable("tahun") tahun:Long):MutableList<TaxInvoiceData>{
        val data = repositoryTaxInvoiceData.getTaxInvoiceByYear(tahun)
        val taxInvoiceData:MutableList<TaxInvoiceData> = mutableListOf()
        data.forEach {
            taxInvoiceData.add(TaxInvoiceData(it.id, it.invoice_id, it.date_invoice, it.nomor_faktur, it.invoice_no, it.customer_name,
                    it.state, it.tax_amount, it.name, it.tax_percentage, it.total, it.subtotal_original, it.subtotal, it.tanggal_pembayaran,
                    it.bank, it.ref, it.voucher_id))
        }
        return taxInvoiceData
    }

    @RequestMapping("/api/taxinfoperiode")
    fun getTaxInvoicePeriode():MutableList<TaxInvoicePeriodeData>{
        val data = repositoryTaxInvoicePeriode.getTaxPeriodeInvoice()
        val taxInvoicePeriodeData:MutableList<TaxInvoicePeriodeData> = mutableListOf()
        data.forEach {
            taxInvoicePeriodeData.add(TaxInvoicePeriodeData(it.id, it.year, it.total_tax_amount,
                    it.total_original_amount, it.total_amount))
        }
        return taxInvoicePeriodeData
    }
}
