import java.io.File
import kotlin.math.max

enum class SnafuDigit(val chrRep: Char) {
    Zero('0'),
    One('1'),
    Two('2'),
    Minus('-'),
    DblMinus('=');
}

inline infix fun <reified E : Enum<E>, V> ((E) -> V).findBy(value: V): E? {
    return enumValues<E>().firstOrNull { this(it) == value }
}

data class Snafu(val thisValue: SnafuDigit, val restOfNumber: Snafu?) {

    constructor (str: String) : this(thisValue = SnafuDigit :: chrRep.findBy(str.last())!!,
                                     restOfNumber = if (str.length == 1) null else Snafu(str.dropLast(1)))
    private fun decr(): Snafu =
        when (thisValue) {
            SnafuDigit.Zero -> Snafu(SnafuDigit.Minus, restOfNumber)
            SnafuDigit.One -> Snafu(SnafuDigit.Zero, restOfNumber)
            SnafuDigit.Two -> Snafu(SnafuDigit.One, restOfNumber)
            SnafuDigit.Minus-> Snafu(SnafuDigit.DblMinus, restOfNumber)
            SnafuDigit.DblMinus -> Snafu(SnafuDigit.Two,restOfNumber?.decr())  // double minus, minus 1 equals minus 3. So borrow 5 and return 2.
        }

    private fun toBaseFiveInternal(): List<Int> =
        when  {
            (thisValue.chrRep in '0'..'2') -> (restOfNumber?.toBaseFiveInternal() ?: emptyList<Int>()) + thisValue.chrRep.digitToInt()
            (thisValue.chrRep == '-') -> (restOfNumber?.decr()?.toBaseFiveInternal() ?: emptyList<Int>()) + 4
            else -> (restOfNumber?.decr()?.toBaseFiveInternal()?: emptyList<Int>()) + 3
        }

    fun toBaseFive(): List<Int> = toBaseFiveInternal().reversed()
    override fun toString(): String = restOfNumber.toString() + thisValue.chrRep
}


class Day25(lines: List<String>) {
    private val snafus: List<Snafu> = lines.map(::Snafu)
    private fun addBaseFive(one: List<Int>, two: List<Int>): List<Int> {
        var carry = 0
        val listOut = mutableListOf<Int>()

        for (i in 0 until max(one.size,two.size))  {
            val sum = (if (i >= one.size) 0 else one[i]) + (if (i >= two.size) 0 else two[i])
            listOut.add((sum + carry) % 5)
            carry = (sum + carry) / 5;
        }
        if (carry > 0) listOut.add(carry)
        return listOut
    }

    private fun baseFiveToSnafuString(baseFive: List<Int>): String {
        var carry = 0
        var digitsLowToHigh: String = ""

        for (num in baseFive) {
            if ((num + carry) % 5 == 3) {
                carry = (num + carry + 2) / 5
                digitsLowToHigh = SnafuDigit.DblMinus.chrRep + digitsLowToHigh
            }
            else if ((num + carry) % 5 == 4) {
                carry = (num + carry + 1) / 5
                digitsLowToHigh = SnafuDigit.Minus.chrRep + digitsLowToHigh
            }
            else {
                digitsLowToHigh = ((num + carry) % 5).toString() + digitsLowToHigh
                carry = (num + carry) / 5
            }
        }
        if (carry > 0) digitsLowToHigh = carry.toString() + digitsLowToHigh
        return digitsLowToHigh
    }

    fun part1(): String = baseFiveToSnafuString(snafus.map(Snafu::toBaseFive).fold(listOf(),::addBaseFive))
//    fun part2(): Int = TODO()

}
fun main() {
    val d25 = Day25(File("input/Day25.txt").readLines())
    println("Part 1 ans: ${d25.part1()}")
//    println("Part 2 ans: ${d25.part2()}")
}
