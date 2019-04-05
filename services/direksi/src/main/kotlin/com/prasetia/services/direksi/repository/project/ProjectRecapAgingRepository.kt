package com.prasetia.services.direksi.repository.project

import com.prasetia.services.direksi.model.project.ProjectRecapAging
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface ProjectRecapAgingRepository:CrudRepository<ProjectRecapAging, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AAA.site_type) AS id,
                                AAA.site_type_id,
                                AAA.site_type,
                                sum(AAA.greater_60) AS greater_60,
                                sum(AAA.between_30_60) AS between_30_60,
                                sum(AAA.less_30) AS less_30
                            FROM (
                                SELECT
                                AA.budget_id,
                                AA.site_type,
																AA.site_type_id,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) > 60 THEN 1
                                    ELSE 0
                                END AS greater_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) BETWEEN 30 AND 60 THEN 1
                                    ELSE 0
                                END AS between_30_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) < 30 THEN 1
                                    ELSE 0
                                END AS less_30
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."name" AS site_type,
																		"public".project_site_type."id" AS site_type_id,
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
                            WHERE
                                AA.invoice_date IS NOT NULL
                            ) AS AAA
                            GROUP BY
                                AAA.site_type_id,
                                AAA.site_type
        """

        const val QUERY_BY_CONDITION_SITE_TYPE_ID = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AAA.site_type) AS id,
                                AAA.site_type_id,
                                AAA.site_type,
                                sum(AAA.greater_60) AS greater_60,
                                sum(AAA.between_30_60) AS between_30_60,
                                sum(AAA.less_30) AS less_30
                            FROM (
                                SELECT
                                AA.budget_id,
                                AA.site_type,
																AA.site_type_id,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) > 60 THEN 1
                                    ELSE 0
                                END AS greater_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) BETWEEN 30 AND 60 THEN 1
                                    ELSE 0
                                END AS between_30_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) < 30 THEN 1
                                    ELSE 0
                                END AS less_30
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."name" AS site_type,
																		"public".project_site_type."id" AS site_type_id,
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
                            WHERE
                                AA.invoice_date IS NOT NULL
                            ) AS AAA
                            GROUP BY
																AAA.site_type_id,
                                AAA.site_type
        """

        const val QUERY_BY_CONDITION_YEAR = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AAA.site_type) AS id,
                                AAA.site_type_id,
                                AAA.site_type,
                                sum(AAA.greater_60) AS greater_60,
                                sum(AAA.between_30_60) AS between_30_60,
                                sum(AAA.less_30) AS less_30
                            FROM (
                                SELECT
                                AA.budget_id,
                                AA.site_type,
																AA.site_type_id,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) > 60 THEN 1
                                    ELSE 0
                                END AS greater_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) BETWEEN 30 AND 60 THEN 1
                                    ELSE 0
                                END AS between_30_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) < 30 THEN 1
                                    ELSE 0
                                END AS less_30
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."name" AS site_type,
																		"public".project_site_type."id" AS site_type_id,
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
                            WHERE
                                AA.invoice_date IS NOT NULL
                            ) AS AAA
                            GROUP BY
																AAA.site_type_id,
                                AAA.site_type
        """

        const val QUERY_BY_CONDITION_SITE_TYPE_ID_YEAR = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY AAA.site_type) AS id,
                                AAA.site_type_id,
                                AAA.site_type,
                                sum(AAA.greater_60) AS greater_60,
                                sum(AAA.between_30_60) AS between_30_60,
                                sum(AAA.less_30) AS less_30
                            FROM (
                                SELECT
                                AA.budget_id,
                                AA.site_type,
																AA.site_type_id,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) > 60 THEN 1
                                    ELSE 0
                                END AS greater_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) BETWEEN 30 AND 60 THEN 1
                                    ELSE 0
                                END AS between_30_60,
                                CASE
                                    WHEN date_part('day', AA.invoice_date - AA.date) < 30 THEN 1
                                    ELSE 0
                                END AS less_30
                            FROM
                            (
                                    SELECT
                                    "public".budget_plan."id" AS budget_id,
                                    "public".project_site_type."name" AS site_type,
																		"public".project_site_type."id" AS site_type_id,
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
                            WHERE
                                AA.invoice_date IS NOT NULL
                            ) AS AAA
                            GROUP BY
                                AAA.site_type_id,
                                AAA.site_type
        """
    }

    @Query(QUERY, nativeQuery = true)
    fun getAllRecapAgingData():Iterable<ProjectRecapAging>

    @Query(QUERY_BY_CONDITION_SITE_TYPE_ID, nativeQuery = true)
    fun getAllRecapAgingDataBySiteTypeID(@PathParam("site_type_ids") site_type_ids:List<Long>):Iterable<ProjectRecapAging>

    @Query(QUERY_BY_CONDITION_YEAR, nativeQuery = true)
    fun getAllRecapAgingDataByYear(@PathParam("years") years:List<Long>):Iterable<ProjectRecapAging>

    @Query(QUERY_BY_CONDITION_SITE_TYPE_ID_YEAR, nativeQuery = true)
    fun getAllRecapAgingDataBySiteTypeIDYear(@PathParam("site_type_ids") site_type_ids:List<Long>, @PathParam("years") years:List<Long>):Iterable<ProjectRecapAging>
}
