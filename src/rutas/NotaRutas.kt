package com.example.rutas

import com.example.data.modelo.Nota
import com.example.data.modelo.Respuestas
import com.example.data.modelo.Usuario
import com.example.repositorio.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.lang.Exception


const val NOTAS = "$API_VERSION/notas"
const val CREACION_NOTAS = "$NOTAS/crear"
const val MODIFICAR_NOTAS = "$NOTAS/modificar"
const val BORRAR_NOTAS = "$NOTAS/borrar"

@Location(CREACION_NOTAS)
class NotaCreacionRuta

@Location(NOTAS)
class NoteVerRuta

@Location(MODIFICAR_NOTAS)
class NotaModificarRuta

@Location(BORRAR_NOTAS)
class NotaBorrarRuta

fun Route.NotaRutas(
    db: Repo,
    hashFunction: (String)->String
) {

    authenticate("jwt"){

        post<NotaCreacionRuta>{

            val nota = try {
                call.receive<Nota>()
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Campos vacios"))
                return@post
            }
            try {
                val email = call.principal<Usuario>()!!.email
                db.anadirNota(nota,email)
                call.respond(HttpStatusCode.OK,Respuestas(true,"Nota anadida satisfactoriamente"))

            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict,Respuestas(false,e.message ?: "Notas no se anadio correctamente"))
            }

        }

        get<NoteVerRuta>{

            try {
                val email = call.principal<Usuario>()!!.email
                val notas = db.mostrarTodasNotas(email)
                call.respond(HttpStatusCode.OK,notas)
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, emptyList<Nota>())
            }
        }

        post<NotaModificarRuta> {

            val nota = try {
                call.receive<Nota>()
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Campos vacios"))
                return@post
            }
            try {
                val email = call.principal<Usuario>()!!.email
                db.modificarNota(nota,email)
                call.respond(HttpStatusCode.OK,Respuestas(true,"La nota se modifico satisfactoriamente"))
            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict,Respuestas(false,e.message ?: "La nota no se modifico correctamente"))
            }
        }

        delete<NotaBorrarRuta> {

            val notaId = try{
                call.request.queryParameters["id"]!!
            }catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest,Respuestas(false,"Id de nota no se encuentra"))
                return@delete
            }
            try {
                val email = call.principal<Usuario>()!!.email
                db.borrarNota(notaId,email)
                call.respond(HttpStatusCode.OK,Respuestas(true,"Nota borrada correctamente"))

            } catch (e: Exception){
                call.respond(HttpStatusCode.Conflict,Respuestas(false, e.message ?: "Hubo problemas a la hora de borrar la nota"))
            }
        }
    }
}