import java.io.File

fun Char.letterCode() = this.code - 'a'.code

object Day6 {

    private fun findMarker(line: String, reqLen: Int): Int {
        var presSet: Int = 0
        for ((i, c) in line.withIndex()) {
            if (i > reqLen - 1) // remove old; to debug: println(presSet.toString(2))
                presSet = presSet.xor(1.shl(line[i-reqLen].letterCode()))

            presSet = presSet.xor(1.shl(line[i].letterCode())) // add new
            if (presSet.countOneBits() == reqLen)
                return i+1
        }
        return -1
    }
    fun part1(line: String) = findMarker(line, 4)
    fun part2(line: String) = findMarker(line, 14)

}

fun main() {
    val lines: List<String> = File("input/Day6.txt").readLines()
    println("Part 1")
    lines.forEach{
        println(Day6.part1(it))
    }
    println("Part 2")
    lines.forEach{
        println(Day6.part2(it))
    }
}