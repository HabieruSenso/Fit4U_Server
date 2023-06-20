package com.example.rutas

import com.example.autenticacion.JwtService
import com.example.data.modelo.Login
import com.example.data.modelo.Registro
import com.example.data.modelo.Respuestas
import com.example.data.modelo.Usuario
import com.example.repositorio.Repo
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.Exception


// escribimos la ruta que se tendrian que cumplir

const val API_VERSION = "/v1"
const val USUARIOS = "$API_VERSION/usuarios"
const val REGISTRO_PETICION = "$USUARIOS/registro"
const val LOGIN_PETICION = "$USUARIOS/login"

@Location(REGISTRO_PETICION)
class UsuarioRegistroRuta

@Location(LOGIN_PETICION)
class UsuarioLoginRuta


// clase de mensajes que se generarian segun el acierto o error.

fun Route.UsuarioRutas(
    db: Repo,
    jwtService: JwtService,
    hashFunction: (String)->String
){

    post<UsuarioRegistroRuta> {
        val registroPeticion = try {
            call.receive<Registro>()
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Faltan algunos datos"))
            return@post
        }

        try {
            val usuario = Usuario(registroPeticion.email,hashFunction(registroPeticion.contrasena),registroPeticion.nombre)
            db.anadirUsuario(usuario)
            call.respond(HttpStatusCode.OK,Respuestas(true,jwtService.generarToken(usuario)))
        }catch (e: Exception){
            call.respond(HttpStatusCode.Conflict,Respuestas(false,e.message ?: "Se vienen cositas!"))
        }
    }

    post<UsuarioLoginRuta> {
        val loginPeticion = try {

            call.receive<Login>()
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Faltan datos"))
            return@post
        }

        try {
            val usuario = db.buscarUsuarioPorMail(loginPeticion.email)

            if(usuario == null){
                call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Email Id Incorrecto"))
            } else {

                if(usuario.hashContrasena == hashFunction(loginPeticion.contrasena)){
                    call.respond(HttpStatusCode.OK,Respuestas(true,jwtService.generarToken(usuario)))
                } else{
                    call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Contrasena incorrecta"))
                }
            }
        } catch (e: Exception){
            call.respond(HttpStatusCode.Conflict,Respuestas(false,e.message ?: "Se vienen cositas"))
        }
    }
}