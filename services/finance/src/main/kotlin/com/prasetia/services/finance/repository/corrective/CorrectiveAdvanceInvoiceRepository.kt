package com.prasetia.services.finance.repository.corrective

import com.prasetia.services.finance.model.corrective.CorrectiveAdvanceInvoice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import javax.websocket.server.PathParam

interface CorrectiveAdvanceInvoiceRepository:CrudRepository<CorrectiveAdvanceInvoice, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            "public".sale_order_line.project_id AS "id",
                            Sum("public".account_invoice_line.price_subtotal) AS nilai_invoice,
                            string_agg("public".account_invoice."state", ';') AS invoice_state,
                            string_agg("public".account_invoice."number", '; ') AS no_inv,
                            --"public".project_project.year_project
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project
                            FROM
                            "public".sale_order_line
                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                            WHERE
                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                            "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                            "public".project_project.site_type_id = 8 AND
                            --"public".project_project.year_project = :tahun
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun
                            GROUP BY
                            "public".sale_order_line.project_id,
                            --"public".project_project.year_project
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)
                            """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveAdvanceInvoice(@PathParam("tahun") tahun:Long): Iterable<CorrectiveAdvanceInvoice>
}
