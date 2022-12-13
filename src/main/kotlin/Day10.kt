import java.io.File
import java.lang.StringBuilder

data class Register(val value: Int, val clock: Int) {
    fun noop(): Register = Register(value, clock+1)
    fun addx(a: Int): List<Register> = listOf(Register(value,clock+1),Register(value+a,clock+2))
    fun poll(): Int = if (clock % 40 == 19) (clock+1) * value else 0
    fun poll2(): String = if ((clock)% 40 + 1 == (value) || clock % 40 == (value) || (clock ) % 40 - 1 == (value)) "#" else "."
}

class Day10(private val lines: List<String>) {
    fun part1(): Int =
        lines.fold(Pair(0, Register(1, 0))) { (total, reg), line ->
            if (line == "noop") Pair(total + reg.noop().poll(), reg.noop())
            else Pair(
                total + reg.addx(line.split(" ")[1].toInt())[0].poll()
                        + reg.addx(line.split(" ")[1].toInt())[1].poll(),
                reg.addx(line.split(" ")[1].toInt())[1])
        }.first

    fun part2(): String =
        lines.fold(Pair(StringBuilder(""), Register(1, 0)),::processPart2).first.toString()

    private fun processPart2(acc: Pair<StringBuilder, Register>, line: String): Pair<StringBuilder, Register>  {
        val (soFar,reg) = acc
        val newReg: Register

        if (reg.clock % 40 == 0 && reg.clock != 0)
            soFar.append("\n")
        soFar.append(reg.poll2())
        if (line == "noop") {
            newReg = reg.noop()
        }
        else {
            newReg = reg.addx(line.split(" ")[1].toInt())[1]
            if (reg.addx(line.split(" ")[1].toInt())[0].clock % 40 == 0)
                soFar.append("\n")
            soFar.append(reg.addx(line.split(" ")[1].toInt())[0].poll2())
         }
        return Pair(soFar, newReg)
    }

}

fun main() {
    val lines: List<String> = File("input/Day10.txt").readLines()
    val d10: Day10 = Day10(lines)

    println("Part 1 ans: ${d10.part1()}")
    println("Part 2 ans:")
    println(d10.part2())

}