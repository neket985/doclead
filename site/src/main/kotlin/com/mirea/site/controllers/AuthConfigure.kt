package com.mirea.site.controllers

import com.auth0.jwt.exceptions.JWTVerificationException
import com.mirea.common.JwtCommon
import com.mirea.common.JwtCommon.toPrincipal
import com.mirea.common.JwtSession
import com.mirea.common.toPrincipal
import com.mirea.mongo.dao.UserDao
import com.mirea.site.common.SiteURLS
import com.mirea.site.common.kodein
import io.ktor.auth.Authentication
import io.ktor.auth.FormAuthChallenge
import io.ktor.auth.authentication
import io.ktor.auth.form
import io.ktor.request.uri
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.kodein.di.generic.instance
import org.mindrot.jbcrypt.BCrypt

object AuthConfigure {
    private val userDao by kodein.instance<UserDao>()

    val configure: Authentication.Configuration.() -> Unit = {
        form {
            passwordParamName = "password"
            userParamName = "email"
            challenge = FormAuthChallenge.Redirect {
                if (this.request.uri == SiteURLS.loginUrl()) SiteURLS.loginUrl(loginError = true)
                else SiteURLS.loginUrl()
            }

            skipWhen { call ->
                call.sessions.get<JwtSession>()?.let {
                    try {
                        val decoded = JwtCommon.verifier.verify(it.jwt)
                        call.authentication.principal = decoded.claims.toPrincipal()
                        true
                    } catch (e: JWTVerificationException) {
                        false
                    }
                } ?: false
            }
            validate { creds ->
                userDao.getConfirmedByEmail(creds.name)?.let { user ->
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
}