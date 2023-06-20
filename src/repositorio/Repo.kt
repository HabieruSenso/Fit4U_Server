package com.example.repositorio

import com.example.data.modelo.Nota
import com.example.data.modelo.Usuario
import com.example.data.tabla.TablaNota
import com.example.data.tabla.TablaUsuario
import com.example.repositorio.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

// necesario para guardar los datos en la base de datos en sus respectiblas tablas

class Repo {

    // el nombre de la funcion indica su funcion...

    suspend fun anadirUsuario(user:Usuario){
        dbQuery{
            TablaUsuario.insert { ut->
                ut[TablaUsuario.email] = user.email
                ut[TablaUsuario.hashContrasena] = user.hashContrasena
                ut[TablaUsuario.nombre] = user.nombreUsuario
            }
        }
    }

    // el nombre de la funcion indica su funcion...

    suspend fun buscarUsuarioPorMail (email:String) = dbQuery {
        TablaUsuario.select { TablaUsuario.email.eq(email) }
            .map { filaParaUsuario(it) }
            .singleOrNull()
    }

    // el nombre de la funcion indica su funcion...

    private fun filaParaUsuario(row:ResultRow?):Usuario?{
        if(row == null){
            return null
        }

        return Usuario(
            email =  row[TablaUsuario.email],
            hashContrasena = row[TablaUsuario.hashContrasena],
            nombreUsuario = row[TablaUsuario.nombre]
        )
    }


    // ==Notas==

    // el nombre de la funcion indica su funcion...

    suspend fun anadirNota(note:Nota,email: String){
        dbQuery {
            TablaNota.insert { nt->
                nt[TablaNota.id] = note.id
                nt[TablaNota.usuarioEmail] = email
                nt[TablaNota.notaTitulo] = note.notaTitulo
                nt[TablaNota.descripcion] = note.descripcion
                nt[TablaNota.fecha] = note.fecha
            }

        }

    }

    // el nombre de la funcion indica su funcion...

    suspend fun mostrarTodasNotas(email:String):List<Nota> = dbQuery {
        TablaNota.select {
            TablaNota.usuarioEmail.eq(email)
        }.mapNotNull { filaParaNota(it) }

    }

    // el nombre de la funcion indica su funcion...

    suspend fun modificarNota(note:Nota,email: String){
        dbQuery {
            TablaNota.update(
                where = {
                    TablaNota.usuarioEmail.eq(email) and TablaNota.id.eq(note.id)
                }
            ){ nt->
                nt[TablaNota.notaTitulo] = note.notaTitulo
                nt[TablaNota.descripcion] = note.descripcion
                nt[TablaNota.fecha] = note.fecha
            }
        }
    }

    // el nombre de la funcion indica su funcion...

    suspend fun borrarNota(id:String,email: String){
        dbQuery {
            TablaNota.deleteWhere { TablaNota.usuarioEmail.eq(email) and TablaNota.id.eq(id) }
        }
    }

    // el nombre de la funcion indica su funcion...

    private fun filaParaNota(row:ResultRow?): Nota? {
        if(row == null){
            return null
        }
        return Nota(
            id = row[TablaNota.id],
            notaTitulo = row[TablaNota.notaTitulo],
            descripcion =  row[TablaNota.descripcion],
            fecha = row[TablaNota.fecha]
        )
    }
}