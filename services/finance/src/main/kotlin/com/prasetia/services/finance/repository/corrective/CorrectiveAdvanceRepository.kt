package com.prasetia.services.finance.repository.corrective

import com.prasetia.services.finance.model.corrective.CorrectiveAdvance
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface CorrectiveAdvanceRepository:CrudRepository<CorrectiveAdvance, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY A.project_id) AS id,
                                A.year_project,
                                A.project_id,
                                SUM(A.amount) AS amount,
                                A.narration,
                                A.ref,
                                A.tanggal,
                                A.ca_id,
                                A.penerima_dana,
                                A.pic,
                                A.no_mi,
                                A.no_po,
                                A.nilai_po
                            FROM (
                                    SELECT
                                    --"public".project_project.year_project,
                                    EXTRACT (YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                                    "public".budget_plan.project_id,
                                    Sum("public".cash_advance_line.price_unit * "public".cash_advance_line.quantity) AS amount,
                                    string_agg("public".cash_advance_line."name", '; ') AS narration,
                                    "public".cash_advance."number" AS "ref",
                                    "public".cash_advance."date" AS tanggal,
                                    "public".cash_advance."id" AS ca_id,
                                    "public".hr_employee.name_related AS penerima_dana,
                                    "public".res_partner."name" AS pic,
                                    "public".sale_memo_internal."name" AS no_mi,
                                    --Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    --string_agg("public".sale_order.client_order_ref, '; ') AS no_po
                                    MAX("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    MAX("public".sale_order.client_order_ref) AS no_po
                                    FROM
                                    "public".cash_advance_line
                                    LEFT JOIN "public".cash_advance ON "public".cash_advance_line.voucher_id = "public".cash_advance."id"
                                    LEFT JOIN "public".budget_plan_line ON "public".cash_advance_line.budget_item_id = "public".budget_plan_line."id"
                                    LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                    LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                    LEFT JOIN "public".hr_employee ON "public".cash_advance.employee_id = "public".hr_employee."id"
                                    LEFT JOIN "public".res_users ON "public".cash_advance.user_id = "public".res_users."id"
                                    LEFT JOIN "public".res_partner ON "public".res_users.partner_id = "public".res_partner."id"
                                            LEFT JOIN "public".sale_memo_internal ON "public".budget_plan.mi_id = "public".sale_memo_internal."id"
                                            LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    WHERE
                                    "public".cash_advance."state" = 'close'
                                    GROUP BY
                                    "public".cash_advance_line.budget_item_id,
                                    "public".cash_advance."number",
                                    "public".budget_plan.project_id,
                                    EXTRACT (YEAR FROM "public".project_project.tanggal_surat_tugas),
                                    "public".cash_advance."date",
                                    "public".cash_advance."id",
                                    "public".hr_employee.name_related,
                                    "public".res_partner."name",
                                            "public".sale_memo_internal."name"
                                    UNION
                                    SELECT
                                        --"public".project_project.year_project,
                                        EXTRACT (YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                                        "public".budget_plan.project_id,
                                        Sum("public".cash_settlement_line.price_unit * "public".cash_settlement_line.quantity) AS amount,
                                        string_agg("public".cash_settlement_line."name", '; ') AS narration,
                                        "public".cash_advance."number" as ref,
                                        "public".cash_advance."date" AS tanggal,
                                        "public".cash_advance."id" AS ca_id,
                                        "public".hr_employee.name_related AS penerima_dana,
                                        "public".res_partner."name" AS pic,
                                        "public".sale_memo_internal."name" AS no_mi,
                                        --Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                        --string_agg("public".sale_order.client_order_ref, '; ') AS no_po
                                        MAX("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                        MAX("public".sale_order.client_order_ref) AS no_po
                                    FROM
                                        "public".cash_settlement_line
                                        LEFT JOIN "public".cash_settlement ON "public".cash_settlement_line.voucher_id = "public".cash_settlement."id"
                                        LEFT JOIN "public".cash_advance ON "public".cash_settlement.advance_id = "public".cash_advance."id"
                                        LEFT JOIN "public".budget_plan_line ON "public".cash_settlement_line.budget_item_id = "public".budget_plan_line."id"
                                        LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".hr_employee ON "public".cash_advance.employee_id = "public".hr_employee."id"
                                        LEFT JOIN "public".res_users ON "public".cash_advance.user_id = "public".res_users."id"
                                        LEFT JOIN "public".res_partner ON "public".res_users.partner_id = "public".res_partner."id"
                                                    LEFT JOIN "public".sale_memo_internal ON "public".budget_plan.mi_id = "public".sale_memo_internal."id"
                                                    LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    WHERE
                                        "public".cash_advance."state" = 'lunas'
                                    GROUP BY
                                        "public".cash_settlement_line.budget_item_id,
                                        "public".cash_advance."number",
                                        "public".budget_plan.project_id,
                                        --"public".project_project.year_project,
                                        EXTRACT (YEAR FROM "public".project_project.tanggal_surat_tugas),
                                        "public".cash_advance."date",
                                        "public".cash_advance."id",
                                        "public".hr_employee.name_related,
                                        "public".res_partner."name",
                                                    "public".sale_memo_internal."name"
                                ) AS A
                            WHERE
                                 A.year_project = :tahun
                            GROUP BY
                                A.year_project,
                                A.project_id,
                                A.narration,
                                A.ref,
                                A.tanggal,
                                A.ca_id,
                                A.penerima_dana,
                                A.pic,
                                A.no_mi,
                                A.no_po,
                                A.nilai_po
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveAdvance(@PathParam("tahun") tahun:Long): Iterable<CorrectiveAdvance>
}
