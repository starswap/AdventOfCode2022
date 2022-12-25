import java.io.File

operator fun Pair<Int,Int>.plus(other: Pair<Int,Int>):Pair<Int,Int> {
    return Pair(this.first+other.first, this.second+other.second)
}

fun <A>MutableList<A>.cycle() {
    this.add(this[0])
    this.removeAt(0)
}

class Day23(lines: List<String>) {
    private val canDirections: MutableList<(Int, Int)-> Boolean> = mutableListOf(::canNorth,::canSouth,::canWest,::canEast)
    private val deltas: MutableList<Pair<Int,Int>> = mutableListOf(Pair(-1,0),Pair(1,0),Pair(0,-1),Pair(0,1))
    private val elves = mutableSetOf<Pair<Int, Int>>()

    private fun canNorth(row: Int, col: Int): Boolean = !(Pair(row-1,col) in elves || Pair(row-1,col+1) in elves || Pair(row-1,col-1) in elves)
    private fun canSouth(row: Int, col: Int): Boolean = !(Pair(row+1,col) in elves || Pair(row+1,col+1) in elves || Pair(row+1,col-1) in elves)
    private fun canEast(row: Int, col: Int): Boolean = !(Pair(row,col+1) in elves || Pair(row+1,col+1) in elves || Pair(row-1,col+1) in elves)
    private fun canWest(row: Int, col: Int): Boolean = !(Pair(row,col-1) in elves || Pair(row+1,col-1) in elves || Pair(row-1,col-1) in elves)

    init {
        for ((r,line) in lines.withIndex())
            for ((c,char) in line.withIndex())
                if (char == '#') elves.add(Pair(r,c))
    }


    private fun propose(curr: Pair<Int,Int>): Pair<Int,Int> {
        val (row, col) = curr
        if (canNorth(row,col) && canWest(row, col) && canEast(row,col) && canSouth(row,col)) return Pair(row,col) // don't move if all free around
        else {
            for ((i,dir) in canDirections.withIndex()) { // move in preferred order
                if (dir(row, col)) return Pair(row,col) + deltas[i]
            }
        }
        return Pair(row,col)
    }

    private fun moveElves(): Boolean {
        val proposed = elves.groupBy(::propose) // if only we could uncurry...
        var noOneMoved = true

        for ((movedTo, thoseElves) in proposed) {
            if (thoseElves.size == 1) { // don't move anyone who said the same as someone else
                elves.remove(thoseElves.first())
                elves.add(movedTo)
                if (movedTo != thoseElves.first()) // actually moved; some people end up proposing their current position
                    noOneMoved = false
            }
        }

        canDirections.cycle()
        deltas.cycle()

        return noOneMoved
    }

    // find the number of empty squares within the smallest rectangular box containing all elves
    private fun count():Int = ((elves.maxOf { it.first } - elves.minOf { it.first } + 1)*(1 + elves.maxOf { it.second } - elves.minOf { it.second }))- elves.size

    // for debugging
    private fun depict() {
        for (r in elves.minOf { it.first }..elves.maxOf { it.first }) {
            for (c in elves.minOf { it.second }..elves.maxOf { it.second }) {
                print(if (Pair(r,c) in elves) '#' else '.')
            }
            println()
        }
        println()
    }

    fun part1(): Int {
        repeat(10) {
            moveElves()
        }
        return count()
    }
    fun part2(): Int {
        var roundNo = 0
        var noOneMoved = false
        while (!noOneMoved) {
            noOneMoved = moveElves()
            roundNo++
        }
        return roundNo
    }
}

fun main() {
    val d23 = Day23(File("input/Day23.txt").readLines())
    println("Part 1 ans: ${d23.part1()}")

    val d23a = Day23(File("input/Day23.txt").readLines())
    println("Part 2 ans: ${d23a.part2()}")
}
