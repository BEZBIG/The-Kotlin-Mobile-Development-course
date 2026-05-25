package com.example.module6taskspart4.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.module6taskspart4.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.REALM
            verifier(
                JWT.require(Algorithm.HMAC256(JwtConfig.SECRET))
                    .withIssuer(JwtConfig.ISSUER)
                    .withAudience(JwtConfig.AUDIENCE)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(JwtConfig.AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}