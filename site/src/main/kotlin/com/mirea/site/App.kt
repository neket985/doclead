package com.mirea.site

import com.mirea.common.JwtSession
import com.mirea.site.common.SiteURLS
import com.mirea.site.controllers.AuthConfigure
import com.mirea.site.controllers.DocumentController
import com.mirea.site.controllers.LoginController
import com.mirea.site.controllers.ProjectController
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.features.StatusPages
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import javax.naming.AuthenticationException

object App {
    fun Application.main() {
        install(Sessions) {
            cookie<JwtSession>("USER_JWT_SESSION") {
                cookie.path = "/"
                cookie.encoding = CookieEncoding.DQUOTES
            }
        }

        install(Authentication, AuthConfigure.configure)

        val staticDir = environment.config.property("template.staticDir").getString()
        routing {
            static("css") {
                files("$staticDir/css")
            }
            static("js") {
                files("$staticDir/js")
            }
            static("img") {
                files("$staticDir/img")
            }

            route("login", LoginController.login)
            route("register", LoginController.register)

            authenticate {
                route("", ProjectController.home)
                route("project") {
                    route("{uid}") {
                        route("", ProjectController.detail)
                        route("branches", ProjectController.branches)
                        route("document") {
                            route("v{branch}") { //начинается с v, что бы не было путаницы с аналогичными запросами без указания версии
                                route("", DocumentController.detail)
                                route("add", DocumentController.documentAdd)
                                route("html", DocumentController.docHtml)
                                route("download", DocumentController.getFile)
                            }
                            route("", DocumentController.detail)
                            route("add", DocumentController.documentAdd)
                            route("download", DocumentController.getFile)
                        }
                    }
                    route("add", ProjectController.projectAdd)
                }
            }
        }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            status(HttpStatusCode.Unauthorized) {
                context.respondRedirect(SiteURLS.loginUrl())
            }
//            exception<AuthorizationException> { cause ->
//                call.respond(HttpStatusCode.Forbidden)
//            }
        }
    }
}