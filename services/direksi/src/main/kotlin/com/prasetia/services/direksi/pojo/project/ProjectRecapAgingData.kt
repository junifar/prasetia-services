package com.prasetia.services.direksi.pojo.project

class ProjectRecapAgingData(
        val id:Long,
        val site_type_id:Long?,
        val site_type:String?,
        val greater_60:Long?,
        val between_30_60:Long?,
        val less_30:Long?
){
    constructor(): this(0,0, "", 0,0,0)
}
