package com.prasetia.services.finance.budgetdepartment.repository.department

import com.prasetia.services.finance.budgetdepartment.model.department.DepartmentBudget
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import javax.websocket.server.PathParam

interface DepartmentBudgetRepository:CrudRepository<DepartmentBudget, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                            "public".budget_plan."id",
                            "public".budget_plan."name",
                            "public".budget_plan.notes,
                            "public".budget_plan.periode_start,
                            "public".budget_plan.periode_end,
                            Sum(d.nilai_budget) AS nilai_budget,
                            COALESCE(sum(C.realisasi_budget), 0) AS realisasi_budget,
                            COALESCE(round(cast(sum(C.realisasi_budget)/sum(D.nilai_budget) as numeric) * 100,2),0) AS persent_budget
                            FROM
                            "public".budget_plan
                            LEFT JOIN "public".hr_department ON "public".budget_plan.department_id = "public".hr_department."id"
                            LEFT JOIN (
                                                SELECT
                                                "public".budget_plan.id,
                                                Sum("public".budget_plan_line.amount) AS nilai_budget
                                                FROM
                                                "public".budget_plan
                                                LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                                WHERE
                                                "public".budget_plan."state" <> 'draft' AND
                                                "public".budget_plan.project_id IS NOT NULL
                                                GROUP BY
                                                "public".budget_plan.id
                                ) AS d ON D.id= "public".budget_plan."id"
                            LEFT JOIN (
                                                SELECT
                                                "public".budget_plan."id",
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
                                                                "public".cash_advance."state" = 'lunas' AND
                                                                "public".cash_settlement."state" NOT IN ('draft', 'cancel')
                                                                GROUP BY
                                                                "public".cash_settlement_line.budget_item_id,
                                                                "public".cash_advance."number"
                                                                ) AS AA
                                                                GROUP BY
                                                                AA.budget_plan_line_id,
                                                                AA.ref
                                                ) AS AB ON AB.budget_plan_line_id = "public".budget_plan_line."id"
                                                WHERE
                                                "public".project_project.site_type_id IN (36, 37, 38, 40, 41, 42, 43, 45, 46, 47, 48, 61, 64, 65, 68, 70, 74)
                                                GROUP BY
                                                "public".budget_plan."id"
                                                ) AS "c" ON "public".budget_plan."id" = C.id
                            WHERE
                            "public".budget_plan.site_type_id IN (36, 37, 38, 40, 41, 42, 43, 45, 46, 47, 48, 61, 64, 65, 68, 70, 74) AND
                            EXTRACT(YEAR from "public".budget_plan.periode_start) IS NOT NULL AND
                            EXTRACT(YEAR from "public".budget_plan.periode_start) = :tahun AND
                            "public".budget_plan."state" NOT IN ('draft', 'cancel', 'approve1') AND
                            "public".budget_plan.department_id = :department_id
                            GROUP BY
                            "public".budget_plan."id",
                            "public".budget_plan."name",
                            "public".budget_plan.notes,
                            "public".budget_plan.periode_start,
                            "public".budget_plan.periode_end
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getDepartmentBudget(@PathParam("tahun") tahun:Long, @PathParam("department_id") department_id:Long):Iterable<DepartmentBudget>
}
