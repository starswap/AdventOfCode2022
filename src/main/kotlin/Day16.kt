import java.io.File
import kotlin.math.max
var counter = 0
//data class State(val u: String, val t: Int, val open: Int)

class Day16(lines: List<String>) {
    private val MAXT = 30
    private val MAXN = 60
    private val dp: Array<Array<Array<Int?>>>  // dp[u][t][open]
    private val nameMap: MutableMap<String,Int> = mutableMapOf()
    private val flowAmt: Array<Int> = Array(MAXN) { 0 }
    private val nonEmptyIdx: Array<Int> = Array(MAXN) { -1 }
    private val AL: Array<MutableList<Int>> = Array(MAXN) { mutableListOf<Int>() }
    private var numNonEmpty = 0


    init {
        var newValveIndex = 0
        for (line in lines) {
            val (s,fl) = "Valve ([A-Z]+) has flow rate=([0-9]+); tunnel[s]* lead[s]* to valve".toRegex().find(line)!!.destructured.toList()

            val targs: List<String>
            if (line.split("valves ").size == 1) {
                targs = line.split("valve ")[1].split(", ")
            }
            else {
                targs = line.split("valves ")[1].split(", ")
            }
            if (s !in nameMap) {
                nameMap[s] = newValveIndex
                newValveIndex++
            }
            flowAmt[nameMap[s]!!] = fl.toInt()
            if (fl.toInt() > 0) {
                nonEmptyIdx[nameMap[s]!!] = numNonEmpty
                numNonEmpty++
            }
            for (t in targs) {
                if (t !in nameMap){
                    nameMap[t] = newValveIndex
                    newValveIndex++
                }
                AL[nameMap[s]!!].add(nameMap[t]!!)
            }
        }
        dp = Array(AL.size) { Array(MAXT + 1) { Array(1.shl(numNonEmpty), { null }) } }
    }

    fun part1(): Int = doDP(nameMap["AA"]!!,30,0)
    fun part2(): Int {
        var best = 0
        for (i in 0 until 1.shl(numNonEmpty)) {
//            println(i.toString(2))
//            println((i).inv().and(1.shl(numNonEmpty)-1).toString(2))
            best = max(best, doDP(nameMap["AA"]!!,26,i) + doDP(nameMap["AA"]!!,26,(i).inv().and(1.shl(numNonEmpty)-1)))
        }
        return best
    }

    private fun doDP(u: Int, t: Int, open: Int): Int {
        if (dp[u][t][open] != null)
            return dp[u][t][open]!!
        else if (t == 0) return 0
        else {
            var best: Int
            if (nonEmptyIdx[u] > -1 && (open and (1).shl(nonEmptyIdx[u]) == 0)) //can turn on and haven't turned on yet
                best = (t-1)*flowAmt[u] + doDP(u,t-1,open or (1).shl(nonEmptyIdx[u]))
            else
                best = -1
            for (v in AL[u]) {
                best = max(best,doDP(v,t-1,open))
            }
            dp[u][t][open] = best
            counter++
//            println(counter)
            return best
        }
    }
}
fun main() {
    val d16 = Day16(File("input/Day16.txt").readLines())
    println("Part 1 ans: ${d16.part1()}")
    println("Part 2 ans: ${d16.part2()}")
}