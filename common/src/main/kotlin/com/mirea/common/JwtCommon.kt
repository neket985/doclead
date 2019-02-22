package com.mirea.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.typesafe.config.ConfigFactory
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
                .withClaim("name", user.name)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expSecs)))
                .sign(algorithm)
    }

    val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()

    fun Payload.toPrincipal() =
            UserPrincipal(this.getClaim("name").asString())

    private val logger = LoggerFactory.getLogger(JwtCommon::class.java)
}