package com.mirea.doclead.common

import com.mirea.doclead.PebbleModule.render
import com.mitchellbosecke.pebble.PebbleEngine
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import org.kodein.di.generic.instance

private val engine by kodein.instance<PebbleEngine>()

suspend fun ApplicationCall.render(template: String, vararg params: Pair<String, Any?>) =
        this.respondText(ContentType.parse("text/html"), HttpStatusCode.OK) {
            engine.render(template, *params)
        }