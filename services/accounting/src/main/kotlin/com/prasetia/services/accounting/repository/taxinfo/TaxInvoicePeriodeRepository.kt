package com.prasetia.services.accounting.repository.taxinfo

import com.prasetia.services.accounting.model.taxinfo.TaxInvoicePeriode
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository

@Repository
interface TaxInvoicePeriodeRepository:CrudRepository<TaxInvoicePeriode, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                    ROW_NUMBER() OVER (ORDER BY EXTRACT(YEAR FROM A.tanggal_pembayaran)) AS id,
                                    EXTRACT(YEAR FROM A.tanggal_pembayaran) AS YEAR,
                                    SUM(A.tax_amount) AS total_tax_amount,
                                    SUM(A.subtotal_original) AS total_original_amount,
                                    SUM(A.subtotal) as total_amount
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
                            GROUP BY
                                    EXTRACT(YEAR FROM A.tanggal_pembayaran)
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getTaxPeriodeInvoice():Iterable<TaxInvoicePeriode>


}
