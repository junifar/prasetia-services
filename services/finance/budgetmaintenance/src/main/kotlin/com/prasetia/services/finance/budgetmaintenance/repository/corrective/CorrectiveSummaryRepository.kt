package com.prasetia.services.finance.budgetmaintenance.repository.corrective

import com.prasetia.services.finance.budgetmaintenance.model.corrective.CorrectiveSummary
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository

@Repository
interface CorrectiveSummaryRepository:CrudRepository<CorrectiveSummary, Long>{
    companion object {

        const val QUERY = """
                            SELECT
                            ROW_NUMBER() OVER (ORDER BY EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)) AS id,
                            Count("public".project_project."id") AS jumlah_site,
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                            COALESCE(Sum(A.nilai_po),0) AS nilai_po,
                            COALESCE(Sum(B.nilai_inv),0) AS nilai_inv,
                            COALESCE(round(Sum(B.nilai_inv)/Sum(A.nilai_po)*100,2), 0) AS percentage,
                            COALESCE(sum(D.nilai_budget),0) AS nilai_budget ,
                            COALESCE(Sum(C.realisasi_budget),0) AS realisasi_budget,
                            COALESCE(Sum(B.nilai_inv),0) - COALESCE(Sum(C.realisasi_budget),0) AS profit,
                            COALESCE(CASE WHEN Sum(C.realisasi_budget) = NULL THEN 0 ELSE (Sum(B.nilai_inv) - Sum(C.realisasi_budget))/Sum(B.nilai_inv) END,0) AS profit_percentage,
                            COALESCE(round(cast(sum(C.realisasi_budget)/sum(D.nilai_budget) as numeric) * 100,2),0) AS persent_budget
                            FROM
                            "public".project_project
                            LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                            LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                            LEFT JOIN (
                                            SELECT
                                            "public".sale_order_line.project_id,
                                            Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po
                                            FROM
                                            "public".sale_order_line
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".project_project.site_type_id = 8
                                            GROUP BY
                                            "public".sale_order_line.project_id
                                    ) AS A ON "public".project_project."id" = A.project_id
                            LEFT JOIN (
                                    SELECT
                                            "public".sale_order_line.project_id,
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
                                            "public".sale_order_line.project_id
                                    ) AS B ON "public".project_project."id" = B.project_id
                            LEFT JOIN (
                                                    SELECT
                                                    "public".project_project."id",
                                                    Max(AB.budget_realisasi) AS realisasi_budget
                                                    FROM
                                                    "public".project_project
                                                    LEFT JOIN "public".budget_plan ON "public".budget_plan.project_id = "public".project_project."id"
                                                    LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                                    LEFT JOIN (
                                                            SELECT
                                                                            AA.budget_plan_line_id AS budget_plan_line_id,
                                                                            sum(AA.realisasi_debit-AA.realisasi_credit) AS budget_realisasi,
                                                                            AA.ref
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
                                                            GROUP BY
                                                            AA.budget_plan_line_id,
                                                            AA.ref
                                                    ) AS AB ON AB.budget_plan_line_id = "public".budget_plan_line."id"
                                                    WHERE
                                                    "public".project_project.site_type_id = 8
                                                    GROUP BY
                                                    "public".project_project."id"
                                    ) AS C ON "public".project_project."id" = C.id
                            LEFT JOIN (
                                            SELECT
                                            "public".budget_plan.project_id,
                                            Sum("public".budget_plan_line.amount) AS nilai_budget
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                            WHERE
                                            "public".budget_plan."state" <> 'draft' AND
                                            "public".budget_plan.project_id IS NOT NULL
                                            GROUP BY
                                            "public".budget_plan.project_id
                            ) AS D ON D.project_id = "public".project_project.id
                            WHERE
                            "public".project_project.site_type_id = 8 AND
                            "public".project_site.customer_id IS NOT NULL
                            GROUP BY
                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)
                        """

        const val QUERY_OLD = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                                "public".res_partner."id" AS customer_id,
                                "public".res_partner.code,
                                Count("public".project_project."id") AS jumlah_site,
                                "public".project_project.year_project,
                                Sum(A.nilai_po) AS nilai_po,
                                Sum(B.nilai_inv) AS nilai_inv,
                                round(Sum(B.nilai_inv)/Sum(A.nilai_po),2) AS percentage,
                                Sum(C.realisasi_budget) AS realisasi_budget,
                                Sum(B.nilai_inv) - Sum(C.realisasi_budget) AS profit,
                                CASE WHEN Sum(C.realisasi_budget) = NULL THEN 0 ELSE (Sum(B.nilai_inv) - Sum(C.realisasi_budget))/Sum(C.realisasi_budget) END AS profit_percentage
                                FROM
                                "public".project_project
                                LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                                LEFT JOIN (
                                        SELECT
                                        "public".sale_order_line.project_id,
                                        Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po
                                        FROM
                                        "public".sale_order_line
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".project_project.site_type_id = 8
                                        GROUP BY
                                        "public".sale_order_line.project_id
                                    ) AS A ON "public".project_project."id" = A.project_id
                                LEFT JOIN (
                                    SELECT
                                        "public".sale_order_line.project_id,
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
                                        "public".sale_order_line.project_id
                                    ) AS B ON "public".project_project."id" = B.project_id
                                LEFT JOIN (
                                            SELECT
                                            "public".project_project."id",
                                            Max(AB.budget_realisasi) AS realisasi_budget
                                            FROM
                                            "public".project_project
                                            LEFT JOIN "public".budget_plan ON "public".budget_plan.project_id = "public".project_project."id"
                                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
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
                                            ) AS AB ON AB.budget_plan_line_id = "public".budget_plan_line."id"
                                            WHERE
                                            "public".project_project.site_type_id = 8
                                            GROUP BY
                                            "public".project_project."id"
                                    ) AS C ON "public".project_project."id" = C.id
                                WHERE
                                "public".project_project.site_type_id = 8 AND
                                "public".project_site.customer_id IS NOT NULL
                                GROUP BY
                                "public".res_partner."id",
                                "public".res_partner.code,
                                "public".project_project.year_project
                                ORDER BY
                                "public".res_partner."id" ASC
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveSummary():Iterable<CorrectiveSummary>
}
