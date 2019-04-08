package com.prasetia.services.finance.repository.cme

import com.prasetia.services.finance.model.cme.CmeInvoiceProject
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface CmeInvoiceProjectRepository:CrudRepository<CmeInvoiceProject, Long>{
    companion object {
        const val QUERY = """
                    SELECT DISTINCT
                    ROW_NUMBER() OVER (ORDER BY "public".sale_order_line.project_id) AS id,
                    "public".sale_order_line.project_id,
                    "public".account_invoice."name",
                    "public".account_invoice."state",
                    "public".account_invoice_line.price_subtotal AS nilai_invoice
                    FROM
                    "public".sale_order_line
                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                    LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                    LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                    LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                    LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                    WHERE
                    "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                    "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                    EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun AND
                    "public".project_site_type."id" = :site_type_id
                """

        const val QUERY_BY_CUSTOMER = """
                    SELECT DISTINCT
                    ROW_NUMBER() OVER (ORDER BY "public".sale_order_line.project_id) AS id,
                    "public".sale_order_line.project_id,
                    "public".account_invoice."name",
                    "public".account_invoice."state",
                    "public".account_invoice_line.price_subtotal AS nilai_invoice
                    FROM
                    "public".sale_order_line
                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                    LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                    LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                    LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                    LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                    LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                    WHERE
                    "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                    "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                    EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun AND
                    "public".project_site.customer_id = :customer_id
                """

        const val QUERY_BY_CUSTOMER_NULL = """
                    SELECT DISTINCT
                    ROW_NUMBER() OVER (ORDER BY "public".sale_order_line.project_id) AS id,
                    "public".sale_order_line.project_id,
                    "public".account_invoice."name",
                    "public".account_invoice."state",
                    "public".account_invoice_line.price_subtotal AS nilai_invoice
                    FROM
                    "public".sale_order_line
                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                    LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                    LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                    LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                    LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                    LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                    WHERE
                    "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                    "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                    EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun AND
                    "public".project_site.customer_id IS NULL
                """
    }

    @Async
    @Query(QUERY, nativeQuery =  true)
    fun getCmeInvoiceProjectRepository(@PathParam("tahun") tahun:Long, @PathParam("site_type_id") site_type_id: Long): Iterable<CmeInvoiceProject>

    @Async
    @Query(QUERY_BY_CUSTOMER, nativeQuery =  true)
    fun getCmeInvoiceProjectCustomerRepository(@PathParam("tahun") tahun:Long, @PathParam("customer_id") customer_id: Long): Iterable<CmeInvoiceProject>

    @Async
    @Query(QUERY_BY_CUSTOMER_NULL, nativeQuery =  true)
    fun getCmeInvoiceProjectCustomerRepository(@PathParam("tahun") tahun:Long): Iterable<CmeInvoiceProject>
}
