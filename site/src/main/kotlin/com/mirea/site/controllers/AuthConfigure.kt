package com.mirea.site.controllers

import com.mirea.common.JwtCommon
import com.mirea.common.JwtSession
import com.mirea.common.toPrincipal
import com.mirea.mongo.dao.UserDao
import com.mirea.site.common.SiteURLS
import com.mirea.site.common.getPrincipalFromSession
import com.mirea.site.common.kodein
import io.ktor.auth.Authentication
import io.ktor.auth.FormAuthChallenge
import io.ktor.auth.form
import io.ktor.request.uri
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.kodein.di.generic.instance
import org.mindrot.jbcrypt.BCrypt

object AuthConfigure {
    private val userDao by kodein.instance<UserDao>()

    val configure: Authentication.Configuration.() -> Unit = {
        form {
            passwordParamName = "password"
            userParamName = "name"
            challenge = FormAuthChallenge.Redirect {
                if (this.request.uri == SiteURLS.loginUrl()) SiteURLS.loginUrl(loginError = true)
                else SiteURLS.loginUrl()
            }

            skipWhen { call ->
                call.getPrincipalFromSession() != null
            }
            validate { creds ->
                val q = creds.name
                userDao.getConfirmedByEmailOrName(creds.name)?.let { user ->
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