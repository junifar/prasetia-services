package com.prasetia.services.accounting.repository.taxinfo

import com.prasetia.services.accounting.model.taxinfo.TaxInvoice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface TaxInvoiceRepository:CrudRepository<TaxInvoice, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".account_invoice."id") AS id,
                                "public".account_invoice."id" as invoice_id,
                                "public".account_invoice.date_invoice,
                                "public".account_invoice.nomor_faktur,
                                "public".account_invoice."name" AS invoice_no,
                                "public".res_partner."name" AS customer_name,
                                "public".account_invoice."state",
                                COALESCE(A.tax_amount, 0) AS tax_amount,
                                A."name",
                                A.tax_percentage,
                                COALESCE("public".account_invoice.amount_total) AS total,
                                COALESCE(A.subtotal_original) AS subtotal_original,
                                COALESCE(A.subtotal) AS subtotal,
                                A.tanggal_pembayaran,
                                A.bank,
                                A."ref",
                                A.voucher_id
                            FROM
                                "public".account_invoice
                                LEFT JOIN "public".res_partner ON "public".account_invoice.partner_id = "public".res_partner."id"
                                LEFT JOIN (
                                    SELECT
                                        "public".account_voucher_line.move_line_id,
                                        "public".account_voucher_line.amount_original as subtotal_original,
                                        "public".account_voucher_line.amount as subtotal,
                                        "public".account_voucher_line.tax_amount,
                                        "public".account_tax."name",
                                        "public".account_tax."amount" AS tax_percentage,
                                        "public".account_voucher."date" AS tanggal_pembayaran,
                                        "public".account_journal."name" AS bank,
                                        "public".account_move."name" AS "ref",
                                        "public".account_voucher."id" AS voucher_id,
                                        "public".account_move_line.move_id
                                    FROM
                                        "public".account_voucher_line
                                        LEFT JOIN "public".account_tax ON "public".account_voucher_line.tax_id = "public".account_tax."id"
                                        LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                        LEFT JOIN "public".account_journal ON "public".account_voucher.journal_id = "public".account_journal."id"
                                        LEFT JOIN "public".account_move ON "public".account_voucher.move_id = "public".account_move."id"
                                        LEFT JOIN "public".account_move_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                    WHERE
                                        "public".account_voucher_line.amount > 0 AND
                                        "public".account_voucher_line.move_line_id IS NOT NULL
                                ) AS A ON "public".account_invoice.move_id = A.move_id
                            WHERE
                                "public".account_invoice."type" = 'out_invoice' AND
                                "public".account_invoice."state" = 'paid'
                            ORDER BY
		                        "public".account_invoice.date_invoice ASC
                        """

        const val QUERY_BY_YEAR = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".account_invoice."id") AS id,
                                "public".account_invoice."id" as invoice_id,
                                "public".account_invoice.date_invoice,
                                "public".account_invoice.nomor_faktur,
                                "public".account_invoice."name" AS invoice_no,
                                "public".res_partner."name" AS customer_name,
                                "public".account_invoice."state",
                                COALESCE(A.tax_amount, 0) AS tax_amount,
                                A."name",
                                A.tax_percentage,
                                COALESCE("public".account_invoice.amount_total) AS total,
                                COALESCE(A.subtotal_original) AS subtotal_original,
                                COALESCE(A.subtotal) AS subtotal,
                                A.tanggal_pembayaran,
                                A.bank,
                                A."ref",
                                A.voucher_id
                            FROM
                                "public".account_invoice
                                LEFT JOIN "public".res_partner ON "public".account_invoice.partner_id = "public".res_partner."id"
                                LEFT JOIN (
                                    SELECT
                                        "public".account_voucher_line.move_line_id,
                                        "public".account_voucher_line.amount_original as subtotal_original,
                                        "public".account_voucher_line.amount as subtotal,
                                        "public".account_voucher_line.tax_amount,
                                        "public".account_tax."name",
                                        "public".account_tax."amount" AS tax_percentage,
                                        "public".account_voucher."date" AS tanggal_pembayaran,
                                        "public".account_journal."name" AS bank,
                                        "public".account_move."name" AS "ref",
                                        "public".account_voucher."id" AS voucher_id,
                                        "public".account_move_line.move_id
                                    FROM
                                        "public".account_voucher_line
                                        LEFT JOIN "public".account_tax ON "public".account_voucher_line.tax_id = "public".account_tax."id"
                                        LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                        LEFT JOIN "public".account_journal ON "public".account_voucher.journal_id = "public".account_journal."id"
                                        LEFT JOIN "public".account_move ON "public".account_voucher.move_id = "public".account_move."id"
                                        LEFT JOIN "public".account_move_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                    WHERE
                                        "public".account_voucher_line.amount > 0 AND
                                        "public".account_voucher_line.move_line_id IS NOT NULL
                                ) AS A ON "public".account_invoice.move_id = A.move_id
                            WHERE
                                "public".account_invoice."type" = 'out_invoice' AND
                                "public".account_invoice."state" = 'paid' AND
                                EXTRACT(year from A.tanggal_pembayaran) = :tahun
                            ORDER BY
		                        "public".account_invoice.date_invoice ASC
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getTaxInvoice():Iterable<TaxInvoice>

    @Async
    @Query(QUERY_BY_YEAR, nativeQuery = true)
    fun getTaxInvoiceByYear(@PathParam("tahun") tahun:Long):Iterable<TaxInvoice>
}
