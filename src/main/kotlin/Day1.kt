import java.io.File

object Day1 {

    private fun preprocess(lines: List<String>): List<Int> {
        val folded: Pair<Int, List<Int>> =
            lines.fold(Pair(0,emptyList<Int>())) { (numCals, prevGroups), thisLine ->
                if (thisLine == "") (Pair(0, prevGroups + numCals))
                else (Pair(numCals + thisLine.toInt(), prevGroups))
            }
        val cals: List<Int> = folded.second + folded.first
        return cals.sortedDescending()
    }

    fun part1(lines: List<String>): Int {
        return preprocess(lines).first()
    }
    fun part2(lines: List<String>): Int {
        return preprocess(lines).take(3).sum()
    }
}


fun main() {
    val lines: List<String> = File("input/Day1.txt").readLines()

    println("Part 1 ans: ${Day1.part1(lines)}")
    println("Part 2 ans: ${Day1.part2(lines)}")

}