import java.io.File

object Day3 {
    fun part1(lines: List<String>): Int =
        lines.map { rucksack ->
            rucksack.substring(0 until rucksack.length / 2).toSet().intersect(
                rucksack.substring(rucksack.length / 2 until rucksack.length).toSet()
            )
        }.sumOf { it -> charScore(it.first()) }

    fun part2(lines: List<String>):Int =
        lines.chunked(3).sumOf { (bag1, bag2, bag3) ->
            charScore(bag1.toSet().intersect(bag2.toSet()).intersect(bag3.toSet()).first())
        }

    private fun charScore(c: Char ): Int =
        when {
            c.code <= 'Z'.code -> c.code - 'A'.code + 27
            else -> c.code - 'a'.code + 1
        }
}



fun main() {
    val lines: List<String> = File("input/Day3.txt").readLines()

    println("Part 1 ans: ${Day3.part1(lines)}")
    println("Part 2 ans: ${Day3.part2(lines)}")

}