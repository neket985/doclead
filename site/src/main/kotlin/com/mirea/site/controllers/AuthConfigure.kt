package com.mirea.site.controllers

import com.auth0.jwt.exceptions.JWTVerificationException
import com.mirea.mongo.dao.UserDao
import com.mirea.site.App
import com.mirea.site.common.*
import io.ktor.auth.Authentication
import io.ktor.auth.FormAuthChallenge
import io.ktor.auth.form
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
            challenge = FormAuthChallenge.Redirect { SiteURLS.loginUrl() }

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