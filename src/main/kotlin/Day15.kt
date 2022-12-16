import java.io.File
import kotlin.math.min
import kotlin.math.max
import kotlin.math.abs

inline fun manhattan(x1: Int, y1: Int, x2: Int, y2: Int) = max(x1,x2) - min(x1,x2) + max(y1,y2) - min(y1,y2)


data class SensorBeacon(val sx: Int, val sy: Int, val bx: Int, val by: Int) {
    fun delta() = manhattan(sx,sy,bx,by)
}

class Day15(lines: List<String>) {
    private var minX: Int = 1000000000
    private var maxX: Int = -100000000
    private val sensorBeacon = mutableListOf<SensorBeacon>()
    private val XMAX = 4000000
    private val YMAX = 4000000
    private val HASHMULT = 4000000


    init {
        for (line in lines) {
//            println(line)
            val (x1, y1, x2, y2) = "Sensor at x=([\\-0-9]+), y=([\\-0-9]+): closest beacon is at x=([\\-0-9]+), y=([\\-0-9]+)".toRegex().find(line)!!.destructured.toList().map(String::toInt)
            minX = min(min(minX, x1 - manhattan(x1,y1,x2,y2)),x2-manhattan(x1,y1,x2,y2))
            maxX = max(max(maxX, x1 + manhattan(x1,y1,x2,y2)),x2+manhattan(x1,y1,x2,y2))
            sensorBeacon.add(SensorBeacon(x1,y1,x2,y2))
        }
    }

    fun part1(): Int {
        val y = 2000000
        var total = 0
        for (x in minX..maxX) {
            for (sb in sensorBeacon) {
                if (manhattan(x,y,sb.sx,sb.sy) <= sb.delta() && !(x == sb.bx && y == sb.by)) {// if this is closer to the sensor than the beacon
                    total++
                    break
                }
            }
        }
        return total
    }

    fun part2(): Long = hash(findMissing())

    private fun hash(p: Pair<Long,Long>) = p.first*HASHMULT + p.second

    private fun findMissing(): Pair<Long,Long> {

        for (y in 0 .. YMAX) {
            val covered = mutableSetOf<Pair<Long,Long>>()
            for (sb in sensorBeacon) {
                if (sb.delta() - abs(sb.sy-y) < 0 || sb.delta() + abs(sb.sy-y) < 0)
                    continue

                var xMin: Long = (sb.sx - (sb.delta() - abs(sb.sy-y))).toLong()
                var xMax: Long = (sb.sx + (sb.delta() - abs(sb.sy-y))).toLong()
                val rIt = covered.iterator()

                while (rIt.hasNext()) {
                    val (x1,x2) = rIt.next()
                    if (xMin in x1..x2) {
                        xMin = x1
                        if (xMax in x1 .. x2)
                            xMax = x2
                        rIt.remove()
                    }
                    else if (xMax in x1..x2) {
                        xMax = x2
                        rIt.remove()
                    }
                    else if (x1 in xMin..xMax && x2 in xMin..xMax) {
                        rIt.remove()
                    }
                }
                covered.add(Pair(xMin,xMax))
            }
            if (covered.first().first > 0)
                return Pair(0L,y.toLong())
            else if (covered.last().second < XMAX)
                return Pair(XMAX.toLong(),y.toLong())
            else if (covered.size > 1)
                return Pair(covered.first().second+1L,y.toLong())
        }
        return Pair(-100000,-100000)
    }

}
fun main() {
    val d15 = Day15(File("input/Day15.txt").readLines())
    println("Part 1 ans: ${d15.part1()}")
    println("Part 2 ans: ${d15.part2()}")
}