package com.prasetia.services.finance.repository.corrective

import com.prasetia.services.finance.model.corrective.CorrectiveProject
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import javax.websocket.server.PathParam

@Repository
interface CorrectiveProjectRepository:CrudRepository<CorrectiveProject, Long>{
    companion object {
        const val QUERY = """
                        SELECT
                        "public".project_project."id",
                        --"public".project_project.year_project,
                        EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) as year_project,
                        "public".project_site."name" AS site_name,
                        "public".res_partner.code AS customer,
                        "public".project_site.customer_id
                        FROM
                        "public".project_project
                        LEFT JOIN "public".project_site ON "public".project_project.site_id = "public".project_site."id"
                        LEFT JOIN "public".res_partner ON "public".project_site.customer_id = "public".res_partner."id"
                        WHERE
                        "public".project_project.site_type_id = 8 AND
                        EXTRACT(YEAR FROM "public".project_project.tanggal_surat_tugas) = :tahun
                        """
    }

    @Async
    @Query(QUERY, nativeQuery = true)
    fun getCorrectiveProject(@PathParam("tahun") tahun:Long):Iterable<CorrectiveProject>
}
