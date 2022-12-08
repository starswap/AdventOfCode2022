import java.io.File

class Day8(private val lines: List<String>) {
    private val grid: Array<Array<Int>> = Array(lines.size) { i ->
        Array(lines[i].length) { j -> lines[i][j].digitToInt() }
    }
    private val visible: Array<Array<Boolean>> = Array(lines.size) { i ->
        if (i == 0 || i == lines.size - 1) Array(lines[i].length) { true }// all edges are visible by default
        else Array(lines[i].length) { j -> j == 0 || j == lines[i].length-1 }// all internals are not visible
    }
    private val scenicScore: Array<Array<Int>> = Array(lines.size) { i ->
        Array(lines[i].length) { 1 }
    }

    init {
        var highestTreeSoFar = 0
        val lastN = Array<Int>(10) { 0 } // lastN[i] = last index we observed that had a tree of size i or larger

        // Top<->Bottom checks
        for (j in grid[0].indices) { // For each column
            // Top -> Bottom check
            highestTreeSoFar = 0
            lastN.fill(0)
            for (i in grid.indices) {
                // Compute visibility (part 1)
                if (grid[i][j] > highestTreeSoFar) {
                    visible[i][j] = true
                    highestTreeSoFar = grid[i][j]
                }
                // Compute scenic scores (part 2)
                scenicScore[i][j] *= (i - lastN[grid[i][j]])
                for (k in 0 .. grid[i][j])    // last higher index we observed was i
                    lastN[k] = i
            }

            // Top <- Bottom check
            lastN.fill(grid.size - 1)
            highestTreeSoFar = grid[grid.size-1][j]
            for (i in grid.size - 1 downTo 0) { // for each row
                if (grid[i][j] > highestTreeSoFar) {
                    visible[i][j] = true
                    highestTreeSoFar = grid[i][j]
                }
                scenicScore[i][j] *= lastN[grid[i][j]] - i
                for (k in 0 .. grid[i][j])
                    lastN[k] = i
            }
        }

        // Left<->Right checks
        for (i in grid.indices) { // for each row
            highestTreeSoFar = grid[i][0]
            lastN.fill(0)

            for (j in grid[0].indices) { // for each column
                // left to right
                if (grid[i][j] > highestTreeSoFar) {
                    visible[i][j] = true
                    highestTreeSoFar = grid[i][j]
                }
                scenicScore[i][j] *= j - lastN[grid[i][j]]
                for (k in 0 .. grid[i][j])
                    lastN[k] = j
            }
            // right to left
            lastN.fill(grid[0].size-1)
            highestTreeSoFar = grid[i][grid[i].size-1]

            for (j in grid[0].size - 1 downTo 0) { // for each column // right to left
                if (grid[i][j] > highestTreeSoFar) {
                    visible[i][j] = true
                    highestTreeSoFar = grid[i][j]
                }
                scenicScore[i][j] *= lastN[grid[i][j]] - j
                for (k in 0 .. grid[i][j])
                    lastN[k] = j
            }
        }
    }

    fun part1(): Int = visible.sumOf { it.sumOf { b -> if (b) 1.toInt() else 0.toInt() } }
    fun part2(): Int = scenicScore.maxOf { it.max() }
}

fun main() {
    val lines: List<String> = File("input/Day8.txt").readLines()
    val d8: Day8 = Day8(lines)

    println("Part 1 ans: ${d8.part1()}")
    println("Part 2 ans: ${d8.part2()}")

}