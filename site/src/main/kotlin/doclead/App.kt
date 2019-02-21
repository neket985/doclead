package com.mirea.doclead

import com.mirea.doclead.common.JwtCommon
import com.mirea.doclead.common.JwtCommon.toPrincipal
import com.mirea.doclead.common.LoginForm
import com.mirea.doclead.common.render
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.features.StatusPages
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import javax.naming.AuthenticationException

object App {
    fun Application.main() {
        install(Sessions) {
            cookie<String>("USER_JWT_SESSION") {
                cookie.path = "/"
                cookie.encoding = CookieEncoding.DQUOTES
            }
        }

        install(Authentication) {
            jwt {
                verifier(JwtCommon.verifier)

                validate { credential ->
                    credential.payload.toPrincipal()
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

            route("login") {
                get("") {
                    if(context.principal<UserPrincipal>()!= null){
                        context.respondRedirect("/")
                    }else {
                        context.render("login")
                    }
                }
                post("") {
                    val form = context.receive<LoginForm>()
                    println()
                }
            }

            authenticate {
                get(""){
                    context.render("home")
                }
            }
        }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            status(HttpStatusCode.Unauthorized){
                context.respondRedirect("/login")
            }
//            exception<AuthorizationException> { cause ->
//                call.respond(HttpStatusCode.Forbidden)
//            }
        }
    }
}