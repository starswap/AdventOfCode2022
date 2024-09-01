// Possible improvements from Reddit
// - Upper bound heuristic where you allow yourself to build several robots per turn if they all work, without requiring the total resources used to be less than what's available
// - if you are skipping when you can build a robot, make sure not to then build that robot before building another one - i.e. choosing the next robot to build and fast forwarding until you can build it is more efficient
// - " Note that we can do a bit better: For any resource R that's not geode: if you already have X robots creating resource R, a current stock of Y for that resource, T minutes left, and no robot requires more than Z of resource R to build, and X * T+Y >= T * Z, then you never need to build another robot mining R anymore."

import java.io.File
import kotlin.math.max

data class ObCost(val ore: Int, val clay: Int)
data class GeCost(val ore: Int, val ob: Int)
data class Blueprint(val index: Int, val oreCost: Int, val clayCost: Int, val obCost: ObCost, val geCost: GeCost)
data class State(val t: Int,val oreR: Int,val clayR: Int,val obR: Int,val geR: Int,val ore: Int,val clay: Int,val obsidian: Int)

class Day19(lines: List<String>) {
    private val bps = mutableListOf<Blueprint>()
    private val dp = mutableMapOf<State,Int>()
    private var bestSoFar = 0
    private var oreNeeded = 0
    private val maxGeodes = mutableListOf<Int>()

    init {
        for (line in lines) {
            println(line)
            val cGs = "Blueprint ([0-9]+): Each ore robot costs ([0-9]+) ore. Each clay robot costs ([0-9]+) ore. Each obsidian robot costs ([0-9]+) ore and ([0-9]+) clay. Each geode robot costs ([0-9]+) ore and ([0-9]+) obsidian.".toRegex()
                .find(line)!!.destructured.toList().map(String::toInt)
            bps.add(Blueprint(cGs[0],cGs[1],cGs[2],ObCost(cGs[3],cGs[4]),GeCost(cGs[5],cGs[6])))
        }

        println("Preprocessing for part 1")
        for ((i,v) in bps.withIndex()) {
            dp.clear()
            oreNeeded = listOf(v.oreCost,v.clayCost,v.obCost.ore,v.geCost.ore).max()
            maxGeodes.add(bestGeodes(24, v, 1, 0, 0, 0, 0, 0, 0))
            println("Done $i")
        }

    }

    private fun bestGeodes(t: Int, bp: Blueprint, oreR: Int, clayR: Int, obR: Int, geR: Int, ore: Int, clay: Int, obsidian: Int): Int {
        if (State(t,oreR,clayR,obR,geR,ore,clay,obsidian) in dp) {
            return dp[State(t,oreR,clayR,obR,geR,ore,clay,obsidian)]!!
        }
        if (t == 0) return 0
        else if (oreR >= bp.geCost.ore && obR >= bp.geCost.ob) return t*geR + (t-1)*t/2
        else {
            var best: Int
            if (ore >= bp.geCost.ore && obsidian >= bp.geCost.ob && ore >= bp.obCost.ore && clay >= bp.obCost.clay && ore >= bp.clayCost && ore >= bp.oreCost) {
                best = 0
            }
            else {
                best = geR + bestGeodes(t-1,bp,oreR,clayR,obR,geR,ore+oreR,clay+clayR,obsidian+obR)
            }

            if (best == t*geR + (t-1)*t/2) {dp[State(t,oreR,clayR,obR,geR,ore,clay,obsidian)] = best; return best}
            if (ore >= bp.geCost.ore && obsidian >= bp.geCost.ob) {
                best = max(best, geR + bestGeodes(t-1,bp,oreR,clayR,obR,geR+1,ore+oreR-bp.geCost.ore,clay+clayR,obsidian+obR-bp.geCost.ob))
            }
            if (best == t*geR + (t-1)*t/2) {dp[State(t,oreR,clayR,obR,geR,ore,clay,obsidian)] = best; return best}
            if (ore >= bp.obCost.ore && clay >= bp.obCost.clay && obR < bp.geCost.ob) {
                best = max(best, geR + bestGeodes(t-1,bp,oreR,clayR,obR+1,geR,ore+oreR-bp.obCost.ore,clay+clayR-bp.obCost.clay,obsidian+obR))
            }
            if (ore >= bp.clayCost && clayR < bp.obCost.clay) {
                best = max(best, geR + bestGeodes(t-1,bp,oreR,clayR+1,obR,geR,ore+oreR-bp.clayCost,clay+clayR,obsidian+obR))
            }
            if (ore >= bp.oreCost && oreR < oreNeeded) {
                best = max(best, geR + bestGeodes(t-1,bp,oreR+1,clayR,obR,geR,ore+oreR-bp.oreCost,clay+clayR,obsidian+obR))
            }

            dp[State(t,oreR,clayR,obR,geR,ore,clay,obsidian)] = best
            bestSoFar = max(best,bestSoFar)
            return best
        }
    }

    fun part1(): Int = maxGeodes.mapIndexed { id, it -> (id + 1) * it }.sum()
    fun part2(): Int {
        var score = 1
        for ((i,v) in bps.withIndex().take(3)) {
            dp.clear()
            oreNeeded = listOf(v.oreCost,v.clayCost,v.obCost.ore,v.geCost.ore).max()
            score *= (bestGeodes(32, v, 1, 0, 0, 0, 0, 0, 0))
            println("Done $i")
        }
        return score
    }

}
fun main() {
    val d19 = Day19(File("input/Day19.txt").readLines())
    println("Starting Part 1")
    println("Part 1 ans: ${d19.part1()}")
    println("Starting Part 2")
    println("Part 2 ans: ${d19.part2()}")
}