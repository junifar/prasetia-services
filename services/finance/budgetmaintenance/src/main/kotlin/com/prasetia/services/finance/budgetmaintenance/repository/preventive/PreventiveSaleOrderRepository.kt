package com.prasetia.services.finance.budgetmaintenance.repository.preventive

import com.prasetia.services.finance.budgetmaintenance.model.preventive.PreventiveSaleOrder
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface PreventiveSaleOrderRepository: CrudRepository<PreventiveSaleOrder, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            "public".sale_order_line."id",
                            "public".sale_order.client_order_ref,
                            "public".sale_order_line.product_uom_qty * "public".sale_order_line.price_unit AS nilai_po,
                            "public".account_analytic_account."name" AS project_id,
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id
                            FROM
                            "public".sale_order_line
                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                            LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            WHERE
                            "public".project_project."state" NOT IN ('cancelled') AND
                            "public".project_project.site_type_id = 7 AND
                            "public".project_site.tahun IS NOT NULL AND
                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                            "public".project_site.customer_id = :partner_id AND
                            "public".project_site.area_id  = :area_id AND
                            "public".project_site.tahun = :tahun
                            ORDER BY
                             CAST("public".project_site.bulan AS INTEGER) ASC
                        """
        const val QUERY_NULL = """
                            SELECT
                            "public".sale_order_line."id",
                            "public".sale_order.client_order_ref,
                            "public".sale_order_line.product_uom_qty * "public".sale_order_line.price_unit AS nilai_po,
                            "public".account_analytic_account."name" AS project_id,
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id
                            FROM
                            "public".sale_order_line
                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                            LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            WHERE
                            "public".project_project."state" NOT IN ('cancelled') AND
                            "public".project_project.site_type_id = 7 AND
                            "public".project_site.tahun IS NOT NULL AND
                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                            "public".project_site.customer_id = :partner_id AND
                            "public".project_site.area_id  IS NULL AND
                            "public".project_site.tahun = :tahun
                            ORDER BY
                             CAST("public".project_site.bulan AS INTEGER) ASC
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getPreventiveSaleOrder(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String,
                               @PathParam("area_id") area_id:Int): Iterable<PreventiveSaleOrder>

    @Async
    @Query(QUERY_NULL, nativeQuery = true)
    fun getPreventiveSaleOrderNullArea(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String): Iterable<PreventiveSaleOrder>
}
