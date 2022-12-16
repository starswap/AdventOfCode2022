import java.io.File

abstract class ListPrime : Comparable<ListPrime> {
    abstract fun parseString(strRep: String, idx: Int): Int
    abstract override operator fun compareTo(other: ListPrime): Int
}
class ListPrimeNode(var value: Int) : ListPrime() {
    override fun parseString(strRep: String, idx: Int): Int {
        value *= 10
        value += strRep[idx].digitToInt()
        return idx+1
    }

    override fun compareTo(other: ListPrime): Int =
        if (other is ListPrimeNode) value.compareTo(other.value)
        else ListPrimeList(mutableListOf(this)).compareTo(other)
}

class ListPrimeList(private val sub: MutableList<ListPrime>) : ListPrime() {
    constructor(strRep: String) : this(mutableListOf()) {
        parseString(strRep,1)
    }
    constructor() : this(mutableListOf())

    override fun parseString(strRep: String, idx: Int): Int {
        var i = idx
        while (i < strRep.length-1) {
            if (strRep[i] == '[') {
                sub.add(ListPrimeList())
                i = sub.last().parseString(strRep,i+1)
            }
            else if (strRep[i] == ']') {
                return i+1
            }
            else if (strRep[i] == ',') {i++} // don't care
            else { // a number
                if (strRep[i-1] == '[' || strRep[i-1] == ',')
                    sub.add(ListPrimeNode(0))
                i = sub.last().parseString(strRep,i)
            }
        }
        return i
    }

    override fun compareTo(other: ListPrime): Int {
        if (other is ListPrimeList) {
            var idx = 0
            while (idx < sub.size && idx < other.sub.size) {
                if (sub[idx] > other.sub[idx]) return sub[idx].compareTo(other.sub[idx])
                else if (sub[idx] < other.sub[idx]) return sub[idx].compareTo(other.sub[idx])
                idx += 1
            }
            if (idx < sub.size) return 10
            else if (idx < other.sub.size) return -10
            else return 0
        }
        else if (other is ListPrimeNode){
            return this.compareTo(ListPrimeList(mutableListOf(other)))
        }
        return error("Not Correct")
    }

}

class Day13(text: String) {
    private val listPairs = mutableListOf<Pair<ListPrimeList,ListPrimeList>>()

    init {
        for (pair in text.split("\r\n\r\n")) {
            val (list1, list2) = pair.split("\r\n")
            listPairs.add(Pair(ListPrimeList(list1),ListPrimeList(list2)))
        }
    }

    fun part1(): Int = listPairs.mapIndexed { i, (left, right) -> if (left <= right) i + 1 else 0 }.sum()
    fun part2(): Int {
        val div1 = ListPrimeList(mutableListOf(ListPrimeList(mutableListOf(ListPrimeNode(2)))))
        val div2 = ListPrimeList(mutableListOf(ListPrimeList(mutableListOf(ListPrimeNode(6)))))
        val srted = (listPairs.map {it.first} + listPairs.map { it.second } + div1 + div2).sorted()
        var score = 1
        for ((i,l) in srted.withIndex()) {
            if (l == div1 || l == div2)
                score *= i+1
        }
        return score
    }

}

fun main() {
    // Create a new instance for part1 and part2 since the state is mutated.
    val d13 = Day13(File("input/Day13.txt").readText())
    println("Part 1 ans: ${d13.part1()}")
    println("Part 2 ans: ${d13.part2()}")
}
