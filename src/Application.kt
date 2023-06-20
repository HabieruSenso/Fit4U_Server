package com.example

import com.example.autenticacion.JwtService
import com.example.autenticacion.hash
import com.example.repositorio.DatabaseFactory
import com.example.repositorio.Repo
import com.example.rutas.NotaRutas
import com.example.rutas.UsuarioRutas
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.locations.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads

//Funcion principal, donde se especifica lo relacionado con la api

fun Application.module(testing: Boolean = false) {

    // inializa la db
    DatabaseFactory.init()
    // iniciamos el servicio de jwt
    val db = Repo()
    val jwtService = JwtService()
    val hashFunction = { s:String -> hash(s) }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // esto es para la parte de identificacion

    install(Authentication) {

        // implementamos la autentificacion de jwt

        jwt("jwt") {

            verifier(jwtService.verificador)
            realm = "Note Server"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()
                val usuario = db.buscarUsuarioPorMail(email)
                usuario
            }

        }

    }

    // instalamos lcoations

    install(Locations)

    install(ContentNegotiation) {
        gson {
        }
    }

    // Las rutas se añaden en routing

    routing {
        get("/") {
            call.respondText("HOLA IVAN!", contentType = ContentType.Text.Plain)
        }
        // rutas creadas en paquetes aparte
        UsuarioRutas(db,jwtService,hashFunction)
        NotaRutas(db, hashFunction)

        // Obtenemos notas por id

        get("/nota/{id}") {
            val id = call.parameters["id"]
            call.respond("${id}")
        }

        //obtenemos notas

        get("/nota"){
            val id = call.request.queryParameters["id"]
            call.respond("${id}")
        }

        // las rutas hacen el codigo mas facil,  lo que metas en ruta solo ocurrira cumpliendo los requisitos que pongas en esa ruta

        route("/notas"){

            route("/crear") {

                // localhost:8081/notas/crear
                // Aqui se crean notas

                post {
                    val body = call.receive<String>()
                    call.respond(body)
                }
            }

            // Aqui se borra

            delete{
                val body = call.receive<String>()
                call.respond(body)
            }
        }
    }
}

data class MySession(val count: Int = 0)