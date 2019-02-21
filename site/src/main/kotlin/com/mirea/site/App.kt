package com.mirea.site

import com.auth0.jwt.exceptions.JWTVerificationException
import com.mirea.mongo.dao.UserDao
import com.mirea.site.common.*
import com.mirea.site.controllers.LoginController
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.FormAuthChallenge
import io.ktor.auth.authenticate
import io.ktor.auth.form
import io.ktor.features.StatusPages
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.*
import org.kodein.di.generic.instance
import org.mindrot.jbcrypt.BCrypt
import javax.naming.AuthenticationException

object App {
    private val userDao by kodein.instance<UserDao>()
    fun Application.main() {
        install(Sessions) {
            cookie<JwtSession>("USER_JWT_SESSION") {
                cookie.path = "/"
                cookie.encoding = CookieEncoding.DQUOTES
            }
        }

        install(Authentication) {
            form {
                passwordParamName = "password"
                userParamName = "login"
                challenge = FormAuthChallenge.Redirect { "/login" }

                skipWhen {
                    it.sessions.get<JwtSession>()?.let {
                        try {
                            JwtCommon.verifier.verify(it.jwt)
                            true
                        } catch (e: JWTVerificationException) {
                            false
                        }
                    } ?: false
                }
                validate { creds ->
                    userDao.getByUsername(creds.name)?.let { user ->
                        if (BCrypt.checkpw(creds.password, user.password)) {
                            val jwt = JwtCommon.genJWT(user.toPrincipal())
                            sessions.set(JwtSession(jwt))
                            user.toPrincipal()
                        } else {
                            null
                        }
                    }
                }
            }
        }

        val staticDir = environment.config.property("template.staticDir").getString()
        routing {
            static("css") {
                files("$staticDir/css")
            }
            static("js") {
                files("$staticDir/js")
            }

            route("login", LoginController.login)

            authenticate {
                get("") {
                    context.render("home")
                }
            }
        }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            status(HttpStatusCode.Unauthorized) {
                context.respondRedirect("/login")
            }
//            exception<AuthorizationException> { cause ->
//                call.respond(HttpStatusCode.Forbidden)
//            }
        }
    }
}