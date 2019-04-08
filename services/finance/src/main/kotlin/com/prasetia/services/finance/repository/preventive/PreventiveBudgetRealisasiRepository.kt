package com.prasetia.services.finance.repository.preventive

import com.prasetia.services.finance.model.preventive.PreventiveRealisasiBudget
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface PreventiveBudgetRealisasiRepository: CrudRepository<PreventiveRealisasiBudget, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            "public".budget_plan_line."id",
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id,
                            budget_plan_line_parent."name",
                            SUM(A.budget_realisasi) AS realisasi_budget,
                            "public".budget_area_detail."name" AS area_detail
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            LEFT JOIN "public".budget_plan_line AS budget_plan_line_parent ON "public".budget_plan_line.parent_id = budget_plan_line_parent."id"
                            LEFT JOIN (
                                                SELECT
                                                    AA.budget_plan_line_id AS budget_plan_line_id,
                                                    sum(AA.realisasi_debit-AA.realisasi_credit) AS budget_realisasi,
                                                    AA.ref
                                                FROM
                                                    (
                                                    --GPR INVOICE
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
                                                GROUP BY
                                                AA.budget_plan_line_id,
                                                AA.ref
                                ) AS A ON A.budget_plan_line_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".budget_area_detail ON "public".budget_plan.area_detail_id = "public".budget_area_detail."id"
                            WHERE
                            "public".project_project."state" NOT IN ('cancelled') AND
                            "public".project_project.site_type_id = 7 AND
                            "public".project_site.tahun IS NOT NULL AND
                            "public".budget_plan."state" NOT IN ('draft', 'cancel') AND
                            "public".budget_plan_line.parent_id IS NOT NULL AND
                            "public".budget_plan.budget_type_maintenance NOT IN ('stock', 'capex') AND
                            "public".project_site.customer_id = :partner_id AND
                            "public".project_site.area_id  = :area_id AND
                            "public".project_site.tahun = :tahun
                            GROUP BY
                            "public".budget_plan_line."id",
                            budget_plan_line_parent."name",
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id,
                            "public".budget_area_detail."name"
                            ORDER BY
                            CAST("public".project_site.bulan AS INTEGER) ASC
                            """

        const val QUERY_NULL = """
                            SELECT
                            "public".budget_plan_line."id",
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id,
                            budget_plan_line_parent."name",
                            SUM(A.budget_realisasi) AS realisasi_budget,
                            "public".budget_area_detail."name" AS area_detail
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            LEFT JOIN "public".budget_plan_line AS budget_plan_line_parent ON "public".budget_plan_line.parent_id = budget_plan_line_parent."id"
                            LEFT JOIN (
                                                SELECT
                                                    AA.budget_plan_line_id AS budget_plan_line_id,
                                                    sum(AA.realisasi_debit-AA.realisasi_credit) AS budget_realisasi,
                                                    AA.ref
                                                FROM
                                                    (
                                                    --GPR INVOICE
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
                                                GROUP BY
                                                AA.budget_plan_line_id,
                                                AA.ref
                                ) AS A ON A.budget_plan_line_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".budget_area_detail ON "public".budget_plan.area_detail_id = "public".budget_area_detail."id"
                            WHERE
                            "public".project_project."state" NOT IN ('cancelled') AND
                            "public".project_project.site_type_id = 7 AND
                            "public".project_site.tahun IS NOT NULL AND
                            "public".budget_plan."state" NOT IN ('draft', 'cancel') AND
                            "public".budget_plan_line.parent_id IS NOT NULL AND
                            "public".budget_plan.budget_type_maintenance NOT IN ('stock', 'capex') AND
                            "public".project_site.customer_id = :partner_id AND
                            "public".project_site.area_id IS NULL AND
                            "public".project_site.tahun = :tahun
                            GROUP BY
                            "public".budget_plan_line."id",
                            budget_plan_line_parent."name",
                            "public".project_site.area_id,
                            "public".project_site.bulan,
                            "public".project_site.tahun,
                            "public".project_site.customer_id,
                            "public".budget_area_detail."name"
                            ORDER BY
                            CAST("public".project_site.bulan AS INTEGER) ASC
                            """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getPreventiveRealisasiBudget(@PathParam("partner_id") partner_id:Int, @PathParam("tahun") tahun: String,
                                      @PathParam("area_id") area_id:Int):Iterable<PreventiveRealisasiBudget>

    @Async
    @Query(QUERY_NULL, nativeQuery = true)
    fun getPreventiveRealisasiBudgetNullArea(@PathParam("partner_id") partner_id:Int,
                                              @PathParam("tahun") tahun: String):Iterable<PreventiveRealisasiBudget>
}
