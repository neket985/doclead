package com.mirea.site.controllers

import com.mirea.mongo.dao.UserDao
import com.mirea.mongo.entity.User
import com.mirea.site.UserPrincipal
import com.mirea.site.common.EmailSender
import com.mirea.site.common.kodein
import com.mirea.site.common.render
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.kodein.di.generic.instance
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.mindrot.jbcrypt.BCrypt
import java.util.*

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

    val register: Route.() -> Unit = {
        get("") {
            if (context.principal<UserPrincipal>() != null) {
                context.respondRedirect("/")
            } else {
                context.render("register")
            }
        }
        post("") {
            val params = context.receiveParameters()
            val email = params["email"]
            val password = params["password"]
            val passwordConfirm = params["password_confirm"]

            val user = email?.let { userDao.getConfirmedByEmail(it) }

            if (password != passwordConfirm) {
                context.render("register", "error_msg" to "Пароли не совпадают")
            } else if (email == null || password == null) {
                context.render("register", "error_msg" to "Логин или пароль некорректны")
            } else if (user != null) {
                context.render("register", "error_msg" to "Пользователь с таким email уже существует")
            } else {
                val hashPW = BCrypt.hashpw(password, BCrypt.gensalt())
                val uuidForConfirm = UUID.randomUUID().toString()
                val toInsert = User(email, hashPW, false, uuidForConfirm)
                userDao.insert(toInsert)

                EmailSender.send(email, registrationTitle, registrationText + confirmLink + uuidForConfirm)

                context.respondRedirect("/login?msgForConfirm=true")
            }
        }

        get("confirm/{uuid}") {
            val uuid = context.parameters["uuid"]!!
            userDao.updateOne(and(User::confirmed eq false, User::confirmUid eq uuid), set(User::confirmed, true))

            context.respondRedirect("/login?emailConfirm=true")
        }
    }

    private val registrationTitle = "Завершение регистрации"
    private val registrationText = "Для завершения регистрации перейдите по ссылке "
    private val confirmLink = "http://0.0.0.0:8080/register/confirm/"
}