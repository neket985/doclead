package com.mirea.site.common

import com.mirea.common.UserPrincipal
import com.mirea.site.pebble.PebbleModule.render
import com.mitchellbosecke.pebble.PebbleEngine
import io.ktor.application.ApplicationCall
import io.ktor.auth.principal
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.util.toMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.kodein.di.generic.instance
import java.io.InputStream
import java.io.OutputStream

private val engine by kodein.instance<PebbleEngine>()

suspend fun ApplicationCall.render(template: String, vararg params: Pair<String, Any?>) =
        this.respondText(ContentType.parse("text/html"), HttpStatusCode.OK) {
            val queryParams = this.request.queryParameters.toMap().map {
                it.key to it.value.firstOrNull()
            }
            val allParams = queryParams
                    .plus(params)
                    .plus("URLS" to SiteURLS)
                    .plus("principal" to this.principal<UserPrincipal>())
                    .toTypedArray()

            engine.render(template, *allParams)
        }


suspend fun InputStream.copyToSuspend(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        yieldSize: Int = 10 * 1024 * 1024,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}