package com.example.module6taskspart4.data.repository

import com.example.module6taskspart4.data.database.UserTable
import com.example.module6taskspart4.domain.model.User
import com.example.module6taskspart4.domain.repository.UserRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {

    // Ищем пользователя по имени, возвращаем вместе с хэшем пароля
    override fun findByUsername(username: String): Pair<User, String>? = transaction {
        UserTable.selectAll()
            .where { UserTable.username eq username }
            .firstOrNull()
            ?.let { row ->
                val user = User(
                    id = row[UserTable.id].value,
                    username = row[UserTable.username],
                    role = row[UserTable.role]
                )
                Pair(user, row[UserTable.passwordHash])
            }
    }

    // Создаём нового пользователя
    override fun createUser(username: String, passwordHash: String, role: String): User = transaction {
        val id = UserTable.insert {
            it[UserTable.username] = username
            it[UserTable.passwordHash] = passwordHash
            it[UserTable.role] = role
        }[UserTable.id].value

        User(id = id, username = username, role = role)
    }
}