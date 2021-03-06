package com.mirea.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import com.typesafe.config.ConfigFactory
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*


object JwtCommon {
    private val config = ConfigFactory.load().getConfig("jwt")
    private val secret = config.getString("secret")
    private val algorithm = Algorithm.HMAC256(secret)
    private val expSecs = config.getLong("expSecs")

    private val issuer = config.getString("domain")

    fun genJWT(user: UserPrincipal): String {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("email", user.email)
                .withClaim("name", user.name)
                .withClaim("id", user.id.toHexString())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expSecs)))
                .sign(algorithm)
    }

    val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()

    fun Payload.toPrincipal() =
            UserPrincipal(
                    this.getClaim("name").asString(),
                    this.getClaim("email").asString(),
                    ObjectId(this.getClaim("id").asString())
            )

    fun Map<String, Claim>.toPrincipal() =
            UserPrincipal(
                    this["name"]!!.asString(),
                    this["email"]!!.asString(),
                    ObjectId(this["id"]!!.asString())
            )

    private val logger = LoggerFactory.getLogger(JwtCommon::class.java)
}