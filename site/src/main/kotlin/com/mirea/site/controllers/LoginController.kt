package com.mirea.site.controllers

import com.mirea.mongo.dao.UserDao
import com.mirea.site.UserPrincipal
import com.mirea.site.common.kodein
import com.mirea.site.common.render
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance

object LoginController {

    private val userDao by kodein.instance<UserDao>()

    val login: Route.() -> Unit = {
        get("") {
            if (context.principal<UserPrincipal>() != null) {
                context.respondRedirect("/")
            } else {
                context.render("login")
            }
        }
        authenticate {
            post("") {
                context.respondRedirect("/")
            }
        }
    }

}