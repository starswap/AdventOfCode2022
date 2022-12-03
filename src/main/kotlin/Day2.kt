import java.io.File

const val oppMin = 'A'
const val youMin = 'X'

object Day2 {
    private fun score(thisTurn: Pair<Int, Int>): Int {
        val (opp, you) = thisTurn
        val rockPaperScissors = you + 1
        val winDrawLose = ((you - opp + 4) % 3)*3
        return winDrawLose + rockPaperScissors
    }

    private fun transformRd2(thisTurn: Pair<Int,Int>): Pair<Int, Int> {
        val (opp, result) = thisTurn
        return Pair(opp,((opp + result - 1)+3) % 3)
    }

    fun part1(thisTurn: List<Pair<Int,Int>>): Int {
        return thisTurn.map(::score).sum()
    }

    fun part2(thisTurn: List<Pair<Int,Int>>): Int {
        return thisTurn.map(::transformRd2).map(::score).sum()
    }
}


fun main() {
    val contents = File("input/Day2.txt").readLines()
    val turns = contents.map {
        Pair<Int, Int>(it.split(" ")[0][0].code - oppMin.code,
                       it.split(" ")[1][0].code - youMin.code)
    }
    println("Part 1 answer ${Day2.part1(turns)}")
    println("Part 2 answer ${Day2.part2(turns)}")

}