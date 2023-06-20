package com.example.data.tabla

import org.jetbrains.exposed.sql.Table

// se crea tabla notas

object TablaNota: Table() {

    val id = varchar("id",512)
    val usuarioEmail = varchar("usuarioEmail",512).references(TablaUsuario.email)
    val notaTitulo = text("notaTitulo")
    val descripcion = text("descripcion")
    val fecha = long("fecha")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}