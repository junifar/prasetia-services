package com.prasetia.services.accounting.pojo.taxinfo

class TaxInvoicePeriodeData(
        val id:Long,
        val year:Int?,
        val total_tax_amount:Float?,
        val total_original_amount:Float?,
        val total_amount:Float?
){
    constructor(): this(0, 0, 0f, 0f, 0f)
}
