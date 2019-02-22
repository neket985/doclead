package com.mirea.api

import com.mirea.common.JwtCommon
import com.mirea.common.JwtCommon.toPrincipal
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import javax.naming.AuthenticationException

object App {
    fun Application.main() {
        install(Authentication) {
            jwt {
                verifier(JwtCommon.verifier)

                validate { credential ->
                    credential.payload.toPrincipal()
                }
            }
        }

        routing {

        }
        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            status(HttpStatusCode.Unauthorized) {

            }
        }
    }
}