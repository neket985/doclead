package com.mirea.site.controllers

import com.mirea.mongo.Page
import com.mirea.mongo.dao.ProjectDao
import com.mirea.mongo.entity.Project
import com.mirea.site.common.kodein
import com.mirea.site.common.render
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.litote.kmongo.descending

object ProjectController {
    private val projectDao by kodein.instance<ProjectDao>()

    val home: Route.() -> Unit = {
        get("") {
            val p = context.parameters["page"]?.toInt() ?: 0
            val page = projectDao.page(Page(p, 10, descending(Project::createdAt)))

            context.render("home", "page" to page)
        }
    }

    val projectAdd: Route.() -> Unit = {
        get("") {

        }
        post(""){

        }
    }

}