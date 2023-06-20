package com.example.data.modelo

import io.ktor.auth.*

// anadimos los datos de usuario con los que vamos a tratar

data class Usuario(
    val email:String,
    val hashContrasena:String,
    val nombreUsuario:String
): Principal
