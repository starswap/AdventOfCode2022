import java.io.File

class Day20(lines: List<String>) {
    private val numbers: List<Int>
    private val numbersLarge: List<Long>
    private var indices: MutableList<Int>

    init {
        numbers = lines.map(String::toInt)
        indices = (numbers.indices).toMutableList()
        numbersLarge = numbers.map { it.toLong() * 811589153 }
    }
    private fun extract(idx: Int, numbersIn: List<Number>): Number = numbersIn[indices[idx]]

//    private fun getArray(numbersIn: List<Number>) = indices.map {numbersIn[it]} // For debugging

    private fun scoreNumbers(): Int {
        val origZero: Int = numbers.indexOf((0))
        val nowZero = indices.indexOf(origZero)
        return extract((nowZero + 1000) % (numbers.size),numbers).toInt() +
                extract((nowZero + 2000) % (numbers.size), numbers).toInt() +
                extract((nowZero + 3000) % (numbers.size), numbers).toInt()
    }
    private fun scoreNumbersLarge(): Long {
        val origZero: Int = numbersLarge.indexOf(0L)
        val nowZero = indices.indexOf(origZero)
        return extract((nowZero + 1000) % (numbersLarge.size), numbersLarge).toLong() +
                extract((nowZero + 2000) % (numbersLarge.size), numbersLarge).toLong() +
                extract((nowZero + 3000) % (numbersLarge.size), numbersLarge).toLong()
    }

    fun part1(): Int {
        mix(numbers)
        return scoreNumbers()
    }

    fun part2(): Long {
        indices = (numbers.indices).toMutableList()
        repeat(10) {
            mix(numbersLarge)
        }
        return scoreNumbersLarge()
    }

    private fun mix(numbersToMix: List<Number>) {
        for (i in numbersToMix.indices) {
            val start: Int = indices.indexOf(i)
            indices.remove(i)
            indices.add((((start.toLong()+numbersToMix[i].toLong()) % (numbersToMix.size-1).toLong() + (numbersToMix.size-1).toLong()) % (numbersToMix.size-1).toLong()).toInt(),i)
        }
    }
}
fun main() {
    val d20 = Day20(File("input/Day20.txt").readLines())
    println("Part 1 ans: ${d20.part1()}")
    println("Part 2 ans: ${d20.part2()}")
}