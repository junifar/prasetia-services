package com.prasetia.services.direksi.pojo.revenue

import javax.persistence.Id

class RevenueYearData(
        @Id
        val id:Long,
        val tahun:Int?,
        val nilai_po:Double?,
        val invoiced:Double?,
        val paid:Double?,
        val total:Double?,
        val target:Double?
){
    constructor(): this(0,0, 0.0,0.0, 0.0, 0.0, 0.0)
}
