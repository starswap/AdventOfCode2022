import java.io.File
import kotlin.math.max

fun <A> KMP(string: List<A>, pattern: List<A>): MutableList<Int> {
    val lps = Array<Int>(pattern.size) { 0 }
    val occs = mutableListOf<Int>()
    var i = 0
    var j = 1
    while (j < lps.size) {
        if (pattern[i] != pattern[j]) {
            j += 1
            if (i > 0) {
                i = lps[i-1]
            }
        }
        else {
            lps[j] = i+1
            i += 1
            j += 1
        }
    }
    var s = 0
    var p = 0
    while (s < string.size) {
        if (p >= pattern.size || string[s] != pattern[p] && p > 0) {
            p = lps[p-1]
        }
        else {
            s += 1
            p += 1
        }
        if (p == lps.size) {
            occs.add(s-pattern.size)
        }
    }
    return occs
}


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
data class Coord(val x: Long, val y: Long) {
    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)
}
class Rock(private val type: RockType, private val bottomLeft: Coord) {
    constructor(type: RockType, highestRock: Long): this(type, Coord(2,highestRock + 4))

    fun moveLeft() = Rock(type, bottomLeft + Coord(-1,0))
    fun moveRight() = Rock(type, bottomLeft + Coord(1,0))
    fun moveDown() = Rock(type, bottomLeft + Coord(0,-1))

    fun presentSquares(): List<Coord> = type.rockLocs.map { bottomLeft + it }
    fun maxHeight(): Long = presentSquares().maxOf { it.y }
}

class Day17(private val cmnds: String) {
    private val squaresTaken = mutableSetOf<Coord>()
    private val WIDTH = 7
    private var maxHeight: Long = 0
    private val deltas = mutableListOf<Long>()
    private fun isValid(rock: Rock) = !(rock.presentSquares().any {
        squaresTaken.contains(it) || it.x < 0 || it.x >= WIDTH
    })
    private fun save(rock: Rock) {squaresTaken.addAll(rock.presentSquares())}

    fun part1() = placeRocks(2022)
    fun part2(): Long  {
        // It's clear that the delta in maxheight must cycle or else we can't solve the problem
        // We could try to think about what would cause this to happen
        // Or we could use pattern matching
        // Try some arbitrary number of rocks (10000) which should be enough for the pattern to emerge
        // Take the last 30 deltas (WLOG) and find the last two occurrences of that pattern of 30 deltas within all the deltas
        // The distance between these two is the pattern that repeats
        // We can then scale the pattern up to the target number of cycles

        val target: Long = 1000000000000 // Number of rocks we want to drop
        val tried: Int = 100000 // Needs to be long enough for a complete occurrence of the pattern, plus a few characters
        val tryPattern: Int = 30 // less than the amount of patternated text we have; not so small that we get repeats without a pattern

        placeRocks(tried)
        val (firstOcc, sndOcc) = KMP(deltas,deltas.takeLast(tryPattern)).takeLast(2)
        val patternLen = sndOcc - firstOcc
        val patternOccs: Long = ((target - tried.toLong())/patternLen)
        val onlyUseful = deltas.map { if (it >= 0) it else 0 }
        val pattern = onlyUseful.drop(firstOcc + tryPattern).take(patternLen)
        maxHeight += pattern.sum()*patternOccs
        val remainingAdds = (target - tried)% patternLen
        maxHeight += pattern.take(remainingAdds.toInt()).sum()
        return maxHeight
    }
    private fun placeRocks(totalRocks: Int): Long {
        var rocksPlaced = 0
        var currentType = RockType.MINUS
        var cmndsPtr = 0
        maxHeight = 0
        deltas.clear()
        squaresTaken.clear()

        for (x in 0 until WIDTH) squaresTaken.add(Coord(x.toLong(),0))

        while (rocksPlaced < totalRocks) {
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

            deltas.add(prevRock.maxHeight() - maxHeight)
            maxHeight = max(maxHeight, prevRock.maxHeight())
            rocksPlaced++
            currentType++
        }
        return maxHeight
    }
    fun printGrid() {
        for (y in (maxHeight + 1) downTo 0) {
            print('|')
            for (x in 0 until WIDTH) {
                if (Coord(x.toLong(),y) in squaresTaken) {
                    print('#')
                }
                else {
                    print('.')
                }
            }
            println('|')
        }
        print("---------")
    }
}
fun main() {
    val d17 = Day17(File("input/Day17.txt").readLines()[0])
    println("Part 1 ans: ${d17.part1()}")
    println("Part 2 ans: ${d17.part2()}")
}