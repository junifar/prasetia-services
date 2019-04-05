package com.prasetia.services.direksi.model.revenue

import javax.persistence.Entity
import javax.persistence.Id


@Entity
class RevenueYear(
       @Id
       val id:Long,
       val tahun:Int?,
       val nilai_po:Double?,
       val invoiced:Double?,
       val paid:Double?,
       val total:Double?,
       val target:Double?
){
 constructor(): this(0,0,0.0,0.0, 0.0, 0.0, 0.0)
}
