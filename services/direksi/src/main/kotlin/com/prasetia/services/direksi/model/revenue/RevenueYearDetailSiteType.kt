package com.prasetia.services.direksi.model.revenue

import javax.persistence.Entity
import javax.persistence.Id


@Entity
class RevenueYearDetailSiteType(
       @Id
       val id:Long,
       val site_type_id:Long,
       val site_type:String,
       val jumlah_site:Int,
       val tahun:Int?,
       val nilai_po:Double?,
       val invoiced:Double?,
       val paid:Double?,
       val total:Double?,
       val target:Double?
){
 constructor(): this(0,0,"", 0,0,0.0, 0.0, 0.0, 0.0, 0.0)
}
