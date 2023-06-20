package com.example.autenticacion

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.modelo.Usuario

// iniciamos el proceso de encriptacion de las contraseñas usando tecnologia JWT

class JwtService {

    private val issuer = "gymAppServer"
    //accedemos variables del sistema
    private val jwtSecret = System.getenv("JWT_SECRET")
    // hacemos la signature
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    // indica el tipo de verificador

    val verificador: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    // indica de donde nos va a encriptar, en este caso de la tabla usuario . Se podrian encriptar notas

    fun generarToken(user:Usuario):String {
        return JWT.create()
            .withSubject("NOteAuthentication")
            .withIssuer(issuer)
            .withClaim("email",user.email)
            .sign(algorithm)
    }
}