package com.prasetia.services.finance.repository.cme

import com.prasetia.services.finance.model.cme.CmeSummaryYearProjectTypeCust
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface CmeSummaryYearProjectTypeCustRepository:CrudRepository<CmeSummaryYearProjectTypeCust, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            ROW_NUMBER() OVER (ORDER BY EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas)) AS id,
                            EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                            Count("public".project_project."id") AS jumlah_site,
                            "public".project_site_type."name" AS project_type,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 1 ELSE 0 END), 0) AS site_cancel,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 0 ELSE A.nilai_po END), 0) AS nilai_po,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 0 ELSE B.nilai_invoice END), 0) AS nilai_invoice,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 0 ELSE C.nilai_budget END), 0) AS nilai_budget,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 0 ELSE D.realisasi_budget END), 0) AS realisasi_budget,
                            COALESCE(Sum(CASE WHEN "public".project_project."state" = 'cancelled' THEN 0 ELSE E.estimate_po END), 0) AS estimate_po,
                            "public".project_project.site_type_id,
                            "public".res_partner.code AS customer,
                            "public".project_site.customer_id
                            FROM
                            "public".project_project
                            LEFT JOIN (
                                            SELECT DISTINCT
                                            "public".sale_order_line.project_id,
                                            Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po
                                            FROM
                                            "public".sale_order_line
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel')
                                            GROUP BY
                                            "public".sale_order_line.project_id
                                    ) AS "a" ON "public".project_project."id" = A.project_id
                            LEFT JOIN (
                                            SELECT DISTINCT
                                            "public".sale_order_line.project_id,
                                            Sum("public".account_invoice_line.price_subtotal) AS nilai_invoice
                                            FROM
                                            "public".sale_order_line
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" NOT IN ('draft', 'cancel')
                                            GROUP BY
                                            "public".sale_order_line.project_id

                                    ) AS b ON "public".project_project."id" = B.project_id
                            LEFT JOIN (
                                            SELECT
                                            "public".budget_plan.project_id,
                                            Sum("public".budget_plan_line.amount) AS nilai_budget
                                            FROM
                                            "public".budget_plan_line
                                            LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                            WHERE
                                            "public".budget_plan."state" NOT IN ('draft', 'cancel') AND
                                            "public".budget_plan.project_id IS NOT NULL
                                            GROUP BY
                                            "public".budget_plan.project_id
                                    ) AS "c" ON "public".project_project."id" = C.project_id
                            LEFT JOIN (
                                            SELECT
                                                "public".budget_plan.project_id,
                                                Sum(AB.realisasi_budget) AS realisasi_budget
                                            FROM
                                                "public".budget_plan_line
                                            LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                            LEFT JOIN (
                                                        SELECT
                                                                        AA.budget_plan_line_id,
                                                                        AA.realisasi_debit-AA.realisasi_credit AS realisasi_budget
                                                        FROM
                                                        (
                                                            SELECT
                                                                "public".account_invoice_line.budget_item_id as budget_plan_line_id,
                                                                Sum("public".account_invoice_line.price_subtotal) AS realisasi_debit,
                                                                0 AS realisasi_credit,
                                                                '' AS narration,
                                                                "public".account_invoice."name" AS "ref"
                                                            FROM
                                                                "public".account_invoice
                                                                LEFT JOIN "public".account_invoice_line ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                                            WHERE
                                                                "public".account_invoice."type" = 'in_invoice' AND
                                                                "public".account_invoice."state" = 'paid' AND
                                                                "public".account_invoice_line.budget_item_id IS NOT NULL
                                                            GROUP BY
                                                                "public".account_invoice_line.budget_item_id,
                                                                "public".account_invoice."name"
                                                            UNION
                                                            --REIMBURSEMENT
                                                            SELECT
                                                                "public".account_voucher_line.budget_item_id AS budget_plan_line_id,
                                                                Sum("public".account_voucher_line.amount) AS realisasi_debit,
                                                                0 AS realisasi_credit,
                                                                '' AS narration,
                                                                "public".account_voucher.reference
                                                            FROM
                                                                "public".account_voucher
                                                                LEFT JOIN "public".account_voucher_line ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                                            WHERE
                                                                "public".account_voucher."type" = 'reimbursement' AND
                                                                "public".account_voucher."state" = 'posted' AND
                                                                "public".account_voucher_line.budget_item_id IS NOT NULL
                                                            GROUP BY
                                                                "public".account_voucher_line.budget_item_id,
                                                                "public".account_voucher.reference
                                                            UNION
                                                            SELECT
                                                                                            "public".cash_advance_line.budget_item_id AS budget_plan_line_id,
                                                                                            Sum("public".cash_advance_line.price_unit * "public".cash_advance_line.quantity) AS realisasi_debit,
                                                                                            Sum(0) AS realisasi_credit,
                                                                                            string_agg("public".cash_advance_line."name", '; ') AS narration,
                                                                                            "public".cash_advance."number" as ref
                                                            FROM
                                                                                            "public".cash_advance_line
                                                                                            LEFT JOIN "public".cash_advance ON "public".cash_advance_line.voucher_id = "public".cash_advance."id"
                                                            WHERE
                                                                                            "public".cash_advance."state" = 'close'
                                                            GROUP BY
                                                                                            "public".cash_advance_line.budget_item_id,
                                                                                            "public".cash_advance."number"
                                                            UNION
                                                            SELECT
                                                                                            "public".cash_settlement_line.budget_item_id AS budget_plan_line_id,
                                                                                            Sum("public".cash_settlement_line.price_unit * "public".cash_settlement_line.quantity) AS realisasi_debit,
                                                                                            Sum(0) AS realisasi_credit,
                                                                                            string_agg("public".cash_settlement_line."name", '; ') AS narration,
                                                                                            "public".cash_advance."number" as ref
                                                            FROM
                                                                                            "public".cash_settlement_line
                                                                                            LEFT JOIN "public".cash_settlement ON "public".cash_settlement_line.voucher_id = "public".cash_settlement."id"
                                                                                            LEFT JOIN "public".cash_advance ON "public".cash_settlement.advance_id = "public".cash_advance."id"
                                                            WHERE
                                                                                            "public".cash_advance."state" = 'lunas'
                                                            GROUP BY
                                                                                            "public".cash_settlement_line.budget_item_id,
                                                                                            "public".cash_advance."number"
                                                        ) AS AA
                                                    ) AS AB ON AB.budget_plan_line_id = "public".budget_plan_line."id"
                                                WHERE
                                                    "public".budget_plan."state" NOT IN ('draft', 'cancel') AND
                                                    "public".budget_plan.project_id IS NOT NULL
                                                GROUP BY
                                        "public".budget_plan.project_id
                                    ) AS d ON "public".project_project."id" = D.project_id
                            LEFT JOIN (
                                            SELECT
                                            "public".budget_plan.project_id,
                                            Sum("public".budget_plan.estimate_po) AS estimate_po
                                            FROM
                                            "public".budget_plan
                                            WHERE
                                            "public".budget_plan.project_id IS NOT NULL
                                            GROUP BY
                                            "public".budget_plan.project_id
                                    ) AS e ON "public".project_project."id" = E.project_id
                            LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                            WHERE
                            "public".project_project.site_type_id IN (1, 2, 61, 5, 6, 3) AND
                            "public".project_project."state" NOT IN ('draft', 'cancelled') AND
                            "public".project_project.tanggal_surat_tugas IS NOT NULL AND
                            EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun AND
                            "public".project_site_type."id" = :site_type_id
                            GROUP BY
                            EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas),
                            "public".project_site_type."name",
                            "public".project_project.site_type_id,
                            "public".res_partner.code,
                            "public".project_site.customer_id
                            ORDER BY
                            EXTRACT( YEAR FROM "public".project_project.tanggal_surat_tugas) DESC

                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCmeSummaryYearProjectTypeCust(@PathParam("tahun") tahun:Long, @PathParam("site_type_id") site_type_id: Long):Iterable<CmeSummaryYearProjectTypeCust>
}
