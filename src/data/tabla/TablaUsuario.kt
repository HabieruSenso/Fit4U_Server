package com.example.data.tabla

import org.jetbrains.exposed.sql.Table

// detallamos comoq ueremos que nos cree la tabla usuario e indicamos su primary key.

object TablaUsuario: Table() {

    val email = varchar("email",512)
    val nombre = varchar("nombre",512)
    val hashContrasena = varchar("hashContrasena",512)

    override val primaryKey: PrimaryKey = PrimaryKey(email)
}