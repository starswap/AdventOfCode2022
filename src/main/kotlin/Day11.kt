import java.io.File
class Monkey(description: String) {
    val items: MutableList<Long>
    val op: (Long) -> Long
    val nextMonkey: (Long) -> Int
    var inspections: Int = 0
    val divisor: Long

    private val opMap = mapOf<String, (Long, Long) -> Long>(
        "*" to {a, b -> a * b},
        "+" to {a, b -> a + b}
    )
    init {
        val lines: List<String> = description.split("\n").map(String::trim)
        items = lines[1].split("Starting items: ")[1].split(", ").map(String::toLong).toMutableList()
        op = { old ->
            val parts = lines[2].split("Operation: new = ")[1].split(" ")
            val operand1 = if (parts[0] == "old") old else parts[0].toLong()
            val operand2 = if (parts[2] == "old") old else parts[2].toLong()
            opMap[parts[1]]?.let { it(operand1,operand2) } ?: -100
        }
        divisor = lines[3].split("Test: divisible by ")[1].toLong()
        val monkeyA = lines[4].split("If true: throw to monkey ")[1].toInt()
        val monkeyB = lines[5].split("If false: throw to monkey ")[1].toInt()
        nextMonkey = { new -> if ((new % divisor) == 0L) monkeyA else monkeyB }
    }

    override fun toString(): String = "Monkey of ${items.toString()} with $inspections inspections"
}

class Day11(text: String) {
    private val monkeys = text.split("\r\n\r\n").map(::Monkey)
    private val product: Long = monkeys.fold(1) { acc, monkey -> acc * monkey.divisor }

    fun part1(): Long = nRounds(20) { it / 3 }
    fun part2(): Long = nRounds(10000) { it % product }

    private fun nRounds(n: Int, postOp: (Long) -> Long):Long {
        repeat(n) { // for n rounds
            for (i in monkeys.indices) { // in each round go over all the monkeys
                for (item in monkeys[i].items) { // for each item the nth monkey holds
                    val new: Long = postOp(monkeys[i].op(item)) // update the worry score, dividing or modding as necessary
                    monkeys[monkeys[i].nextMonkey(new)].items.add(new) // throw this item to the next monkey in question
                    monkeys[i].inspections += 1 // this monkey has inspected on more time
                }
                monkeys[i].items.clear() // now all of this monkey's items have been thrown to another monkey
            }
        }
        return monkeys.sortedByDescending { it.inspections }.take(2).fold(1) { acc, mon -> acc * mon.inspections } // product of two highest
    }
}

fun main() {
    // Create a new instance for part1 and part2 since the state is mutated.
    val d11: Day11 = Day11(File("input/Day11.txt").readText())
    println("Part 1 ans: ${d11.part1()}")

    val d11a: Day11 = Day11(File("input/Day11.txt").readText())
    println("Part 2 ans: ${d11a.part2()}")

}