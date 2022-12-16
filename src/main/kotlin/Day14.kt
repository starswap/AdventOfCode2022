import java.io.File
import kotlin.math.max
import kotlin.math.min

enum class Square {
    SAND, AIR, ROCK
}

class Day14(lines: List<String>) {
    private val grid = Array<Array<Square>>(1000) { Array<Square>(1000) { Square.AIR } }
    private var maxRock: Int = -100000
    init {
        for (line in lines) {
            line.split(" -> ").map { coords ->
                Pair(coords.split(",").map(String::toInt)[0], coords.split(",").map(String::toInt)[1])
            }.zipWithNext { (x1, y1), (x2, y2) ->
                (min(x1,x2)..max(x1,x2)).forEach { xc ->
                    (min(y1,y2)..max(y1,y2)).forEach { yc -> grid[yc][xc] = Square.ROCK ; maxRock = max(maxRock, yc)}
                }
            }
        }
    }

    private fun printGrid() {
        for (row in grid) {
            for (col in row) {
                print(when (col) {
                    Square.AIR -> '.'
                    Square.SAND -> 'o'
                    Square.ROCK -> '#'
                })
            }
            println()
        }
    }

    fun part1(): Int {
        var currX = 500
        var currY = 0
        var totalPlaced = 0
        while (currY <= maxRock) {
            if (grid[currY+1][currX] == Square.AIR)
                currY++
            else if (grid[currY+1][currX-1] == Square.AIR) {
                currY++
                currX--
            }
            else if (grid[currY+1][currX+1] == Square.AIR) {
                currY++
                currX++
            }
            else {
                grid[currY][currX] = Square.SAND
                totalPlaced++
                currX = 500
                currY = 0
            }
        }
        return totalPlaced
    }

    fun part2(): Int {
        var currX = 500
        var currY = 0
        var totalPlaced = 0
        while (true) {
            if (grid[currY+1][currX] == Square.AIR && currY+1 != maxRock+2)
                currY++
            else if (grid[currY+1][currX-1] == Square.AIR && currY+1 != maxRock+2) {
                currY++
                currX--
            }
            else if (grid[currY+1][currX+1] == Square.AIR && currY+1 != maxRock+2) {
                currY++
                currX++
            }
            else {
                grid[currY][currX] = Square.SAND
                totalPlaced++
                if (currX == 500 && currY == 0)
                    break
                else {
                    currX = 500
                    currY = 0
                }
            }
        }
        return totalPlaced
    }}

fun main() {
    val d14 = Day14(File("input/Day14.txt").readLines())
    println("Part 1 ans: ${d14.part1()}")

    val d14a = Day14(File("input/Day14.txt").readLines())
    println("Part 2 ans: ${d14a.part2()}")
}
