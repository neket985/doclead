package com.mirea.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.mirea.api.controllers.ProjectController
import com.mirea.common.ApiError
import com.mirea.common.ApiError.Companion.apiError
import com.mirea.common.JwtCommon
import com.mirea.common.JwtCommon.toPrincipal
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import org.bson.types.ObjectId
import java.time.Duration
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

        install(CORS)
        {
            method(HttpMethod.Options)
            header(HttpHeaders.XForwardedProto)
            header(HttpHeaders.Authorization)
            anyHost()
        }

        install(ContentNegotiation) {
            jackson {
                val module = SimpleModule()
                        .addSerializer(ApiError::class.java, object : JsonSerializer<ApiError>() {
                            override fun serialize(value: ApiError, jgen: JsonGenerator, p2: SerializerProvider?) {
                                jgen.writeStartObject()
                                jgen.writeNumberField("code", value.code)
                                jgen.writeStringField("desc", value.desc)
                                jgen.writeEndObject()
                            }

                        })
                        .addSerializer(ObjectId::class.java, object : JsonSerializer<ObjectId>() {
                            override fun serialize(value: ObjectId, jgen: JsonGenerator, p2: SerializerProvider?) {
                                jgen.writeString(value.toHexString())
                            }
                        })
                        .addDeserializer(ObjectId::class.java, object : JsonDeserializer<ObjectId>() {
                            override fun deserialize(jp: JsonParser, p1: DeserializationContext?): ObjectId {
                                val hexId = jp.text
                                return ObjectId(hexId)
                            }
                        })
                registerModule(module)
            }
        }

        routing {
            authenticate {
                route("project", ProjectController.route)
            }
        }
        install(StatusPages) {
            status(HttpStatusCode.Unauthorized) {
                apiError(401, "Unauthorized")
            }
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<ApiError> { cause ->
                call.respond(cause)
            }
        }
    }
}