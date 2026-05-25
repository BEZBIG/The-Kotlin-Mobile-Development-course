package com.example.module6taskspart4.data.repository

import com.example.module6taskspart4.data.database.LaureateTable
import com.example.module6taskspart4.data.database.PrizeTable
import com.example.module6taskspart4.data.database.UserPrizeTable
import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.model.NobelPrize
import com.example.module6taskspart4.domain.repository.PrizeRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PrizeRepositoryImpl : PrizeRepository {

    // Достаём все премии из БД
    override fun getAllPrizes(): List<NobelPrize> = transaction {
        PrizeTable.selectAll().map { row ->
            rowToPrize(row)
        }
    }

    // Ищем премию по году и категории
    override fun getPrize(year: String, category: String): NobelPrize? = transaction {
        PrizeTable.selectAll()
            .where { (PrizeTable.awardYear eq year) and (PrizeTable.category eq category) }
            .firstOrNull()
            ?.let { rowToPrize(it) }
    }

    // Достаём лауреатов конкретной премии
    override fun getLaureates(year: String, category: String): List<Laureate> = transaction {
        val prize = PrizeTable.selectAll()
            .where { (PrizeTable.awardYear eq year) and (PrizeTable.category eq category) }
            .firstOrNull() ?: return@transaction emptyList()

        LaureateTable.selectAll()
            .where { LaureateTable.prizeId eq prize[PrizeTable.id] }
            .map { row ->
                Laureate(
                    id = row[LaureateTable.id].value.toString(),
                    name = row[LaureateTable.fullName],
                    motivation = row[LaureateTable.motivation] ?: "",
                    share = row[LaureateTable.portion]
                )
            }
    }

    // Сохраняем премию и её лауреатов в БД
    override fun savePrize(prize: NobelPrize): Int = transaction {
        val prizeId = PrizeTable.insertAndGetId {
            it[awardYear] = prize.year
            it[category] = prize.category
        }.value

        prize.laureates.forEach { laureate ->
            LaureateTable.insert {
                it[LaureateTable.prizeId] = prizeId
                it[fullName] = laureate.name
                it[portion] = laureate.share
                it[motivation] = laureate.motivation
            }
        }
        prizeId
    }

    // Получаем избранные премии пользователя
    override fun getFavorites(userId: Int): List<NobelPrize> = transaction {
        (UserPrizeTable innerJoin PrizeTable)
            .selectAll()
            .where { UserPrizeTable.userId eq userId }
            .map { rowToPrize(it) }
    }

    // Добавляем премию в избранное
    override fun addFavorite(userId: Int, prizeId: Int) = transaction {
        UserPrizeTable.insertIgnore {
            it[UserPrizeTable.userId] = userId
            it[UserPrizeTable.prizeId] = prizeId
        }
        Unit
    }

    // Удаляем премию из избранного
    override fun removeFavorite(userId: Int, prizeId: Int) = transaction {
        UserPrizeTable.deleteWhere {
            (UserPrizeTable.userId eq userId) and (UserPrizeTable.prizeId eq prizeId)
        }
        Unit
    }

    // Конвертируем строку БД в доменную модель
    private fun rowToPrize(row: ResultRow): NobelPrize {
        val prizeId = row[PrizeTable.id].value
        val laureates = LaureateTable.selectAll()
            .where { LaureateTable.prizeId eq prizeId }
            .map { lr ->
                Laureate(
                    id = lr[LaureateTable.id].value.toString(),
                    name = lr[LaureateTable.fullName],
                    motivation = lr[LaureateTable.motivation] ?: "",
                    share = lr[LaureateTable.portion]
                )
            }
        return NobelPrize(
            year = row[PrizeTable.awardYear],
            category = row[PrizeTable.category],
            laureates = laureates
        )
    }
}