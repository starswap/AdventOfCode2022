import java.io.File
import kotlin.math.max

data class Instruction(val numCrates: Int, val fromStack: Int, val toStack: Int)

abstract class Day5(lines: List<String>) {
    private val stackMatch = "(\\[([A-Z])\\])|    ".toRegex()
    private val instrMatch = "move ([0-9]+) from ([0-9]+) to ([0-9]+)".toRegex()
    private val instructions: MutableList<Instruction> = mutableListOf<Instruction>()
    protected val stacks: MutableList<ArrayDeque<String>> = mutableListOf()

    init {
        var stillParsingStacks: Boolean = true
        for (line in lines) {
            if (stillParsingStacks) {
                stillParsingStacks = false
                for ((idx, match) in stackMatch.findAll(line).withIndex()) {
                    stillParsingStacks = true
                    if (match.destructured.toList().toList()[1] != "") { // we found a letter (box) and not a space
                        while (stacks.size <= idx) { // initially we don't know how many stacks we need; keep adding more stacks if we later find we need more.
                            stacks.add(ArrayDeque())
                        }
                        stacks[idx].addFirst(match.destructured.toList()[1])
                    }
                }
            }
            else { // now parsing instructions
                instrMatch.find(line)?.destructured?.let {
                        (amt,from,to) -> instructions.add(Instruction(amt.toInt(),from.toInt()-1,to.toInt()-1))
                }
            }
        }
    }

    abstract fun moveCrates(instruction: Instruction)

    fun run(): String {
        instructions.forEach(::moveCrates)
        return stacks.joinToString("") { it.last() }
    }

}

class Day5Part1(lines: List<String>) : Day5(lines) {
    override fun moveCrates(instruction: Instruction) {
        val (numCrates, from, to) = instruction;
        repeat(numCrates) {stacks[to].add(stacks[from].removeLast())}
    }
}

class Day5Part2(lines: List<String>) : Day5(lines) {
    override fun moveCrates(instruction: Instruction) {
        val (numCrates, from, to) = instruction;
        stacks[to].addAll(stacks[from].subList(max(0,stacks[from].size-numCrates),stacks[from].size))
        repeat(numCrates) {stacks[from].removeLast()}
    }
}

fun main() {
    val lines: List<String> = File("input/Day5.txt").readLines()

    val part1 = Day5Part1(lines)
    println("Part 1 ans: ${part1.run()}")

    val part2 = Day5Part2(lines)
    println("Part 2 ans: ${part2.run()}")
}