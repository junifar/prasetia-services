package com.prasetia.services.finance.budgetmaintenance.repository.corrective

import com.prasetia.services.finance.budgetmaintenance.model.corrective.CorrectiveSO
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import javax.websocket.server.PathParam

interface CorrectiveSORepository:CrudRepository<CorrectiveSO, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            ROW_NUMBER() OVER (ORDER BY "public".project_site.customer_id) AS id,
                            "public".project_site.customer_id,
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                            "public".project_site."name" as site_name,
                            "public".account_analytic_account."name" as project_id,
                            "public".sale_order.client_order_ref as no_po,
                            Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                            COALESCE(SUM(A.nilai_inv), 0) as nilai_invoice,
                            COALESCE(round(cast(sum(A.nilai_inv)/sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) as numeric) * 100,2),0) AS persent_invoice
                            FROM
                            "public".sale_order_line
                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                            LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                            LEFT JOIN (
                                        SELECT
                                        "public".sale_order_line.id,
                                        Sum("public".account_invoice_line.price_subtotal) AS nilai_inv
                                        FROM
                                        "public".sale_order_line
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".project_project.site_type_id = 8 AND
                                        "public".account_invoice."state" NOT IN ('draft', 'cancel')
                                        GROUP BY
                                        "public".sale_order_line.id
                                        ) AS A ON A.id = "public".sale_order_line.id
                            WHERE
                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                            "public".project_project.site_type_id = 8 AND
                            "public".project_site.customer_id IS NOT NULL AND
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun AND
                            "public".project_site.customer_id = :customer_id
                            GROUP BY
                            "public".project_site.customer_id,
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas),
                            "public".project_site."name",
                            "public".account_analytic_account."name",
                            "public".sale_order_line.project_id,
                            "public".sale_order.client_order_ref
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveSO(@PathParam("tahun") tahun:Long, @PathParam("customer_id") customer_id:Long):
            Iterable<CorrectiveSO>
}
