package com.prasetia.services.finance.repository.corrective

import com.prasetia.services.finance.model.corrective.CorrectiveBudgetUsed
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface CorrectiveBudgetUsedRepository:CrudRepository<CorrectiveBudgetUsed, Long>{
    companion object {
        const val QUERY = """
                        SELECT
                        ROW_NUMBER() OVER (ORDER BY "public".budget_plan.project_id) AS id,
                        --"public".project_project.year_project,
                        EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) AS year_project,
                        "public".budget_plan.project_id,
                        "public".account_move_line.debit - "public".account_move_line.credit AS amount,
                        "public".account_move.narration,
                        "public".account_invoice."name" as ref,
                        "public".res_partner."name" AS pic,
                        supplier."name" AS penerima_dana,
                        "public".account_move."date" AS tanggal
                        FROM
                        "public".budget_plan_line
                        LEFT JOIN "public".budget_plan ON "public".budget_plan_line.budget_id = "public".budget_plan."id"
                        LEFT JOIN "public".budget_used ON "public".budget_used.budget_item_id = "public".budget_plan_line."id"
                        LEFT JOIN "public".project_project ON "public".budget_plan.project_id = "public".project_project."id"
                        LEFT JOIN "public".account_move_line ON "public".budget_used.move_line_id = "public".account_move_line."id"
                        LEFT JOIN "public".account_move ON "public".account_move_line.move_id = "public".account_move."id"
                        LEFT JOIN "public".advance_move_rel ON "public".advance_move_rel.move_id = "public".account_move."id"
                        LEFT JOIN "public".settlement_move_rel ON "public".settlement_move_rel.move_id = "public".account_move."id"
                        LEFT JOIN "public".account_invoice ON "public".account_invoice.move_id = "public".account_move."id"
                        LEFT JOIN "public".res_users ON "public".account_invoice.user_id = "public".res_users."id" AND '' = ''
                        LEFT JOIN "public".res_partner ON "public".res_users.partner_id = "public".res_partner."id"
                        LEFT JOIN "public".res_partner AS supplier ON "public".account_invoice.partner_id = supplier."id"
                        WHERE
                        "public".project_project.site_type_id = 8 AND
                        "public".advance_move_rel.advance_id IS NULL AND
                        "public".settlement_move_rel.settlement_id IS NULL AND
                        "public".account_move_line."id" IS NOT NULL AND
                        "public".budget_plan.project_id IS NOT NULL AND
                        EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun
                        --"public".project_project.year_project = :tahun
                    """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveBudgetUsed(@PathParam("tahun") tahun:Long):Iterable<CorrectiveBudgetUsed>
}
