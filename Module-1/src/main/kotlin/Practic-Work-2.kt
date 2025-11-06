/*Напишите функцию countVowels подсчитывающую число гласных английских букв (a, e, i, o, u) в строке у,
которая передаётся в виде параметра*/

package com.module_1

fun countVowels(input: String): Int {
    var result: Int = 0
    for (i in input) {
        if (i in arrayOf('a', 'e', 'i', 'o', 'u')) {
            result++
        }
    }
    return result
}
