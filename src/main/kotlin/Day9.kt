import java.io.File
import javax.swing.text.Segment
import kotlin.math.abs

data class Vec(val dx: Int, val dy: Int) {
    fun divide(i: Int) = Vec(dx/i,dy/i)
    fun signs() = Vec(if (dx > 0) 1 else -1, if (dy > 0) 1 else -1)
}
data class Point(var x: Int, var y: Int) {
    fun transform(v: Vec) {
        x += v.dx
        y += v.dy
    }
    fun deltaTo(other: Point) = Vec(other.x-this.x,other.y-this.y)
}

data class Snake(val sec: List<Point>) {
    private val delta = mapOf<String, Vec>(
        "D" to Vec(0,-1),
        "U" to Vec(0,1),
        "L" to Vec(-1,0),
        "R" to Vec(1,0)
    )
    private fun updateTail() {sec.zipWithNext().forEach(::updateOne)}

    private fun updateOne(p: Pair<Point, Point>) {
        val (head, tail) = p
        val d: Vec = tail.deltaTo(head)
        if (abs(d.dx) > 1 || abs(d.dy) > 1) { // need to catch up
            if (d.dx == 0 || d.dy == 0) { // horizontally or vertically
                tail.transform(d.divide(abs(d.dx + d.dy)))
            } else {
                tail.transform(d.signs())
            }
        }
    }

    fun move(cmd: String) {
        sec[0].transform(delta[cmd]!!)
        updateTail()
    }
}

object Day9 {
    fun part1(lines: List<String>): Int = process(lines, Snake(List(2) { Point(0, 0) }))
    fun part2(lines: List<String>): Int = process(lines, Snake(List(10) { Point(0, 0) }))

    fun process(lines: List<String>, snake: Snake): Int {
        val visited: MutableSet<Point> = mutableSetOf<Point>()
        visited.add(snake.sec.last().copy())
        for (line in lines) {
            val (command, dist) = line.split(" ")
            repeat(dist.toInt()) {
                snake.move(command)
                visited.add(snake.sec.last().copy())
            }
        }
        return visited.size
    }
}

fun main() {
    val lines: List<String> = File("input/Day9.txt").readLines()
    println("Part 1 ans: ${Day9.part1(lines)}")
    println("Part 2 ans: ${Day9.part2(lines)}")
}