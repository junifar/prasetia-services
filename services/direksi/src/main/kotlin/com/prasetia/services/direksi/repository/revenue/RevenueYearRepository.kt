package com.prasetia.services.direksi.repository.revenue

import com.prasetia.services.direksi.model.revenue.RevenueYear
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RevenueYearRepository:CrudRepository<RevenueYear, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                ROW_NUMBER() OVER (ORDER BY EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)) AS id,
                                EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) as tahun,
                                COALESCE(sum(B.nilai_po),0) AS nilai_po,
                                COALESCE(sum(F.nilai_penagihan),0) AS invoiced,
                                COALESCE(sum(C.nilai_penagihan),0) AS paid,
                                COALESCE(sum(F.nilai_penagihan) + sum(C.nilai_penagihan),0) AS total,
                                0 AS target
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
                                                        "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                                                        "public".sale_order_line.project_id IS NOT NULL
                                                        GROUP BY
                                                        "public".sale_order_line.project_id
                                                ) AS F ON F.project_id = "public".project_project.id
                            WHERE
                                "public".project_project."state" NOT IN ('cancelled') AND
                                "public".project_site.customer_id IS NOT NULL AND
                                "public".project_project.tanggal_surat_tugas IS NOT NULL
                            GROUP BY
                                EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)
                            ORDER BY
                                EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) desc
                            """
    }

    @Query(QUERY, nativeQuery = true)
    fun getRevenueYear():Iterable<RevenueYear>
}
