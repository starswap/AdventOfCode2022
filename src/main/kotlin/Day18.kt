// You will need to increase the stack size to run this program
// Try the following from the terminal:
//       kotlinc src\main\kotlin\Day18.kt -include-runtime -d day18.jar
//       java -Xss18m -jar day18.jar

import java.io.File

data class ThreeD(val x: Int, val y: Int, val z: Int)

class Day18(lines: List<String>) {
    private val dx = listOf(-1,1,0,0,0,0)
    private val dy = listOf(0,0,-1,1,0,0)
    private val dz = listOf(0,0,0,0,-1,1)
    private val XMIN = -1
    private val YMIN = -1
    private val ZMIN = -1
    private val XMAX = 32
    private val YMAX = 32
    private val ZMAX = 32
    private val pres = mutableSetOf<ThreeD>()
    private val visited = mutableSetOf<ThreeD>()

    init {
        for (line in lines) {
            val (x,y,z) = line.split(",").map(String::toInt)
            pres.add(ThreeD(x,y,z))
        }
    }
    private fun score(x: Int, y: Int, z: Int): Int {
        var score = 6
        for (i in 0..5) {
            if (ThreeD(x+dx[i],y+dy[i],z+dz[i]) in pres) score--
        }
        return score
    }
    private fun dfs(x: Int, y: Int, z: Int): Int {
        if (ThreeD(x,y,z) in visited || x < XMIN || x > XMAX || y < YMIN || y > YMAX || z < ZMIN || z > ZMAX) return 0
        else {
            if (ThreeD(x,y,z) in pres) return 1
            else {
                visited.add(ThreeD(x,y,z))
                var adjSc = 0
                for (i in dx.indices) {
                    adjSc += dfs(x+dx[i],y+dy[i],z+dz[i])
                }
                return adjSc
            }
        }
    }

    fun part1(): Int {
        var total = 0
        for (x in XMIN..XMAX) {
            for (y in YMIN..YMAX) {
                for (z in ZMIN..ZMAX) {
                    if (ThreeD(x,y,z) in pres) {
                        total += score(x,y,z)
                    }
                }
            }
        }
        return total
    }
    fun part2(): Int {
        visited.clear()
        return dfs(XMIN, YMIN, ZMIN)
    }

}
fun main() {
    val d17 = Day18(File("input/Day18.txt").readLines())
    println("Part 1 ans: ${d17.part1()}")
    println("Part 2 ans: ${d17.part2()}")
}