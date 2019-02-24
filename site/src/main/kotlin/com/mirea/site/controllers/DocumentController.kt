package com.mirea.site.controllers

import com.mirea.common.WebError.Companion.webError
import com.mirea.common.getPrincipal
import com.mirea.mongo.dao.DocumentDao
import com.mirea.mongo.dao.ProjectDao
import com.mirea.mongo.entity.Document
import com.mirea.site.common.SiteURLS
import com.mirea.site.common.copyToSuspend
import com.mirea.site.common.kodein
import com.mirea.site.common.render
import com.typesafe.config.ConfigFactory
import io.ktor.application.call
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import java.io.File
import java.time.Instant

object DocumentController {
    private val projectDao by kodein.instance<ProjectDao>()
    private val documentDao by kodein.instance<DocumentDao>()
    private val baseDir = ConfigFactory.load().getString("documentsDirectory")

    val detail: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.parameters["uid"] ?: webError(400, "Parameter uid required")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val newestDocument = documentDao.getNewest(project._id!!)

            context.render("document-detail", "project" to project, "document" to newestDocument)
        }
    }

    val documentAdd: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.parameters["uid"] ?: webError(400, "Parameter uid required")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val lastVersion = documentDao.getNewest(project._id!!) ?: initVersion
            context.render("document-add", "project" to project, "lastVersion" to lastVersion)
        }

        post("") {
            val user = context.getPrincipal()

            val projectUid = context.parameters["uid"] ?: webError(400, "Parameter uid required")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            var docFile: File? = null
            var isPostmanCollection = false
            var description: String? = null
            var version: String? = null

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val ext = File(part.originalFileName).extension
                        //todo проверять на наличие нужного расширения файла

                        val documentPath = File("$baseDir/${user.id}/${project._id}", initVersion)
                        if (!documentPath.exists()) {
                            documentPath.mkdirs()
                        }
                        val file = File(documentPath, "${part.originalFileName}")
                        part.streamProvider().use { input -> file.outputStream().buffered().use { output -> input.copyToSuspend(output) } }
                        docFile = file
                    }
                    is PartData.FormItem -> {
                        when (part.name) {
                            "doc-toggle" -> isPostmanCollection = part.value.toBoolean()
                            "description" -> description = part.value.let {
                                if (it.isBlank()) null
                                else it
                            }
                            "version" -> version = part.value
                        }
                    }
                }

                part.dispose()
            }

            if (version == null) webError(400, "Parameter version required")

            if (docFile != null) {
                documentDao.insert(
                        Document(
                                project._id!!,
                                Instant.now(),
                                user.toUserEmbedded(),
                                version!!,
                                description,
                                docFile!!.name,
                                false //todo
                        )
                )
            }

            context.respondRedirect(SiteURLS.homeUrl())
        }
    }

    private val initVersion = "0"
}