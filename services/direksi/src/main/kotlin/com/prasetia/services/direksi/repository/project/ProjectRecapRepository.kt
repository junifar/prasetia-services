package com.prasetia.services.direksi.repository.project

import com.prasetia.services.direksi.model.project.ProjectRecap
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface ProjectRecapRepository:CrudRepository<ProjectRecap, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AA.site_type) AS id,
                                AA.site_type_id,
                                AA.site_type,
                                COALESCE(SUM(AA.count_po_project),0) AS po,
                                --COALESCE(SUM(AA.invoiced),0) AS invoiced,
                                COALESCE(SUM(AA.invoiced) + SUM(AA.invoice_paid),0) AS invoiced,
                                COALESCE(SUM(AA.BAST_STATUS),0) AS bast,
                                COALESCE(SUM(AA.invoice_paid),0) AS paid,
                                COALESCE(SUM(AA.nilai_po),0) AS nilai_po,
                                COALESCE(SUM(AB.REALISASI_BUDGET),0) AS realisasi_budget,
                                COALESCE(SUM(AA.invoice_open) + SUM(AA.invoice_paid_customer),0) AS invoice_open,
                                COALESCE(SUM(AA.invoice_paid_customer),0) AS invoice_paid
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."id" AS site_type_id,
                                    "public".project_site_type."name" AS site_type,
                                    "public".budget_plan.date,
                                    Count("public".sale_order_line."id") AS count_po_project,
                                    Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    Count(A.paid_status) AS invoiced,
                                    sum(B.bast_status) AS BAST_STATUS,
                                    count(C.paid_status) AS invoice_paid,
                                    COALESCE(max(A.invoice_date), now()) AS invoice_date,
                                    sum(A.total_invoice) AS invoice_open,
                                    sum(C.total_invoice) AS invoice_paid_customer
                                    FROM
                                    "public".budget_plan
                                    LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice,
                                        max("public".account_invoice.date_invoice) invoice_date
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'open'
                                        GROUP BY
                                        "public".budget_plan."id") AS A ON A.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'paid'
                                        GROUP BY
                                        "public".budget_plan."id") AS C ON C.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT DISTINCT
                                        "public".project_task.project_id,
                                        1 as bast_status
                                        FROM
                                        "public".project_task
                                        WHERE
                                        "public".project_task."key_type" IN (15, 24, 25) AND
                                        "public".project_task.project_id IS NOT NULL) AS B ON B.project_id = "public".budget_plan.project_id
                                    LEFT JOIN (
                                            SELECT
                                            "public".budget_plan."id" AS paid_status,
                                            "public".project_site_type."name" AS site_type,
                                            "public".budget_plan.date,
                                            max("public".account_voucher."date") invoice_date
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                            LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                            LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            LEFT JOIN "public".account_move ON "public".account_invoice.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_move_line ON "public".account_move_line.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_voucher_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                            LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" = 'paid'
                                            GROUP BY
                                            "public".budget_plan."id",
                                            "public".project_site_type."name",
                                            "public".budget_plan.date
                                        ) AS D ON D.paid_status = "public".budget_plan."id"
                                            AND D.site_type = "public".project_site_type."name"
                                            AND D.date = "public".budget_plan.date
                                    WHERE
                                    "public".sale_order."state" NOT IN ('draft', 'cancel')
                                    AND "public".project_site_type."id" IN (2,80,10,3,1,5) AND
                                    EXTRACT(YEAR from "public".budget_plan.date) IN (2016,2018,2018,2019)
                                    GROUP BY
                                    "public".budget_plan."id",
                                    "public".project_site_type."id",
                                    "public".project_site_type."name",
                                    "public".budget_plan.date
                            ) AS AA
                            LEFT JOIN
                            (SELECT
                            "public".budget_plan."id",
                            SUM(realisasi_debit - realisasi_credit) AS REALISASI_BUDGET
                            FROM
                            "public".budget_plan
                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN (SELECT
                            "public".budget_plan_line."id" AS budget_plan_line_id,
                            "public".account_move_line.debit AS realisasi_debit,
                            "public".account_move_line.credit AS realisasi_credit,
                            "public".account_move.narration,
                            "public".account_move."ref"
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_used ON "public".budget_used.budget_item_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".account_move_line ON "public".budget_used.move_line_id = "public".account_move_line."id"
                            LEFT JOIN "public".account_move ON "public".account_move_line.move_id = "public".account_move."id"
                            LEFT JOIN "public".advance_move_rel ON "public".advance_move_rel.move_id = "public".account_move."id"
                            LEFT JOIN "public".settlement_move_rel ON "public".settlement_move_rel.move_id = "public".account_move."id"
                            WHERE
                            "public".advance_move_rel.advance_id IS NULL AND
                            "public".settlement_move_rel.settlement_id IS NULL AND
                            "public".account_move_line.debit IS NOT NULL AND
                            "public".account_move_line.credit IS NOT NULL

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
                            "public".cash_advance."number") AS X ON "public".budget_plan_line.id = x.budget_plan_line_id
                            GROUP BY "public".budget_plan."id") AS AB ON AA.budget_id = AB.id
                            -- WHERE
                            -- 	EXTRACT(YEAR from AA.date) IN (2018, 2017)
                            GROUP BY
                                AA.site_type,
                                AA.site_type_id
        """

        const val QUERY_BY_CONDITION_SITE_TYPE_ID = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AA.site_type) AS id,
                                AA.site_type_id,
                                AA.site_type,
                                COALESCE(SUM(AA.count_po_project),0) AS po,
                                --COALESCE(SUM(AA.invoiced),0) AS invoiced,
		                        COALESCE(SUM(AA.invoiced) + SUM(AA.invoice_paid),0) AS invoiced,
                                COALESCE(SUM(AA.BAST_STATUS),0) AS bast,
                                COALESCE(SUM(AA.invoice_paid),0) AS paid,
                                COALESCE(SUM(AA.nilai_po),0) AS nilai_po,
                                COALESCE(SUM(AB.REALISASI_BUDGET),0) AS realisasi_budget,
                                COALESCE(SUM(AA.invoice_open) + SUM(AA.invoice_paid_customer),0) AS invoice_open,
                                COALESCE(SUM(AA.invoice_paid_customer),0) AS invoice_paid
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."id" AS site_type_id,
                                    "public".project_site_type."name" AS site_type,
                                    "public".budget_plan.date,
                                    Count("public".sale_order_line."id") AS count_po_project,
                                    Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    Count(A.paid_status) AS invoiced,
                                    sum(B.bast_status) AS BAST_STATUS,
                                    count(C.paid_status) AS invoice_paid,
                                    COALESCE(max(A.invoice_date), now()) AS invoice_date,
                                    sum(A.total_invoice) AS invoice_open,
                                    sum(C.total_invoice) AS invoice_paid_customer
                                    FROM
                                    "public".budget_plan
                                    LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice,
                                        max("public".account_invoice.date_invoice) invoice_date
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'open'
                                        GROUP BY
                                        "public".budget_plan."id") AS A ON A.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'paid'
                                        GROUP BY
                                        "public".budget_plan."id") AS C ON C.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT DISTINCT
                                        "public".project_task.project_id,
                                        1 as bast_status
                                        FROM
                                        "public".project_task
                                        WHERE
                                        "public".project_task."key_type" IN (15, 24, 25) AND
                                        "public".project_task.project_id IS NOT NULL) AS B ON B.project_id = "public".budget_plan.project_id
                                    LEFT JOIN (
                                            SELECT
                                            "public".budget_plan."id" AS paid_status,
                                            "public".project_site_type."name" AS site_type,
                                            "public".budget_plan.date,
                                            max("public".account_voucher."date") invoice_date
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                            LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                            LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            LEFT JOIN "public".account_move ON "public".account_invoice.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_move_line ON "public".account_move_line.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_voucher_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                            LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" = 'paid'
                                            GROUP BY
                                            "public".budget_plan."id",
                                            "public".project_site_type."name",
                                            "public".budget_plan.date
                                        ) AS D ON D.paid_status = "public".budget_plan."id"
                                            AND D.site_type = "public".project_site_type."name"
                                            AND D.date = "public".budget_plan.date
                                    WHERE
                                    "public".sale_order."state" NOT IN ('draft', 'cancel')
                                    AND "public".project_site_type."id" IN :site_type_ids
                                    GROUP BY
                                    "public".budget_plan."id",
                                    "public".project_site_type."id",
                                    "public".project_site_type."name",
                                    "public".budget_plan.date
                            ) AS AA
                            LEFT JOIN
                            (SELECT
                            "public".budget_plan."id",
                            SUM(realisasi_debit - realisasi_credit) AS REALISASI_BUDGET
                            FROM
                            "public".budget_plan
                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN (SELECT
                            "public".budget_plan_line."id" AS budget_plan_line_id,
                            "public".account_move_line.debit AS realisasi_debit,
                            "public".account_move_line.credit AS realisasi_credit,
                            "public".account_move.narration,
                            "public".account_move."ref"
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_used ON "public".budget_used.budget_item_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".account_move_line ON "public".budget_used.move_line_id = "public".account_move_line."id"
                            LEFT JOIN "public".account_move ON "public".account_move_line.move_id = "public".account_move."id"
                            LEFT JOIN "public".advance_move_rel ON "public".advance_move_rel.move_id = "public".account_move."id"
                            LEFT JOIN "public".settlement_move_rel ON "public".settlement_move_rel.move_id = "public".account_move."id"
                            WHERE
                            "public".advance_move_rel.advance_id IS NULL AND
                            "public".settlement_move_rel.settlement_id IS NULL AND
                            "public".account_move_line.debit IS NOT NULL AND
                            "public".account_move_line.credit IS NOT NULL

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
                            "public".cash_advance."number") AS X ON "public".budget_plan_line.id = x.budget_plan_line_id
                            GROUP BY "public".budget_plan."id") AS AB ON AA.budget_id = AB.id
                            -- WHERE
                            -- 	EXTRACT(YEAR from AA.date) IN (2018, 2017)
                            GROUP BY
                                AA.site_type,
                                AA.site_type_id
        """

        const val QUERY_BY_CONDITION_YEAR = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AA.site_type) AS id,
                                AA.site_type_id,
                                AA.site_type,
                                COALESCE(SUM(AA.count_po_project),0) AS po,
                                --COALESCE(SUM(AA.invoiced),0) AS invoiced,
		                        COALESCE(SUM(AA.invoiced) + SUM(AA.invoice_paid),0) AS invoiced,
                                COALESCE(SUM(AA.BAST_STATUS),0) AS bast,
                                COALESCE(SUM(AA.invoice_paid),0) AS paid,
                                COALESCE(SUM(AA.nilai_po),0) AS nilai_po,
                                COALESCE(SUM(AB.REALISASI_BUDGET),0) AS realisasi_budget,
                                COALESCE(SUM(AA.invoice_open) + SUM(AA.invoice_paid_customer),0) AS invoice_open,
                                COALESCE(SUM(AA.invoice_paid_customer),0) AS invoice_paid
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."id" AS site_type_id,
                                    "public".project_site_type."name" AS site_type,
                                    "public".budget_plan.date,
                                    Count("public".sale_order_line."id") AS count_po_project,
                                    Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    Count(A.paid_status) AS invoiced,
                                    sum(B.bast_status) AS BAST_STATUS,
                                    count(C.paid_status) AS invoice_paid,
                                    COALESCE(max(A.invoice_date), now()) AS invoice_date,
                                    sum(A.total_invoice) AS invoice_open,
                                    sum(C.total_invoice) AS invoice_paid_customer
                                    FROM
                                    "public".budget_plan
                                    LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice,
                                        max("public".account_invoice.date_invoice) invoice_date
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'open'
                                        GROUP BY
                                        "public".budget_plan."id") AS A ON A.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'paid'
                                        GROUP BY
                                        "public".budget_plan."id") AS C ON C.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT DISTINCT
                                        "public".project_task.project_id,
                                        1 as bast_status
                                        FROM
                                        "public".project_task
                                        WHERE
                                        "public".project_task."key_type" IN (15, 24, 25) AND
                                        "public".project_task.project_id IS NOT NULL) AS B ON B.project_id = "public".budget_plan.project_id
                                    LEFT JOIN (
                                            SELECT
                                            "public".budget_plan."id" AS paid_status,
                                            "public".project_site_type."name" AS site_type,
                                            "public".budget_plan.date,
                                            max("public".account_voucher."date") invoice_date
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                            LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                            LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            LEFT JOIN "public".account_move ON "public".account_invoice.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_move_line ON "public".account_move_line.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_voucher_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                            LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" = 'paid'
                                            GROUP BY
                                            "public".budget_plan."id",
                                            "public".project_site_type."name",
                                            "public".budget_plan.date
                                        ) AS D ON D.paid_status = "public".budget_plan."id"
                                            AND D.site_type = "public".project_site_type."name"
                                            AND D.date = "public".budget_plan.date
                                    WHERE
                                    "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                    EXTRACT(YEAR from "public".budget_plan.date) IN :years
                                    GROUP BY
                                    "public".budget_plan."id",
                                    "public".project_site_type."id",
                                    "public".project_site_type."name",
                                    "public".budget_plan.date
                            ) AS AA
                            LEFT JOIN
                            (SELECT
                            "public".budget_plan."id",
                            SUM(realisasi_debit - realisasi_credit) AS REALISASI_BUDGET
                            FROM
                            "public".budget_plan
                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN (SELECT
                            "public".budget_plan_line."id" AS budget_plan_line_id,
                            "public".account_move_line.debit AS realisasi_debit,
                            "public".account_move_line.credit AS realisasi_credit,
                            "public".account_move.narration,
                            "public".account_move."ref"
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_used ON "public".budget_used.budget_item_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".account_move_line ON "public".budget_used.move_line_id = "public".account_move_line."id"
                            LEFT JOIN "public".account_move ON "public".account_move_line.move_id = "public".account_move."id"
                            LEFT JOIN "public".advance_move_rel ON "public".advance_move_rel.move_id = "public".account_move."id"
                            LEFT JOIN "public".settlement_move_rel ON "public".settlement_move_rel.move_id = "public".account_move."id"
                            WHERE
                            "public".advance_move_rel.advance_id IS NULL AND
                            "public".settlement_move_rel.settlement_id IS NULL AND
                            "public".account_move_line.debit IS NOT NULL AND
                            "public".account_move_line.credit IS NOT NULL

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
                            "public".cash_advance."number") AS X ON "public".budget_plan_line.id = x.budget_plan_line_id
                            GROUP BY "public".budget_plan."id") AS AB ON AA.budget_id = AB.id
                            -- WHERE
                            -- 	EXTRACT(YEAR from AA.date) IN (2018, 2017)
                            GROUP BY
                                AA.site_type,
                                AA.site_type_id
        """


        const val QUERY_BY_CONDITION_SITE_TYPE_ID_YEAR = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AA.site_type) AS id,
                                AA.site_type_id,
                                AA.site_type,
                                COALESCE(SUM(AA.count_po_project),0) AS po,
                                --COALESCE(SUM(AA.invoiced),0) AS invoiced,
		                        COALESCE(SUM(AA.invoiced) + SUM(AA.invoice_paid),0) AS invoiced,
                                COALESCE(SUM(AA.BAST_STATUS),0) AS bast,
                                COALESCE(SUM(AA.invoice_paid),0) AS paid,
                                COALESCE(SUM(AA.nilai_po),0) AS nilai_po,
                                COALESCE(SUM(AB.REALISASI_BUDGET),0) AS realisasi_budget,
                                COALESCE(SUM(AA.invoice_open) + SUM(AA.invoice_paid_customer),0) AS invoice_open,
                                COALESCE(SUM(AA.invoice_paid_customer),0) AS invoice_paid
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."id" AS site_type_id,
                                    "public".project_site_type."name" AS site_type,
                                    "public".budget_plan.date,
                                    Count("public".sale_order_line."id") AS count_po_project,
                                    Sum("public".sale_order_line.price_unit * "public".sale_order_line.product_uom_qty) AS nilai_po,
                                    Count(A.paid_status) AS invoiced,
                                    sum(B.bast_status) AS BAST_STATUS,
                                    count(C.paid_status) AS invoice_paid,
                                    COALESCE(max(A.invoice_date), now()) AS invoice_date,
                                    sum(A.total_invoice) AS invoice_open,
                                    sum(C.total_invoice) AS invoice_paid_customer
                                    FROM
                                    "public".budget_plan
                                    LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                    LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                    LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                    LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice,
                                        max("public".account_invoice.date_invoice) invoice_date
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'open'
                                        GROUP BY
                                        "public".budget_plan."id") AS A ON A.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT
                                        "public".budget_plan."id" AS paid_status,
                                        sum("public".account_invoice_line.price_subtotal) as total_invoice
                                        FROM
                                        "public".budget_plan
                                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                        LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                        LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                        LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                        LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                        WHERE
                                        "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                        "public".account_invoice."state" = 'paid'
                                        GROUP BY
                                        "public".budget_plan."id") AS C ON C.paid_status = "public".budget_plan."id"
                                    LEFT JOIN (SELECT DISTINCT
                                        "public".project_task.project_id,
                                        1 as bast_status
                                        FROM
                                        "public".project_task
                                        WHERE
                                        "public".project_task."key_type" IN (15, 24, 25) AND
                                        "public".project_task.project_id IS NOT NULL) AS B ON B.project_id = "public".budget_plan.project_id
                                    LEFT JOIN (
                                            SELECT
                                            "public".budget_plan."id" AS paid_status,
                                            "public".project_site_type."name" AS site_type,
                                            "public".budget_plan.date,
                                            max("public".account_voucher."date") invoice_date
                                            FROM
                                            "public".budget_plan
                                            LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                                            LEFT JOIN "public".project_site_type ON "public".project_project.site_type_id = "public".project_site_type."id"
                                            LEFT JOIN "public".sale_order_line ON "public".sale_order_line.project_id = "public".project_project."id"
                                            LEFT JOIN "public".sale_order ON "public".sale_order_line.order_id = "public".sale_order."id"
                                            LEFT JOIN "public".sale_order_line_invoice_rel ON "public".sale_order_line_invoice_rel.order_line_id = "public".sale_order_line."id"
                                            LEFT JOIN "public".account_invoice_line ON "public".sale_order_line_invoice_rel.invoice_id = "public".account_invoice_line."id"
                                            LEFT JOIN "public".account_invoice ON "public".account_invoice_line.invoice_id = "public".account_invoice."id"
                                            LEFT JOIN "public".account_move ON "public".account_invoice.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_move_line ON "public".account_move_line.move_id = "public".account_move."id"
                                            LEFT JOIN "public".account_voucher_line ON "public".account_voucher_line.move_line_id = "public".account_move_line."id"
                                            LEFT JOIN "public".account_voucher ON "public".account_voucher_line.voucher_id = "public".account_voucher."id"
                                            WHERE
                                            "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                            "public".account_invoice."state" = 'paid'
                                            GROUP BY
                                            "public".budget_plan."id",
                                            "public".project_site_type."name",
                                            "public".budget_plan.date
                                        ) AS D ON D.paid_status = "public".budget_plan."id"
                                            AND D.site_type = "public".project_site_type."name"
                                            AND D.date = "public".budget_plan.date
                                    WHERE
                                    "public".sale_order."state" NOT IN ('draft', 'cancel') AND
                                    EXTRACT(YEAR from "public".budget_plan.date) IN :years
                                    AND "public".project_site_type."id" IN :site_type_ids
                                    GROUP BY
                                    "public".budget_plan."id",
                                    "public".project_site_type."id",
                                    "public".project_site_type."name",
                                    "public".budget_plan.date
                            ) AS AA
                            LEFT JOIN
                            (SELECT
                            "public".budget_plan."id",
                            SUM(realisasi_debit - realisasi_credit) AS REALISASI_BUDGET
                            FROM
                            "public".budget_plan
                            LEFT JOIN "public".budget_plan_line ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                            LEFT JOIN (SELECT
                            "public".budget_plan_line."id" AS budget_plan_line_id,
                            "public".account_move_line.debit AS realisasi_debit,
                            "public".account_move_line.credit AS realisasi_credit,
                            "public".account_move.narration,
                            "public".account_move."ref"
                            FROM
                            "public".budget_plan_line
                            LEFT JOIN "public".budget_used ON "public".budget_used.budget_item_id = "public".budget_plan_line."id"
                            LEFT JOIN "public".account_move_line ON "public".budget_used.move_line_id = "public".account_move_line."id"
                            LEFT JOIN "public".account_move ON "public".account_move_line.move_id = "public".account_move."id"
                            LEFT JOIN "public".advance_move_rel ON "public".advance_move_rel.move_id = "public".account_move."id"
                            LEFT JOIN "public".settlement_move_rel ON "public".settlement_move_rel.move_id = "public".account_move."id"
                            WHERE
                            "public".advance_move_rel.advance_id IS NULL AND
                            "public".settlement_move_rel.settlement_id IS NULL AND
                            "public".account_move_line.debit IS NOT NULL AND
                            "public".account_move_line.credit IS NOT NULL

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
                            "public".cash_advance."number") AS X ON "public".budget_plan_line.id = x.budget_plan_line_id
                            GROUP BY "public".budget_plan."id") AS AB ON AA.budget_id = AB.id
                            -- WHERE
                            -- 	EXTRACT(YEAR from AA.date) IN (2018, 2017)
                            GROUP BY
                                AA.site_type,
                                AA.site_type_id
        """
    }

    @Query(QUERY, nativeQuery = true)
    fun getAllRecapData():Iterable<ProjectRecap>

    @Query(QUERY_BY_CONDITION_SITE_TYPE_ID, nativeQuery = true)
    fun getAllRecapDataBySiteTypeID(@PathParam("site_type_ids") site_type_ids:List<Long>):Iterable<ProjectRecap>

    @Query(QUERY_BY_CONDITION_YEAR, nativeQuery = true)
    fun getAllRecapDataByYear(@PathParam("years") years:List<Long>):Iterable<ProjectRecap>

    @Query(QUERY_BY_CONDITION_SITE_TYPE_ID_YEAR, nativeQuery = true)
    fun getAllRecapDataBySiteTypeIDYear(@PathParam("site_type_ids") site_type_ids:List<Long>, @PathParam("years") years:List<Long>):Iterable<ProjectRecap>
}
