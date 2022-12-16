import java.io.File
import java.util.LinkedList
import java.util.Queue
import kotlin.math.min

data class Node(val r: Int, val c: Int, val dist: Int)

class Day12(val lines: List<String>) {
    private var start = Pair<Int,Int>(0,0)
    private var end   = Pair<Int,Int>(0,0)

    private val dr = listOf<Int>(1,0,0,-1)
    private val dc = listOf<Int>(0,1,-1,0)

    private fun can(char1: Char, char2: Char) = (if (char2 == 'E') 'z' else char2).code - (if (char1 == 'S') 'a' else char1).code <= 1

    init {
        for ((i,row) in lines.withIndex()) {
            for ((j,col) in row.withIndex()) {
                if (col == 'S') start = Pair<Int, Int>(i,j)
                else if (col == 'E') end = Pair<Int,Int>(i,j)
            }
        }
    }

    fun part1(): Int {
        val q: Queue<Node> = LinkedList<Node>()
        val v: MutableSet<Pair<Int,Int>> = mutableSetOf()

        q.add(Node(start.first, start.second, 0))
        v.add(start)
        while (!q.isEmpty()) {

            val (r,c,dist) = q.remove()
            if (Pair<Int,Int>(r,c) == end)
                return dist
            else {
                for (d in dr.indices) {
                    if (r+dr[d] >= 0 && r+dr[d] < lines.size && c+dc[d] >= 0 && c+dc[d] < lines[0].length &&
                        can(lines[r][c],lines[r+dr[d]][c+dc[d]]) && Pair<Int,Int>(r+dr[d],c+dc[d]) !in v) {
                        q.add(Node(r+dr[d],c+dc[d],dist+1))
                        v.add(Pair(r+dr[d],c+dc[d]))
                    }
                }
            }
        }
        return -1
    }

    fun part2(): Int {
        // We could make this slightly more efficient by caching the distance from a given point.
        // An alternative is to do a BFS from E and just record the distance to each node
        // But reusing part 1 saves coding time and a quick analysis reveals it should run in 1 sec.
        var best: Int = 1000000000
        for ((i, row) in lines.withIndex()) {
            for ((j, col) in row.withIndex()) {
                if (col == 'a' || col == 'S') {
                    start = Pair(i,j)
                    val new = part1()
                    if (new != -1)
                        best = min(best, new)
                }
            }
        }
        return best
    }

}

fun main() {
    // Create a new instance for part1 and part2 since the state is mutated.
    val d12: Day12 = Day12(File("input/Day12.txt").readLines())
    println("Part 1 ans: ${d12.part1()}")
    println("Part 2 ans: ${d12.part2()}")
}