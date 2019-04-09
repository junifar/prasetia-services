package com.prasetia.services.finance.budgetmaintenance.repository.preventive

import com.prasetia.services.finance.budgetmaintenance.model.preventive.PreventiveCustomerDetail
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface PreventiveCustomerDetailRepository: CrudRepository<PreventiveCustomerDetail, Long>{
    companion object {
        const val QUERY = """
                                SELECT
                                    ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                                    "public".res_partner."id" AS customer_id,
                                    "public".res_partner.code AS customer_name,
                                    "public".project_area."name" AS area,
                                    "public".project_area."id" AS area_id,
                                    "public".project_site.tahun
                                FROM
                                    "public".project_project
                                    LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                    LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                                    LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".project_area ON "public".project_site.area_id = "public".project_area."id"
                                WHERE
                                    "public".project_project."state" NOT IN ('cancelled') AND
                                    "public".project_project.site_type_id = 7 AND
                                    "public".project_site.tahun IS NOT NULL AND
                                    "public".res_partner."id" = :partner_id AND
                                    "public".project_site.tahun = :tahun AND
                                    "public".project_area."id" = :area_id
                                GROUP BY
                                    "public".res_partner."id",
                                    "public".res_partner.code,
                                    "public".project_area."name",
                                    "public".project_site.tahun,
                                    "public".project_area."id"
                                """

        const val QUERY_NULL = """
                                SELECT
                                    ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                                    "public".res_partner."id" AS customer_id,
                                    "public".res_partner.code AS customer_name,
                                    "public".project_area."name" AS area,
                                    "public".project_area."id" AS area_id,
                                    "public".project_site.tahun
                                FROM
                                    "public".project_project
                                    LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                    LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                                    LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".project_area ON "public".project_site.area_id = "public".project_area."id"
                                WHERE
                                    "public".project_project."state" NOT IN ('cancelled') AND
                                    "public".project_project.site_type_id = 7 AND
                                    "public".project_site.tahun IS NOT NULL AND
                                    "public".res_partner."id" = :partner_id AND
                                    "public".project_site.tahun = :tahun AND
                                    "public".project_area."id" IS NULL
                                GROUP BY
                                    "public".res_partner."id",
                                    "public".res_partner.code,
                                    "public".project_area."name",
                                    "public".project_site.tahun,
                                    "public".project_area."id"
                                """
    }


    @Async
    @Query(QUERY, nativeQuery = true)
    fun getDataByCustomerYearArea(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String,
                                  @PathParam("area_id") area_id:Int): Iterable<PreventiveCustomerDetail>

    @Async
    @Query(QUERY_NULL, nativeQuery = true)
    fun getDataByCustomerYearArea(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String): Iterable<PreventiveCustomerDetail>
}
