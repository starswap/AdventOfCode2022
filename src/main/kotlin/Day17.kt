import java.io.File
import kotlin.math.max

enum class RockType(val rockLocs: List<Coord>) {
    MINUS(listOf(Coord(0,0),Coord(1,0),Coord(2,0),Coord(3,0))) {
        override fun inc() = PLUS
    },
    PLUS(listOf(Coord(0,1),Coord(1,0),Coord(1,1),Coord(1,2),Coord(2,1))) {
        override fun inc() = L
    },
    L(listOf(Coord(0,0),Coord(1,0),Coord(2,0),Coord(2,1),Coord(2,2))) {
        override fun inc() = I
    },
    I(listOf(Coord(0,0),Coord(0,1),Coord(0,2),Coord(0,3))) {
        override fun inc() = SQUARE
    },
    SQUARE(listOf(Coord(0,0),Coord(0,1),Coord(1,0),Coord(1,1))) {
        override fun inc() = MINUS
    };
    abstract operator fun inc(): RockType
}
data class Coord(val x: Int, val y: Int) {
    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)
}
class Rock(private val type: RockType, private val bottomLeft: Coord) {
    constructor(type: RockType, highestRock: Int): this(type, Coord(2,highestRock + 4))

    fun moveLeft() = Rock(type, bottomLeft + Coord(-1,0))
    fun moveRight() = Rock(type, bottomLeft + Coord(1,0))
    fun moveDown() = Rock(type, bottomLeft + Coord(0,-1))

    fun presentSquares(): List<Coord> = type.rockLocs.map { bottomLeft + it }
    fun maxHeight(): Int = presentSquares().maxOf { it.y }
}

class Day17(private val cmnds: String) {
    private val squaresTaken = mutableSetOf<Coord>()
    private val TOTAL_ROCKS = 2022
    private val WIDTH = 7
    private fun isValid(rock: Rock) = !(rock.presentSquares().any {
        squaresTaken.contains(it) || it.x < 0 || it.x >= WIDTH
    })
    private fun save(rock: Rock) {squaresTaken.addAll(rock.presentSquares())}

    fun part1(): Int {
        var rocksPlaced = 0
        var maxHeight = 0
        var currentType = RockType.MINUS
        var cmndsPtr = 0

        squaresTaken.clear()
        for (x in 0 until WIDTH) squaresTaken.add(Coord(x,0))

        while (rocksPlaced < TOTAL_ROCKS) {
            var currRock = Rock(currentType,maxHeight)
            var prevRock: Rock = Rock(currentType,maxHeight)
            var down = false
            while (isValid(currRock)) {
                val prevPrevRock = prevRock
                prevRock = currRock
                if (down) currRock = currRock.moveDown()
                else {
                    if (cmnds[cmndsPtr] == '<') currRock = currRock.moveLeft()
                    else if (cmnds[cmndsPtr] == '>') currRock = currRock.moveRight()
                    cmndsPtr++
                    if (cmndsPtr == cmnds.length) cmndsPtr = 0
                    if (!isValid(currRock)) {
                        currRock = prevRock
                        prevRock = prevPrevRock
                    }
                }
                down = !down
            }
            save(prevRock)

            maxHeight = max(maxHeight, prevRock.maxHeight())
            rocksPlaced++
            currentType++
        }
        return maxHeight
    }
    fun part2(): Int = TODO()
}
fun main() {
    val d17 = Day17(File("input/Day17.txt").readLines()[0])
    println("Part 1 ans: ${d17.part1()}")
//    println("Part 2 ans: ${d17.part2()}")
}