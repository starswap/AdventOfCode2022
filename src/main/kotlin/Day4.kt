import java.io.File

object Day4 {
    fun part1(text: String): Int = check(text) { al, ar, bl, br -> if (al <= bl && ar >= br || bl <= al && br >= ar) 1 else 0 }
    fun part2(text: String): Int = check(text) { al, ar, bl, br -> if (!(ar < bl || br < al)) 1 else 0 }

    private fun check(text: String, overlap: (Int, Int, Int, Int) -> Int): Int {
        var total = 0
        for (match in "([0-9]+)-([0-9]+),([0-9]+)-([0-9]+)".toRegex().findAll(text)) {
            val (al,ar,bl,br) = match.destructured.toList().map(String::toInt)
            total += overlap(al,ar,bl,br)
        }
        return total
    }

}

fun main() {
    val text: String = File("input/Day4.txt").readText()
    println("Part 1 ans: ${Day4.part1(text)}")
    println("Part 2 ans: ${Day4.part2(text)}")
}