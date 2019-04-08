package com.prasetia.services.finance.repository.preventive

import com.prasetia.services.finance.model.preventive.PreventiveSaleOrderInvoice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface PreventiveSaleOrderInvoiceRepository:CrudRepository<PreventiveSaleOrderInvoice, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".sale_order_line."id") AS id,
                                "public".sale_order_line."id" AS order_line_id,
                                "public".sale_order.client_order_ref,
                                "public".project_site.bulan AS bulan_po,
                                A.NAME,
                                A.month_invoice,
                                A.year_invoice,
                                A.nilai_invoice,
                                A.state
                            FROM
                                "public".sale_order_line
                                LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                                LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                    LEFT JOIN (
                                                                        SELECT
                                                                        "public".sale_order_line_invoice_rel.order_line_id,
                                                                        Sum("public".account_invoice_line.price_subtotal) AS nilai_invoice,
                                                                        "public".account_invoice."name",
                                                                        "public".account_invoice."state",
                                                                        EXTRACT(MONTH FROM "public".account_invoice.date_invoice) AS month_invoice,
                                                                        EXTRACT(YEAR FROM "public".account_invoice.date_invoice) AS year_invoice
                                                                        FROM
                                                                                        "public".account_invoice_line
                                                                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                                                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                                                        WHERE
                                                                                        "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                                                                                        "public".account_invoice."type" = 'out_invoice' AND
                                                                                        "public".sale_order_line_invoice_rel.order_line_id IS NOT NULL
                                                                        GROUP BY
                                                                                        "public".sale_order_line_invoice_rel.order_line_id,
                                                                                        "public".account_invoice."name",
                                                                                        "public".account_invoice."state",
                                                                                        EXTRACT(MONTH FROM "public".account_invoice.date_invoice),
                                                                                        EXTRACT(YEAR FROM "public".account_invoice.date_invoice)
                                                                    ) AS A ON A.order_line_id = "public".sale_order_line."id"
                            WHERE
                                "public".project_project."state" NOT IN ('cancelled') AND
                                "public".project_project.site_type_id = 7 AND
                                "public".project_site.tahun IS NOT NULL AND
                                "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                A.name IS NOT NULL AND
                                "public".project_site.customer_id = :partner_id AND
                                "public".project_site.area_id  = :area_id AND
                                "public".project_site.tahun = :tahun
                            ORDER BY
                                 CAST("public".project_site.bulan AS INTEGER) ASC

                        """

        const val QUERY_NULL = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".sale_order_line."id") AS id,
                                "public".sale_order_line."id" AS order_line_id,
                                "public".sale_order.client_order_ref,
                                "public".project_site.bulan AS bulan_po,
                                A.NAME,
                                A.month_invoice,
                                A.year_invoice,
                                A.nilai_invoice,
                                A.state
                            FROM
                                "public".sale_order_line
                                LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                                LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                    LEFT JOIN (
                                                                        SELECT
                                                                        "public".sale_order_line_invoice_rel.order_line_id,
                                                                        Sum("public".account_invoice_line.price_subtotal) AS nilai_invoice,
                                                                        "public".account_invoice."name",
                                                                        "public".account_invoice."state",
                                                                        EXTRACT(MONTH FROM "public".account_invoice.date_invoice) AS month_invoice,
                                                                        EXTRACT(YEAR FROM "public".account_invoice.date_invoice) AS year_invoice
                                                                        FROM
                                                                                        "public".account_invoice_line
                                                                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                                                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                                                        WHERE
                                                                                        "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                                                                                        "public".account_invoice."type" = 'out_invoice' AND
                                                                                        "public".sale_order_line_invoice_rel.order_line_id IS NOT NULL
                                                                        GROUP BY
                                                                                        "public".sale_order_line_invoice_rel.order_line_id,
                                                                                        "public".account_invoice."name",
                                                                                        "public".account_invoice."state",
                                                                                        EXTRACT(MONTH FROM "public".account_invoice.date_invoice),
                                                                                        EXTRACT(YEAR FROM "public".account_invoice.date_invoice)
                                                                    ) AS A ON A.order_line_id = "public".sale_order_line."id"
                            WHERE
                                "public".project_project."state" NOT IN ('cancelled') AND
                                "public".project_project.site_type_id = 7 AND
                                "public".project_site.tahun IS NOT NULL AND
                                "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                A.name IS NOT NULL AND
                                "public".project_site.customer_id = :partner_id AND
                                "public".project_site.area_id IS NULL AND
                                "public".project_site.tahun = :tahun
                            ORDER BY
                                 CAST("public".project_site.bulan AS INTEGER) ASC
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getPreventiveSaleOrderInvoice(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String,
                                      @PathParam("area_id") area_id:Int): Iterable<PreventiveSaleOrderInvoice>

    @Async
    @Query(QUERY_NULL, nativeQuery = true)
    fun getPreventiveSaleOrderInvoiceNullArea(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String): Iterable<PreventiveSaleOrderInvoice>
}
