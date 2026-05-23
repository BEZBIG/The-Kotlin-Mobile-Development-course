package com.example.module6taskspart4.data.repository

import com.example.module6taskspart4.domain.model.Laureate
import com.example.module6taskspart4.domain.model.NobelPrize
import com.example.module6taskspart4.domain.repository.PrizeRepository

// Данные хранятся в памяти (in-memory)
class PrizeRepositoryImpl : PrizeRepository {

    // Тестовые данные — несколько реальных нобелевских премий
    private val prizes = listOf(
        NobelPrize(
            year = "2023",
            category = "physics",
            laureates = listOf(
                Laureate("1", "Pierre Agostini", "For experimental methods that generate attosecond pulses of light", "3"),
                Laureate("2", "Ferenc Krausz", "For experimental methods that generate attosecond pulses of light", "3"),
                Laureate("3", "Anne L'Huillier", "For experimental methods that generate attosecond pulses of light", "3")
            )
        ),
        NobelPrize(
            year = "2023",
            category = "chemistry",
            laureates = listOf(
                Laureate("4", "Moungi G. Bawendi", "For the discovery and synthesis of quantum dots", "3"),
                Laureate("5", "Louis E. Brus", "For the discovery and synthesis of quantum dots", "3"),
                Laureate("6", "Alexei I. Ekimov", "For the discovery and synthesis of quantum dots", "3")
            )
        ),
        NobelPrize(
            year = "2023",
            category = "medicine",
            laureates = listOf(
                Laureate("7", "Katalin Karikó", "For discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines", "2"),
                Laureate("8", "Drew Weissman", "For discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines", "2")
            )
        ),
        NobelPrize(
            year = "2023",
            category = "literature",
            laureates = listOf(
                Laureate("9", "Jon Fosse", "Who gives voice to the unsayable", "1")
            )
        ),
        NobelPrize(
            year = "2023",
            category = "peace",
            laureates = listOf(
                Laureate("10", "Narges Mohammadi", "For her fight against the oppression of women in Iran", "1")
            )
        ),
        NobelPrize(
            year = "2023",
            category = "economics",
            laureates = listOf(
                Laureate("11", "Claudia Goldin", "For having advanced our understanding of women's labour market outcomes", "1")
            )
        ),
        NobelPrize(
            year = "2022",
            category = "physics",
            laureates = listOf(
                Laureate("12", "Alain Aspect", "For experiments with entangled photons", "3"),
                Laureate("13", "John F. Clauser", "For experiments with entangled photons", "3"),
                Laureate("14", "Anton Zeilinger", "For experiments with entangled photons", "3")
            )
        ),
        NobelPrize(
            year = "2022",
            category = "chemistry",
            laureates = listOf(
                Laureate("15", "Carolyn R. Bertozzi", "For the development of click chemistry and bioorthogonal chemistry", "3"),
                Laureate("16", "Morten Meldal", "For the development of click chemistry and bioorthogonal chemistry", "3"),
                Laureate("17", "K. Barry Sharpless", "For the development of click chemistry and bioorthogonal chemistry", "3")
            )
        ),
        NobelPrize(
            year = "2022",
            category = "peace",
            laureates = listOf(
                Laureate("18", "Ales Bialiatski", "For their efforts to document war crimes", "3"),
                Laureate("19", "Memorial", "Human rights organisation in Russia", "3"),
                Laureate("20", "Center for Civil Liberties", "Human rights organisation in Ukraine", "3")
            )
        )
    )

    override fun getAllPrizes(): List<NobelPrize> = prizes

    override fun getPrize(year: String, category: String): NobelPrize? =
        prizes.find { it.year == year && it.category == category }

    override fun getLaureates(year: String, category: String): List<Laureate> =
        getPrize(year, category)?.laureates ?: emptyList()
}