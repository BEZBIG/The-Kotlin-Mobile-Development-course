package com.example.module6taskspart4.data.database

import at.favre.lib.crypto.bcrypt.BCrypt
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val config = HikariConfig().apply {
            // Передаём параметры отдельно — так драйвер не путается со спецсимволами
            jdbcUrl = "jdbc:postgresql://ep-broad-sea-ab56zy2a.eu-west-2.aws.neon.tech/neondb"
            driverClassName = "org.postgresql.Driver"
            username = "neondb_owner"
            password = "npg_TnN1aAdJ9rhx"
            maximumPoolSize = 5
            minimumIdle = 1
            idleTimeout = 300000
            maxLifetime = 1800000
            connectionTimeout = 30000
            // SSL обязателен для neon.tech
            addDataSourceProperty("sslmode", "require")
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        // Создаём таблицы если их нет
        transaction {
            SchemaUtils.create(
                UserTable,
                PrizeTable,
                LaureateTable,
                UserPrizeTable
            )
        }

        // Создаём admin если его нет
        transaction {
            val exists = UserTable.selectAll()
                .where { UserTable.username eq "admin" }
                .count() > 0
            if (!exists) {
                val hash = BCrypt.withDefaults()
                    .hashToString(12, "password123".toCharArray())
                UserTable.insert {
                    it[username] = "admin"
                    it[passwordHash] = hash
                    it[role] = "admin"
                }
            }
        }
    }
}