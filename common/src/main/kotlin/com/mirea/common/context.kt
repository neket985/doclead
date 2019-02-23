package com.mirea.common

import com.mirea.mongo.entity.User
import io.ktor.application.ApplicationCall
import io.ktor.auth.principal
import javax.naming.AuthenticationException

fun User.toPrincipal() = UserPrincipal(this.email, this._id!!)

fun ApplicationCall.getPrincipal() = this.principal<UserPrincipal>() ?: throw AuthenticationException()