package com.prasetia.services.accounting.pojo.taxinfo

import java.util.*

class TaxInvoiceData(
        val id:Long,
        val invoice_id:Long?,
        val date_invoice:Date?,
        val nomor_faktur:String?,
        val invoice_no:String?,
        val customer_name:String?,
        val state:String?,
        val tax_amount:Float?,
        val name:String?,
        val tax_percentage:Float?,
        val total:Float?,
        val subtotal_original:Float?,
        val subtotal:Float?,
        val tanggal_pembayaran: Date?,
        val bank:String?,
        val ref:String?,
        val voucher_id:Long?
){
    constructor(): this(0, 0, null, "", "","","",0f,
            "", 0f,0f, 0f, 0f, null, "", "", 0)
}
