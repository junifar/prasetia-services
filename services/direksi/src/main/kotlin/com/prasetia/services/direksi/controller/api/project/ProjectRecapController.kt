package com.prasetia.services.direksi.controller.api.project

import com.prasetia.services.direksi.model.project.ProjectRecap
import com.prasetia.services.direksi.model.project.ProjectRecapAging
import com.prasetia.services.direksi.pojo.project.ProjectRecapAgingData
import com.prasetia.services.direksi.pojo.project.ProjectRecapData
import com.prasetia.services.direksi.repository.project.ProjectRecapAgingRepository
import com.prasetia.services.direksi.repository.project.ProjectRecapRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectRecapController{

    @Autowired
    lateinit var repository: ProjectRecapRepository

    @Autowired
    lateinit var repositoryProjectRecapAging: ProjectRecapAgingRepository

    @RequestMapping("api/project/recap")
    fun getAllData():MutableList<ProjectRecapData>{
        val data = repository.getAllRecapData()
        val projectRecapData:MutableList<ProjectRecapData> = mutableListOf()
        data.forEach {
            projectRecapData.add(ProjectRecapData(it.id,
                    it.site_type_id,it.site_type,
                    it.po, it.invoiced,
                    it.bast, it.paid,
                    it.nilai_po, it.realisasi_budget,
                    it.invoice_open, it.invoice_paid))
        }
        return projectRecapData
    }

    @RequestMapping("api/project/recap/{site_type_filter}/{year_filter}")
    fun getAllData(@PathVariable("site_type_filter") site_type_filter:String, @PathVariable("year_filter") year_filter:String):MutableList<ProjectRecapData>{
        val siteTypeIds:MutableList<Long> = arrayListOf()
        val years:MutableList<Long> = arrayListOf()

        if(site_type_filter != "null"){
            site_type_filter.split("-").forEach {
                siteTypeIds.add(it.toLong())
            }
        }

        if(year_filter != "null"){
            year_filter.split("-").forEach {
                years.add(it.toLong())
            }
        }

        val data:Iterable<ProjectRecap> = when{
            (siteTypeIds.size > 0) and (years.size > 0) -> repository.getAllRecapDataBySiteTypeIDYear(siteTypeIds, years)
            (siteTypeIds.size > 0) and (years.size == 0) -> repository.getAllRecapDataBySiteTypeID(siteTypeIds)
            (siteTypeIds.size == 0) and (years.size > 0) -> repository.getAllRecapDataByYear(years)
            else -> repository.getAllRecapData()
        }

        val projectRecapData:MutableList<ProjectRecapData> = mutableListOf()
        data.forEach {
            projectRecapData.add(ProjectRecapData(it.id,
                    it.site_type_id,it.site_type,
                    it.po, it.invoiced,
                    it.bast, it.paid,
                    it.nilai_po, it.realisasi_budget,
                    it.invoice_open, it.invoice_paid))
        }
        return projectRecapData
    }

    @RequestMapping("api/project/recap_aging")
    fun getAllDataAging():MutableList<ProjectRecapAgingData>{
        val data = repositoryProjectRecapAging.getAllRecapAgingData()
        val projectRecapAgingData:MutableList<ProjectRecapAgingData> = mutableListOf()
        data.forEach {
            projectRecapAgingData.add((ProjectRecapAgingData(it.id, it.site_type_id, it.site_type, it.greater_60, it.between_30_60, it.less_30)))
        }
        return projectRecapAgingData
    }

    @RequestMapping("api/project/recap_aging/{site_type_filter}/{year_filter}")
    fun getAllDataAging(@PathVariable("site_type_filter") site_type_filter:String, @PathVariable("year_filter") year_filter:String):MutableList<ProjectRecapAgingData>{
        val siteTypeIds:MutableList<Long> = arrayListOf()
        val years:MutableList<Long> = arrayListOf()

        if(site_type_filter != "null"){
            site_type_filter.split("-").forEach {
                siteTypeIds.add(it.toLong())
            }
        }

        if(year_filter != "null"){
            year_filter.split("-").forEach {
                years.add(it.toLong())
            }
        }

        val data:Iterable<ProjectRecapAging> = when{
            (siteTypeIds.size > 0) and (years.size > 0) -> repositoryProjectRecapAging.getAllRecapAgingDataBySiteTypeIDYear(siteTypeIds, years)
            (siteTypeIds.size > 0) and (years.size == 0) -> repositoryProjectRecapAging.getAllRecapAgingDataBySiteTypeID(siteTypeIds)
            (siteTypeIds.size == 0) and (years.size > 0) -> repositoryProjectRecapAging.getAllRecapAgingDataByYear(years)
            else -> repositoryProjectRecapAging.getAllRecapAgingData()
        }

//        val data = repositoryProjectRecapAging.getAllRecapAgingData()
        val projectRecapAgingData:MutableList<ProjectRecapAgingData> = mutableListOf()
        data.forEach {
            projectRecapAgingData.add((ProjectRecapAgingData(it.id, it.site_type_id, it.site_type, it.greater_60, it.between_30_60, it.less_30)))
        }
        return projectRecapAgingData
    }
}
