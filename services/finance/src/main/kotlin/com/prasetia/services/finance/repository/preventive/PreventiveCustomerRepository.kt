package com.prasetia.services.finance.repository.preventive

import com.prasetia.services.finance.model.preventive.PreventiveCustomer
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface PreventiveCustomerRepository: CrudRepository<PreventiveCustomer, Long>{
    companion object {
        const val QUERY = """
                        SELECT
                            ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                            "public".res_partner."id" AS customer_id,
                            "public".res_partner."name" AS customer_name,
                            "public".project_site.tahun,
                            "public".project_site.bulan,
                            COALESCE(SUM(A.nilai_po),0) AS nilai_po
                        FROM
                            "public".project_project
                        LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                        LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                        LEFT JOIN (
                                SELECT
                                "public".sale_order_line.project_id,
                                "public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty nilai_po
                                FROM
                                "public".sale_order_line
                                LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                WHERE
                                "public".sale_order."state" NOT IN ('draft', 'cancel')
                            ) AS A ON A.project_id = "public".project_project."id"
                        WHERE
                            "public".project_project.site_type_id = 7 AND
                            "public".project_site.tahun IS NOT NULL AND
                            "public".project_site.bulan IS NOT NULL AND
                            "public".res_partner."id" IS NOT NULL
                        GROUP BY
                            "public".res_partner."id",
                            "public".res_partner."name",
                            "public".project_site.tahun,
                            "public".project_site.bulan
                        """

        const val QUERY_PREVENTIVE_REPORT = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                                "public".res_partner."id" AS customer_id,
                                "public".res_partner.code AS customer_name,
                                "public".project_area."name" AS area,
                                "public".project_area."id" AS area_id,
                                "public".project_site.tahun,
                                COALESCE(sum(B.nilai_po),0) AS nilai_po,
                                COALESCE(sum(C.nilai_penagihan),0) AS nilai_penagihan,
                                COALESCE(round(sum(C.nilai_penagihan)/sum(B.nilai_po) * 100, 2),0) AS persent_penagihan,
                                COALESCE(sum(D.nilai_budget),0) AS nilai_budget ,
                                COALESCE(sum(E.realisasi_budget),0) AS realisasi_budget,
                                COALESCE(sum(E.realisasi_budget)/sum(D.nilai_budget) * 100,0) AS persent_budget,
                                COALESCE(sum(C.nilai_penagihan),0)-COALESCE(sum(E.realisasi_budget),0) AS laba_rugi,
                                COALESCE((sum(C.nilai_penagihan)-sum(E.realisasi_budget))/sum(E.realisasi_budget) *  100,0) AS persent_laba_rugi
                            FROM
                                "public".project_project
                                LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                                LEFT JOIN "public".account_analytic_account ON "public".project_project.analytic_account_id = "public".account_analytic_account."id"
                                LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                                LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                LEFT JOIN "public".project_area ON "public".project_site.area_id = "public".project_area."id"
                                LEFT JOIN (
                                            SELECT DISTINCT
                                            "public".budget_plan.project_id,
                                            "public".budget_area_detail."id" AS area_detail_id,
                                            "public".budget_area_detail."name" AS area_detail
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".budget_area_detail ON "public".budget_plan.area_detail_id = "public".budget_area_detail."id"
                                            WHERE
                                            "public".budget_plan.project_id IS NOT NULL AND
                                            "public".budget_plan.area_detail_id IS NOT NULL
                                    ) AS A ON A.project_id = "public".project_project.id
                                LEFT JOIN (
                                            SELECT
                                            "public".sale_order_line.project_id,
                                            Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po
                                            FROM
                                            "public".sale_order_line
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel')
                                            GROUP BY
                                            "public".sale_order_line.project_id
                                    ) AS B ON B.project_id = "public".project_project.id
                                LEFT JOIN (
                                            SELECT
                                            "public".sale_order_line.project_id,
                                            Sum("public".account_invoice_line.price_subtotal) AS nilai_penagihan
                                            FROM
                                            "public".sale_order_line
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" IN ('paid') AND
                                            "public".sale_order_line.project_id IS NOT NULL
                                            GROUP BY
                                            "public".sale_order_line.project_id
                                    ) AS C ON C.project_id = "public".project_project.id
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
                                LEFT JOIN (
                                                SELECT
                                                "public".budget_plan.project_id,
                                                SUM("AA".budget_realisasi) AS realisasi_budget
                                                FROM
                                                "public".budget_plan
                                                LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                                                LEFT JOIN (
                                                                        SELECT
                                                                            AAA.budget_plan_line_id AS budget_plan_line_id,
                                                                            sum(AAA.realisasi_debit-AAA.realisasi_credit) AS budget_realisasi,
                                                                            AAA.ref
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
                                                                        ) AS AAA
                                                                        GROUP BY
                                                                        AAA.budget_plan_line_id,
                                                                        AAA.ref
                                                                            ) AS "AA" ON "AA".budget_plan_line_id = "public".budget_plan_line."id"
                                                                        WHERE
                                                    "public".budget_plan."state" <> 'draft' AND
                                                    "public".budget_plan.project_id IS NOT NULL
                                                    GROUP BY
                                                    "public".budget_plan.project_id
                                    ) AS E ON E.project_id = "public".project_project.id
                            WHERE
                                "public".project_project."state" NOT IN ('cancelled') AND
                                "public".project_project.site_type_id = 7 AND
                                "public".project_site.tahun IS NOT NULL AND
                                "public".project_site.tahun = :tahun
                            GROUP BY
                                "public".res_partner."id",
                                "public".res_partner.code,
                                "public".project_area."name",
                                "public".project_site.tahun,
                                "public".project_area."id"
                            ORDER BY
                                "public".project_site."tahun" desc
                        """
    }

    @Async
    @Query(QUERY_PREVENTIVE_REPORT, nativeQuery = true)
    fun getAllData(@PathParam("tahun") tahun:String): Iterable<PreventiveCustomer>
}
