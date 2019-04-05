package com.prasetia.services.direksi.repository.revenue

import com.prasetia.services.direksi.model.revenue.RevenueYearDetail
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface RevenueYearDetailRepository:CrudRepository<RevenueYearDetail, Long>{
    companion object {
        const val QUERY = """
                            SELECT
                                    ROW_NUMBER() OVER (ORDER BY "public".res_partner."id") AS id,
                                    "public".res_partner."id" AS customer_id,
                                    "public".res_partner.code,
                                    COALESCE(Count("public".project_project."id"),0) AS jumlah_site,
                                    EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) as tahun,
                                    COALESCE(Sum(A.nilai_po),0) AS nilai_po,
                                    COALESCE(sum(C.nilai_penagihan),0) AS invoiced,
                                    COALESCE(sum(B.nilai_penagihan),0) AS paid,
                                    COALESCE(sum(B.nilai_penagihan),0) + COALESCE(sum(C.nilai_penagihan),0)  AS total,
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
                                                                            LEFT JOIN "public".project_project ON "public".sale_order_line.project_id = "public".project_project."id"
                                                                            WHERE
                                                                            "public".sale_order."state" NOT IN ('draft', 'cancel')
                                                                            GROUP BY
                                                                            "public".sale_order_line.project_id
                                                            ) AS A ON "public".project_project."id" = A.project_id
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
                                                                                    "public".account_invoice."state" NOT IN ('draft', 'cancel') AND
                                                                                    "public".sale_order_line.project_id IS NOT NULL
                                                                                    GROUP BY
                                                                                    "public".sale_order_line.project_id
                                                                            ) AS C ON C.project_id = "public".project_project.id
                            WHERE
                                            "public".project_project."state" NOT IN ('cancelled') AND
                                            "public".project_site.customer_id IS NOT NULL AND
                                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun
                            GROUP BY
                                            "public".res_partner."id",
                                            "public".res_partner.code,
                                            EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas)
                            ORDER BY
                                            COALESCE(Sum(A.nilai_po),0) DESC
                            """
    }

    @Query(QUERY, nativeQuery = true)
    fun getRevenueYearDetailByYear(@PathParam("tahun") tahun: Long):Iterable<RevenueYearDetail>
}
