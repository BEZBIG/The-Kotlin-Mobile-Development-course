/*
Необходимо разработать класс для управления роботом (Robot), содержащий поля координат x, y (тип Int) и направления direction.
Для направления уже определён заранее тип Direction:

enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

Обратите внимание, что мы используем тип enum для указания направления.
Для управления роботом определите методы turnLeft(), turnRight(), stepForward().
Конструктор получает параметры (x, y, direction). Предусмотрите вывод состояния робота методом toString() в виде

x: $x, y: $y, dir: $direction
*/

package com.module_1


enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

class Robot(var x: Int, var y: Int, var direction: Direction) {

    // Поворот налево
    fun turnLeft() {
        direction = when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
            Direction.DOWN -> Direction.RIGHT
            Direction.RIGHT -> Direction.UP
        }
    }

    // Поворот направо
    fun turnRight() {
        direction = when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
    }

    // Шаг вперед в текущем направлении
    fun stepForward() {
        when (direction) {
            Direction.UP -> y++
            Direction.DOWN -> y--
            Direction.RIGHT -> x++
            Direction.LEFT -> x--
        }
    }

    // Строковое представление состояния робота
    override fun toString(): String {
        return "x: $x, y: $y, dir: $direction"
    }
}