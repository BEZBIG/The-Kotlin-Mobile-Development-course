/*
ДНК состоит из 4 типов нуклеотидов: A (аденин), T (тимин), G (гуанин), C (цитозин).
Ваша программа получает на вход строку вида ATGCCTCTCTC и должна посчитать число нуклеотидов каждого типа
(вывести числа через пробел в порядке как в строке выше).

Правильным по форме, но не по содержанию является ответ вида

fun main() {
    println("0 0 0 0")
}

Для примера:

Ввод	      Результат
ATGCCTCTCTC   1 4 1 5
*/

package com.module_1

fun main() {
    val dna: String = readLine()!!.trim()
    val a: Int = dna.count { it == 'A' }
    val t: Int = dna.count { it == 'T' }
    val g: Int = dna.count { it == 'G' }
    val c: Int = dna.count { it == 'C' }

    println("$a $t $g $c")
}