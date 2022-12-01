import java.io.File

fun preprocess(lines: List<String>): List<Int> {
    val folded: Pair<Int, List<Int>> =
        lines.fold(Pair(0,emptyList<Int>())) { (numCals, prevGroups), thisLine ->
            if (thisLine == "") (Pair(0, prevGroups + numCals))
            else (Pair(numCals + thisLine.toInt(), prevGroups))
        }
    val cals: List<Int> = folded.second + folded.first
    return cals.sortedDescending()
}

fun solve1(lines: List<String>): Int {
    return preprocess(lines).first()
}
fun solve2(lines: List<String>): Int {
    return preprocess(lines).take(3).sum()
}

fun main() {
    val lines: List<String> = File("input/Day1.txt").readLines()

    println("Part 1 ans: ${solve1(lines)}")
    println("Part 2 ans: ${solve2(lines)}")

}