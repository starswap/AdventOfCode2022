import java.io.File
import kotlin.math.max

data class State(val u: String, val t: Int, val open: Int)

class Day16(lines: List<String>) {
    private val dp: MutableMap<State,Int> = mutableMapOf()
    private val flowAmt = mutableMapOf<String,Int>()
    private val nonEmptyMap = mutableMapOf<String, Int>()
    private val AL = mutableMapOf<String, MutableList<String>>()

    init {

        for (line in lines) {
            val (s,fl) = "Valve ([A-Z]+) has flow rate=([0-9]+); tunnel[s]* lead[s]* to valve".toRegex().find(line)!!.destructured.toList()

            val targs: List<String>
            if (line.split("valves ").size == 1) {
                targs = line.split("valve ")[1].split(", ")
            }
            else {
                targs = line.split("valves ")[1].split(", ")
            }
            flowAmt[s] = fl.toInt()
            if (fl.toInt() > 0) {
                nonEmptyMap[s] = nonEmptyMap.size
            }
            AL[s] = mutableListOf()
            targs.forEach { t ->
                AL[s]?.add(t)
            }
        }
    }

    fun part1(): Int = doDP("AA",30,0)
    fun part2(): Int = TODO()

    private fun doDP(u: String, t: Int, open: Int): Int {
        if (dp[State(u,t,open)] != null)
            return dp[State(u,t,open)]!!
        else if (t == 0) return 0
        else {
            var best: Int
            if (u in nonEmptyMap && (open and (1).shl(nonEmptyMap[u]!!) == 0)) //can turn on and haven't turned on yet
                best = (t-1)*flowAmt[u]!! + doDP(u,t-1,open or (1).shl(nonEmptyMap[u]!!))
            else
                best = -1
            for (v in AL[u]!!) {
                best = max(best,doDP(v,t-1,open))
            }
            dp[State(u,t,open)] = best
            return best
        }
    }
}
fun main() {
    val d16 = Day16(File("input/Day16.txt").readLines())
    println("Part 1 ans: ${d16.part1()}")
//    println("Part 2 ans: ${d15.part2()}")
}