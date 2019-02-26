package com.mirea.site.controllers

import com.mirea.common.WebError.Companion.webError
import com.mirea.common.getPrincipal
import com.mirea.mongo.dao.DocumentDao
import com.mirea.mongo.dao.ProjectDao
import com.mirea.mongo.entity.Document
import com.mirea.site.common.*
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
import io.ktor.util.error
import org.bson.types.ObjectId
import org.kodein.di.generic.instance
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.time.Instant


object DocumentController {
    private val projectDao by kodein.instance<ProjectDao>()
    private val documentDao by kodein.instance<DocumentDao>()
    private val baseDir = ConfigFactory.load().getString("documentsDirectory")
    private const val convertScript = "/Users/nikitos/IdeaProjects/doclead/site/src/main/resources/openapi-html"

    val detail: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.paramReq("uid")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val branch = context.paramReq("branch")
            val document = documentDao.getByBranch(project._id!!, branch)
                    ?: context.respondRedirect(SiteURLS.documentAddUrl(project.accessUid, branch))

            context.render("document-detail", "project" to project, "document" to document)
        }
    }

    val docHtml: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.paramReq("uid")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val branch = context.paramReq("branch")
            val document = documentDao.getByBranch(project._id!!, branch)
                    ?: webError(404, "Document not founded")

            val html = File(getDocsPath(user.id, project._id!!, document.branch), "index.html")

            if (!html.exists()) webError(500, "Html file not founded")

            context.render(html)
        }
    }

    val getFile: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.paramReq("uid")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val branch = context.paramReq("branch")
            val document = documentDao.getByBranch(project._id!!, branch)
                    ?: webError(404, "Document not founded")

            val file = File(getDocsPath(user.id, project._id!!, document.branch), document.filename)

            if (!file.exists()) webError(500, "File not founded")

            context.respondFile(file)
        }
    }

    val documentAdd: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.paramReq("uid")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            val branch = context.paramReq("branch")
            context.render("document-add", "project" to project, "branch" to branch)
        }

        post("") {
            val user = context.getPrincipal()

            val projectUid = context.paramReq("uid")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: webError(404, "Project not founded")

            var docFile: File? = null
            var isPostmanCollection = false
            var description: String? = null
            var branch: String? = null

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val ext = File(part.originalFileName).extension
                        //todo валидировать файл openapi

                        if (branch == null) webError(400, "Parameter branch required")

                        val documentPath = getDocsPath(user.id, project._id!!, branch!!)
                        if (!documentPath.exists()) {
                            documentPath.mkdirs()
                        } else {
                            documentPath.deleteRecursively()
                            documentPath.mkdirs()
                        }
                        val file = File(documentPath, "${part.originalFileName}")
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyToSuspend(output)
                            }
                        }
                        docFile = file
                    }
                    is PartData.FormItem -> {
                        when (part.name) {
                            "doc-toggle" -> isPostmanCollection = part.value.toBoolean()
                            "description" -> description = part.value.let {
                                if (it.isBlank()) null
                                else it
                            }
                            "branch" -> branch = part.value
                        }
                    }
                }

                part.dispose()
            }

            val document = docFile?.let {
                execCmd("$convertScript ${docFile!!.parent} ${docFile!!.name}")

                documentDao.insert(
                        Document(
                                project._id!!,
                                Instant.now(),
                                user.toUserEmbedded(),
                                branch!!,
                                description,
                                it.name,
                                false //todo
                        )
                )
            } //todo ?: error

            context.respondRedirect(SiteURLS.documentDetailUrl(project, document))
        }
    }

    private fun getDocsPath(userId: ObjectId, projectId: ObjectId, branch: String) =
            File("$baseDir/$userId/$projectId", branch)

    private fun execCmd(cmd: String) =
            try {
                val process = Runtime.getRuntime().exec(cmd)

                val output = StringBuilder()

                val reader = BufferedReader(InputStreamReader(process.inputStream))

                var line = reader.readLine()
                while (line != null) {
                    line = reader.readLine()
                    output.appendln(line)
                }

                val exitVal = process.waitFor()
                if (exitVal == 0) {
                    logger.debug("Success!")
                    logger.debug(output.toString())
                } else {
                    logger.error("abnormal... to exec commands $cmd")
                }

            } catch (e: IOException) {
                logger.error(e)
            } catch (e: InterruptedException) {
                logger.error(e)
            }

    private val logger = LoggerFactory.getLogger(DocumentController::class.java)
}