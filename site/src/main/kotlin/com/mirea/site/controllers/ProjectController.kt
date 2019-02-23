package com.mirea.site.controllers

import com.mirea.common.UserPrincipal
import com.mirea.common.WebError
import com.mirea.common.getPrincipal
import com.mirea.mongo.Page
import com.mirea.mongo.dao.ProjectDao
import com.mirea.mongo.entity.Project
import com.mirea.site.common.SiteURLS
import com.mirea.site.common.kodein
import com.mirea.site.common.render
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.util.getOrFail
import org.kodein.di.generic.instance
import org.litote.kmongo.contains
import org.litote.kmongo.descending
import java.time.Instant
import java.util.*

object ProjectController {
    private val projectDao by kodein.instance<ProjectDao>()

    val home: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val p = context.parameters["page"]?.toInt() ?: 0
            val page = projectDao.page(
                    Page(p, 10, descending(Project::createdAt)),
                    Project::authors contains user.toUserEmbedded()
            )

            context.render("home", "page" to page)
        }
    }

    val detail: Route.() -> Unit = {
        get("") {
            val user = context.getPrincipal()

            val projectUid = context.parameters["uid"] ?: WebError.webError(400, "Parameter uid required")
            val project = projectDao.getByUid(projectUid, user.toUserEmbedded()) ?: WebError.webError(404, "Project not founded")

            context.render("project-detail", "project" to project)
        }
    }

    val projectAdd: Route.() -> Unit = {
        get("") {
            context.render("project-add")
        }

        post("") {
            val user = context.principal<UserPrincipal>()!!
            val params = call.receiveParameters()
            val form = params.toAddForm()

            val project = projectDao.insert(
                    Project(
                            form.title,
                            form.description,
                            Instant.now(),
                            user.toUserEmbedded(),
                            listOf(user.toUserEmbedded()),
                            false,
                            UUID.randomUUID().toString()
                    )
            )

            context.respondRedirect(SiteURLS.projectDetailUrl(project))
        }
    }

    data class AddForm(
            val title: String,
            val description: String?
    )

    private fun Parameters.toAddForm() = AddForm(
            this.getOrFail("title"),
            if (this.contains("description"))
                this.getOrFail("description").let {
                    if (it.isBlank()) null
                    else it
                }
            else null
    )

}