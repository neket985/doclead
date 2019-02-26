package com.mirea.api.controllers

import com.mirea.api.ParamAddRemove
import com.mirea.api.ProjectUid
import com.mirea.api.kodein
import com.mirea.common.ApiError.Companion.apiError
import com.mirea.common.api
import com.mirea.common.getPrincipal
import com.mirea.mongo.dao.ProjectDao
import com.mirea.mongo.dao.UserDao
import com.mirea.mongo.entity.Project
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import org.kodein.di.generic.instance
import org.litote.kmongo.addToSet
import org.litote.kmongo.pull
import org.litote.kmongo.set

object ProjectController {
    private val userDao by kodein.instance<UserDao>()
    private val projectDao by kodein.instance<ProjectDao>()

    val route: Route.() -> Unit = {
        route("author") {
            post("remove", authorRemove)
            post("add", authorAdd)
        }
        post("branch/add", branchAdd)
        post("access/toggle", accessByLinkToggle)
    }

    val authorRemove: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        api {
            val authorAdd = context.receive<ParamAddRemove>()

            val author = userDao.getConfirmedByName(authorAdd.name)?.toUserEmbedded()
                    ?: apiError(404, "User not founded")
            val principal = context.getPrincipal()
            val project = projectDao.getByUid(authorAdd.projectUid, principal.toUserEmbedded())
                    ?: apiError(404, "Project not founded")

            if (project.creator._id != principal.id) apiError(403, "Creator only can add authors")

            if (project.authors.contains(author)) {
                projectDao.updateById(project._id!!, pull(Project::authors, author))
            } else project

        }
    }

    val authorAdd: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        api {
            val authorAdd = context.receive<ParamAddRemove>()

            val author = userDao.getConfirmedByName(authorAdd.name)?.toUserEmbedded()
                    ?: apiError(404, "User not founded")
            val principal = context.getPrincipal()
            val project = projectDao.getByUid(authorAdd.projectUid, principal.toUserEmbedded())
                    ?: apiError(404, "Project not founded")

            if (project.creator._id != principal.id) apiError(403, "Creator only can add authors")

            if (project.authors.contains(author)) project
            else {
                projectDao.updateById(project._id!!, addToSet(Project::authors, author))
            }
        }
    }

    val branchAdd: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        api {
            val branchAdd = context.receive<ParamAddRemove>()

            val principal = context.getPrincipal()
            val project = projectDao.getByUid(branchAdd.projectUid, principal.toUserEmbedded())
                    ?: apiError(404, "Project not founded")

            projectDao.updateById(project._id!!, addToSet(Project::branches, branchAdd.name))
        }
    }

    val accessByLinkToggle: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        api {
            val uid = context.receive<ProjectUid>().uid

            val principal = context.getPrincipal()

            val project = projectDao.getByUid(uid, principal.toUserEmbedded())
                    ?: apiError(404, "Project not founded")

            if (project.creator._id != principal.id) apiError(403, "Creator only can add authors")

            projectDao.updateById(project._id!!, set(Project::accessByLink, !project.accessByLink))
        }
    }

}