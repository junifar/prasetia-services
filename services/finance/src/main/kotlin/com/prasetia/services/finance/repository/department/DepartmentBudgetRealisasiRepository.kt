package com.prasetia.services.finance.repository.department

import com.prasetia.services.finance.model.department.DepartmentBudgetRealisasi
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import javax.websocket.server.PathParam

interface DepartmentBudgetRealisasiRepository:CrudRepository<DepartmentBudgetRealisasi, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                    ROW_NUMBER() OVER (ORDER BY "public".budget_plan_line.parent_id) AS id,
                                    AA.budget_plan_line_id AS budget_id,
                                    "public".budget_plan_line.parent_id,
                                    AA.narration,
                                    AA.ref,
                                    AA.date,
                                    sum(AA.realisasi_debit-AA.realisasi_credit) AS budget_realisasi
                            FROM
                            (
                                    SELECT
                                    "public".account_invoice_line.budget_item_id as budget_plan_line_id,
                                    "public".account_invoice.date_invoice AS date,
                                    Sum("public".account_invoice_line.price_subtotal) AS realisasi_debit,
                                    0 AS realisasi_credit,
                                    string_agg("public".account_invoice_line."name", '; ') AS narration,
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
                                    "public".account_invoice.date_invoice,
                                    "public".account_invoice."name"
                                    UNION
                                    --REIMBURSEMENT
                                    SELECT
                                    "public".account_voucher_line.budget_item_id AS budget_plan_line_id,
                                    "public".account_voucher.date_pay AS date,
                                    Sum("public".account_voucher_line.amount) AS realisasi_debit,
                                    0 AS realisasi_credit,
                                    string_agg("public".account_voucher_line."name", '; ') AS narration,
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
                                    "public".account_voucher.date_pay,
                                    "public".account_voucher.reference
                                    UNION
                                    SELECT
                                    "public".cash_advance_line.budget_item_id AS budget_plan_line_id,
                                    "public".cash_advance."date" as date,
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
                                    "public".cash_advance."number",
                                    "public".cash_advance."date"
                                    UNION
                                    SELECT
                                    "public".cash_settlement_line.budget_item_id AS budget_plan_line_id,
                                    "public".cash_advance."date" as date,
                                    Sum("public".cash_settlement_line.price_unit * "public".cash_settlement_line.quantity) AS realisasi_debit,
                                    Sum(0) AS realisasi_credit,
                                    string_agg("public".cash_settlement_line."name", '; ') AS narration,
                                    "public".cash_advance."number" as ref
                                    FROM
                                    "public".cash_settlement_line
                                    LEFT JOIN "public".cash_settlement ON "public".cash_settlement_line.voucher_id = "public".cash_settlement."id"
                                    LEFT JOIN "public".cash_advance ON "public".cash_settlement.advance_id = "public".cash_advance."id"
                                    WHERE
                                    "public".cash_advance."state" = 'lunas' AND
				                    "public".cash_settlement."state" NOT IN ('draft', 'cancel')
                                    GROUP BY
                                    "public".cash_settlement_line.budget_item_id,
                                    "public".cash_advance."number",
                                    "public".cash_advance."date"
                            ) AS AA
                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line."id" = AA.budget_plan_line_id
                            LEFT JOIN "public".budget_plan ON "public".budget_plan."id" = "public".budget_plan_line.budget_id
                            WHERE
                            EXTRACT(YEAR from "public".budget_plan.periode_start) IS NOT NULL AND
                            EXTRACT(YEAR from "public".budget_plan.periode_start) = :tahun AND
                            "public".budget_plan."state" NOT IN ('draft', 'cancel', 'approve1') AND
                            "public".budget_plan.department_id = :department_id
                            GROUP BY
                                AA.budget_plan_line_id,
                                AA.narration,
                                "public".budget_plan_line.parent_id,
                                AA.ref,
                                AA."date"
                            """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getDepartmentBudgetRealisasi(@PathParam("tahun") tahun:Long, @PathParam("department_id") department_id:Long): Iterable<DepartmentBudgetRealisasi>
}
