// State-Space BFS (O(V+E)). Realistically you aren't going to need more than 150 locations * 10 (for going round and waiting) = 1500 nodes. Will clearly pass
// You know if there is something there at that time by taking the delta in time and adding that on in space in the same row and in the same column. This can be done in O(1)
//  (mod to do the wrap around)
//  You can only be hit by a blizzard that starts in the same row or column as you as there's no diagonal moves

import java.io.File
import java.util.Deque
import java.util.LinkedList

class Day24(private val lines: List<String>) {
    data class State(val time: Int, val row: Int, val col: Int)

    private val dr: List<Int> = listOf(0,0,1,-1,0)
    private val dc: List<Int> = listOf(1,-1,0,0,0)
    private var goalState: State = State(0,0,0)   // default initialiser; will be overwritten
    private var startState: State = State(0,0,0)  // default initialiser; will be overwritten

    init { // Find start and end; we use the string representation internally as there's not any better way to process the grid really
        for ((i,node) in lines.first().withIndex())
            if (node == '.') startState = State(0,0,i)
        for ((i,node) in lines.last().withIndex())
            if (node == '.') goalState = State(0,lines.size-1,i)
    }

    private fun blizzard(s: State): Boolean {     // Is there a blizzard at [row][col] at time t? Also deals with the edges of the grid, the '#' which we treat as a stationary blizzard
        val (time, row, col) = s

        // If a blizzard started in e.g. "same column but above" where would it need to start to arrive at the current location at the stated time?
        // ... we know if it's a "same col above" blizzard that it must be a "v" type blizzard that moves down.

        // Hopefully compiler would optimise out these variable assignments, which are left for readability
        val sameColBelow = (((row + time) - 1) % (lines.size - 2) + 1)
        val sameColAbove = (((row - time) - 1 + 100*(lines.size - 2)) % (lines.size - 2) + 1)
        val sameRowRight = (((col + time) - 1) % (lines[0].length - 2) + 1)
        val sameRowLeft  = (((col - time) - 1 + 100*(lines[0].length - 2)) % (lines[0].length - 2) + 1)
        return (lines[row][col] == '#' || lines[sameColBelow][col] == '^' || lines[sameColAbove][col] == 'v' || lines[row][sameRowLeft] == '>' || lines[row][sameRowRight] == '<')
    }

    fun part1(): Int = stateBFS(startState, goalState)
    fun part2(): Int {
        // Hopefully compiler would optimise out these variable assignments
        val there = stateBFS(startState,goalState)
        val back = stateBFS(State(there,goalState.row,goalState.col),startState)
        val thereAgain = stateBFS(State(back, startState.row, startState.col),goalState)
        return thereAgain
    }

    private fun stateBFS(start: State, goalState: State): Int {
        val bfsStateQueue: Deque<State> = LinkedList<State>()
        val visited: MutableSet<State> = mutableSetOf()

        bfsStateQueue.add(start)
        while (bfsStateQueue.isNotEmpty()) {
            val (time, row, col) = bfsStateQueue.removeFirst()
            if (col == goalState.col && row == goalState.row) return time // done; we found it!
            else {
                for (i in dr.indices) { // all neigbours
                    val newState = State(time + 1, row + dr[i], col + dc[i]) // will move to a new posn at time t+
                    // ok if we stay in the grid, haven't visited before, and there's no blizzard blocking us
                    if (newState !in visited && newState.row >= 0 && newState.row < lines.size && newState.col >= 0 && newState.col < lines[0].length && !blizzard(newState)) {
                        bfsStateQueue.add(newState)
                        visited.add(newState)
                    }
                }
            }
        }
        return -1

    }


}
fun main() {
    val d24 = Day24(File("input/Day24.txt").readLines())
    println("Part 1 ans: ${d24.part1()}")
    println("Part 2 ans: ${d24.part2()}")
}
