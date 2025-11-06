/*
Используя класс для управления роботом (Robot), разработанный в предыдущем задании, реализуйте функцию
moveRobot(r: Robot, toX: Int, toY: Int), перемещающую объект типа Robot с помощью методов
turnLeft(), turnRight(), stepForward() в заданную точку (toX, toY).

В качестве решения сообщите только код функции moveRobot, классы Direction и Robot уже определены.

Для примера:

Тест	                                Результат
val r = Robot(0,1,Direction.UP)         3 7
moveRobot(r, 3, 7)
println("${r.x} ${r.y}")
*/

package com.module_1


fun moveRobot(r: Robot, toX: Int, toY: Int) {
    // Двигаемся по оси X
    while (r.x != toX) {
        // Поворачиваем в нужном направлении для движения по X
        if (r.x < toX) {
            // Нужно двигаться вправо
            while (r.direction != Direction.RIGHT) {
                r.turnRight()
            }
        } else {
            // Нужно двигаться влево
            while (r.direction != Direction.LEFT) {
                r.turnRight()
            }
        }
        r.stepForward()
    }

    // Двигаемся по оси Y
    while (r.y != toY) {
        // Поворачиваем в нужном направлении для движения по Y
        if (r.y < toY) {
            // Нужно двигаться вверх
            while (r.direction != Direction.UP) {
                r.turnRight()
            }
        } else {
            // Нужно двигаться вниз
            while (r.direction != Direction.DOWN) {
                r.turnRight()
            }
        }
        r.stepForward()
    }
}