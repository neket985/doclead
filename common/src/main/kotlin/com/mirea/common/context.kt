package com.mirea.common

import com.mirea.mongo.entity.User
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import javax.naming.AuthenticationException

fun User.toPrincipal() = UserPrincipal(this.name, this.email, this._id!!)

fun ApplicationCall.getPrincipal() = this.principal<UserPrincipal>() ?: throw AuthenticationException()

suspend fun <T : Any> PipelineContext<Unit, ApplicationCall>.api(data: suspend () -> T) = call.respond(data())
