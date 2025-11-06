/*
Определены два класса: Point (точка с координатами x, y) и CloudOfPoints (набор точек Point).

internal class Point(var x:Int, var y:Int)
internal class CloudOfPoints(val points: ArrayList<Point>)

Дополните классы новой функциональностью - реализуйте интерфейс Movable (метод move(dx, dy) добавляет dx к координате x, а dy к координате y).
Необходимо написать фрагмент программы: полное описание классов с реализованным интерфейсом.

interface Movable {
    fun move( dx: Int, dy: Int)
}

// Ваш фрагмент будет помещён здесь
*/

package com.module_1


interface Movable {
    fun move( dx: Int, dy: Int)
}

internal class Point(var x: Int, var y: Int) : Movable {
    override fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }
}

internal class CloudOfPoints(val points: ArrayList<Point>) : Movable {
    override fun move(dx: Int, dy: Int) {
        for (point in points) {
            point.move(dx, dy)
        }
    }
}