package com.example.repositorio
import com.example.data.tabla.TablaUsuario
import com.example.data.tabla.TablaNota
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

// necesario para conectarse a el servidor postgresql y crear tablas en la db

object DatabaseFactory {

    fun init(){
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(TablaUsuario)
            SchemaUtils.create(TablaNota)
        }

    }

    // Configuramos la base de datos

    fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER") // 1
        config.jdbcUrl = System.getenv("DATABASE_URL") // 2
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        /*  val uri = URI(System.getenv("DATABASE_URL"))
          val username = uri.userInfo.split(":").toTypedArray()[0]
          val password = uri.userInfo.split(":").toTypedArray()[1]

          config.jdbcUrl =
              "jdbc:postgresql://" + uri.host + ":" + uri.port + uri.path + "?sslmode=require" + "&user=$username&password=$password"
  */
        config.validate()

        return HikariDataSource(config)
    }

    // permite querys en la base de datos

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}