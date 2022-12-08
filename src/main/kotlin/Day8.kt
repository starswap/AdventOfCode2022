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
        determineVisibility()
        calcScenicScores()
    }

    private fun determineVisibility() {
        for (j in 0 until grid[0].size) { // for each column
            var max = 0
            for (i in 0 until grid.size) { // for each row
                // top to bottom
                if (grid[i][j] > max) {
                    visible[i][j] = true
                    max = grid[i][j]
                }
            }
            // bottom to top
            max = grid[grid.size-1][j]
            for (i in grid.size - 1 downTo 0) { // for each row
                if (grid[i][j] > max) {
                    visible[i][j] = true
                    max = grid[i][j]
                }
            }
        }

        // side to side
        for (i in 0 until grid.size) { // for each row
            var max = grid[i][0]
            for (j in 0 until grid[0].size) { // for each column
                // left to right
                if (grid[i][j] > max) {
                    visible[i][j] = true
                    max = grid[i][j]
                }
            }
            // right to left
            max = grid[i][grid[i].size-1]
            for (j in grid[0].size - 1 downTo 0) { // for each column // right to left
                if (grid[i][j] > max) {
                    visible[i][j] = true
                    max = grid[i][j]
                }
            }
        }
    }

    private fun calcScenicScores() {
        for (j in 0 until grid[0].size) { // for each column
            val lastN = Array<Int>(10) { 0 } // last one that was bigger than n or the same size.

            for (i in 0 until grid.size) { // for each row
                // top to bottom
                scenicScore[i][j] *= i - lastN[grid[i][j]]
                for (k in 0 .. grid[i][j])
                    lastN[k] = i
            }
            // bottom to top
            lastN.fill(grid.size - 1)
            for (i in grid.size -1 downTo 0) { // for each row
                // top to bottom
                scenicScore[i][j] *= lastN[grid[i][j]] - i
                for (k in 0 .. grid[i][j])
                    lastN[k] = i
            }
        }

        // side to side
        for (i in 0 until grid.size) { // for each row
            val lastN = Array<Int>(10) { 0 } // last one that was bigger than n or the same size.
            for (j in 0 until grid[0].size) { // for each column
                // left to right
                scenicScore[i][j] *= j - lastN[grid[i][j]]
                for (k in 0 .. grid[i][j])
                    lastN[k] = j
            }
            lastN.fill(grid[0].size-1)
            for (j in grid[0].size - 1 downTo 0) {// for each column
                // right to left
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