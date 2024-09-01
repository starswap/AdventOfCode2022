import java.io.File
import java.util.*

// Possible Methods
//   Hard Code (as all the same shape)
//   3D - 2D coordinates mapping for every coordinate
//   Graph Method (as on Reddit)
//   3D vectors method (as on Reddit)
//   The guy's Haskell method (as on Reddit)

val deltas = listOf(Pair(0,1),Pair(1,0),Pair(0,-1),Pair(-1,0))

// replace some pairs with vectors aaaaaaaaaaaaaaaaaaaaaa
// replace some persons wiht the coordinates

data class Person(var row: Int, var col: Int, var facingIdx: Int) {
    operator fun rem(other: Pair<Int,Int>): Person {
        return Person((this.row + other.first) % other.first,(this.col + other.second) % other.second, this.facingIdx)
    }

    operator fun plus(delta: Pair<Int,Int>): Person {
        return Person(row + delta.first, col + delta.second, facingIdx)
    }

    operator fun minus(delta: Pair<Int,Int>): Person {
        return Person(row - delta.first, col - delta.second, facingIdx)
    }

    fun turnRight(): Person = Person(row, col, (facingIdx + 1 + 4) % 4)
    fun turnLeft(): Person = Person(row, col, (facingIdx - 1 + 4) % 4)
    fun position(): TwoDPoint = TwoDPoint(row, col)
}


data class TwoDVector(val dr: Int, val dc: Int) {
    fun norm() = dr + dc // don't tell anyone!
    fun orthogonal() = TwoDVector(this.dc, this.dr) // shh!
    fun negate() = TwoDVector(-this.dr, -this.dc) // this one's ok!

    operator fun div(scalar: Int) = TwoDVector(this.dr / scalar, this.dc / scalar)
    operator fun times(scalar: Int) = TwoDVector(this.dr * scalar, this.dc * scalar)
}

data class TwoDPoint(val row: Int, val col: Int) {
    fun exists(grid: List<String>) = row >= 0 && row < grid.size && col >= 0 && col < grid[0].length && grid[row][col] != ' '
    operator fun minus(other: TwoDPoint): TwoDVector = TwoDVector(this.row - other.row, this.col - other.col)
    operator fun plus(other: TwoDVector): TwoDPoint = TwoDPoint(this.row + other.dr, this.col + other.dc)
}
data class ThreeDPoint(val x: Boolean, val y: Boolean, val z: Boolean)
data class TwoDEdge(val p1: TwoDPoint, val p2: TwoDPoint) {
    fun addRow(amt: Int) = TwoDEdge(TwoDPoint(p1.row + amt,p1.col),TwoDPoint(p2.row + amt, p2.col))
    fun addCol(amt: Int) = TwoDEdge(TwoDPoint(p1.row,p1.col + amt),TwoDPoint(p2.row, p2.col + amt))
    fun shareRow() = p1.row == p2.row
    fun shareCol() = p1.col == p2.col
    fun exists(grid: List<String>) = p1.exists(grid) && p2.exists(grid)
    fun addDelta(delta: Int): TwoDPoint  = p1 + ((p2 - p1) / (p2 - p1).norm()) * delta
    fun awayFromEdgeDirection(grid: List<String>): Int {
        val orthogonal = ((p2 - p1) / (p2 - p1).norm()).orthogonal()
        if ((p1 + orthogonal).exists(grid)) return deltas.indexOf(orthogonal.dr to orthogonal.dc)
        else return deltas.indexOf(orthogonal.negate().dr to orthogonal.negate().dc)
    }
}
data class ThreeDEdge(val p1: ThreeDPoint, val p2: ThreeDPoint) {
    fun flipX() = ThreeDEdge(ThreeDPoint(!this.p1.x,this.p1.y,this.p1.z), ThreeDPoint(!this.p2.x,this.p2.y,this.p2.z))
    fun flipY() = ThreeDEdge(ThreeDPoint(this.p1.x,!this.p1.y,this.p1.z), ThreeDPoint(this.p2.x,!this.p2.y,this.p2.z))
    fun flipZ() = ThreeDEdge(ThreeDPoint(this.p1.x,this.p1.y,!this.p1.z), ThreeDPoint(this.p2.x,this.p2.y,!this.p2.z))
}



operator fun List<String>.get(loc: Person): Char {
    return this[loc.row][loc.col]
}

class Day22(text: String, cubeSize: Int) {
    private val grid: List<String>
    private val instructions: String = text.split("\r\n\r\n")[1].trim()

    private val twoDToThreeEdge: MutableMap<TwoDEdge, ThreeDEdge> = mutableMapOf()
    private val threeDToTwoDEdge: MutableMap<ThreeDEdge, MutableList<TwoDEdge>> = mutableMapOf() // we should be able to filter
    private val allVertexTwoD: MutableList<TwoDPoint> = mutableListOf()

    init {
        val unpadded = text.split("\r\n\r\n")[0].split("\r\n")
        grid = unpadded.map{line -> line.padEnd(unpadded.maxOf { it.length },' ')}

        var zerozerozero = TwoDPoint(0,0)
        for ((id, tile) in grid[0].withIndex()) {
            if (tile != ' ') {
                zerozerozero = TwoDPoint(0, id)
                break
            }
        }
        val zerozeroone = TwoDPoint(zerozerozero.row, zerozerozero.col + cubeSize - 1)
        val zeroonezero = TwoDPoint(zerozerozero.row + cubeSize - 1, zerozerozero.col)
        val zerooneone = TwoDPoint(zerozerozero.row + cubeSize - 1, zerozerozero.col + cubeSize - 1)

        val toProcess: Deque<Pair<TwoDEdge,ThreeDEdge>> = LinkedList()
        toProcess.add(TwoDEdge(zerozerozero,zerozeroone) to ThreeDEdge(ThreeDPoint(false, false, false),ThreeDPoint(false, false, true)))
        toProcess.add(TwoDEdge(zerozerozero,zeroonezero) to ThreeDEdge(ThreeDPoint(false, false, false),ThreeDPoint(false, true, false)))
        toProcess.add(TwoDEdge(zeroonezero,zerooneone) to ThreeDEdge(ThreeDPoint(false, true, false), ThreeDPoint(false, true, true)))
        toProcess.add(TwoDEdge(zerozeroone,zerooneone) to ThreeDEdge(ThreeDPoint(false, false, true), ThreeDPoint(false, true, true)))



        while (toProcess.isNotEmpty()) {
            val (currTwoD, currThreeD) = toProcess.removeFirst()
            var newTwoD = TwoDEdge(TwoDPoint(0,0),TwoDPoint(0,0)) // default
            var newThreeD = ThreeDEdge(ThreeDPoint(false,false,false),ThreeDPoint(false,false,false))
            var foundTwoD = false

            allVertexTwoD.add(currTwoD.p1)
            allVertexTwoD.add(currTwoD.p2)

            if (currTwoD.shareRow()) {
                if (currTwoD.addRow(cubeSize - 1) !in twoDToThreeEdge && currTwoD.addRow(cubeSize - 1).exists(grid)) {
                    newTwoD = currTwoD.addRow(cubeSize - 1)
                    foundTwoD = true
                }
                else if (currTwoD.addRow(-cubeSize + 1) !in twoDToThreeEdge && currTwoD.addRow(- cubeSize + 1).exists(grid)) {
                    newTwoD = currTwoD.addRow(-cubeSize + 1)
                    foundTwoD = true
                }
            }
            else if (currTwoD.shareCol()) {
                if (currTwoD.addCol(cubeSize - 1) !in twoDToThreeEdge && currTwoD.addCol(cubeSize - 1).exists(grid)) {
                    newTwoD = currTwoD.addCol(cubeSize - 1)
                    foundTwoD = true
                }
                else if (currTwoD.addCol(-cubeSize + 1) !in twoDToThreeEdge && currTwoD.addCol(- cubeSize + 1).exists(grid)) {
                    newTwoD = currTwoD.addRow(-cubeSize + 1)
                    foundTwoD = true
                }
            }


            if (currThreeD.p1.x == currThreeD.p2.x && currThreeD.flipX() !in threeDToTwoDEdge) {
                newThreeD = currThreeD.flipX()
            }
            else if (currThreeD.p1.y == currThreeD.p2.y && currThreeD.flipY() !in threeDToTwoDEdge) {
                newThreeD = currThreeD.flipY()
            }
            else if (currThreeD.p1.z == currThreeD.p2.z && currThreeD.flipZ() !in threeDToTwoDEdge) {
                newThreeD = currThreeD.flipZ()
            }

            if (foundTwoD) {
                twoDToThreeEdge[newTwoD] = newThreeD
                threeDToTwoDEdge.getOrPut(newThreeD) { mutableListOf() }.add(newTwoD)
                toProcess.add(newTwoD to newThreeD)
            }
        }
    }

    // Locate the top left '.' or '#' cell and call this (0,0,0)
    // Add cubeSize in x or y to get to (1,0,0) and (0,1,0)
    // Add cubeSize in x and y to get to (1,1,1)
    // Create a queue (or similar) of edges to process
    // insert edges (0,0,0) to (1,0,0), (1,0,0) to (1,1,0), (1,1,0) to (0,1,0), (0,1,0) to (0,0,0)      [order might matter]

    // twoDtoThreeDEDGES = visited set (we need to be able to map in both directions so store also threeDtotwoD)

    // might need a set of all vertices' 2d coords
    // pop from queue until empty {
    //     with the top edge:
    //          follow the direction of the edge on the 2D map by adding cubesize in the correct direction
    //          if another edge is found
    //                 flip bits we haven't flipped already, based on twoDtoThreeD; add to twoDtoThreeD and to the queue
    //
    // }


//        }

    private fun findEdge(p: Person,delta: Pair<Int,Int>): Pair<Pair<TwoDEdge,ThreeDEdge>, Int> {
        val orthogonal = Pair(delta.second, delta.first)
        var currPos = p
        var lengthDelta = 0
        while (currPos.position() !in allVertexTwoD) {
            currPos += orthogonal
            lengthDelta++
        }
        val oneSide = currPos.position()
        currPos = p
        while (currPos.position() !in allVertexTwoD) {
            currPos -= orthogonal
        }
        val otherSide = currPos.position()
        if (TwoDEdge(oneSide,otherSide) in twoDToThreeEdge) {
            return Pair(Pair(TwoDEdge(oneSide,otherSide),twoDToThreeEdge[TwoDEdge(oneSide,otherSide)]!!), lengthDelta)
        }
        else {
            println(otherSide)
            println(oneSide)
            return Pair(Pair(TwoDEdge(otherSide, oneSide), twoDToThreeEdge[TwoDEdge(otherSide, oneSide)]!!), lengthDelta)
        }
    }

    private fun wrapPart1(pos: Person, delta: Pair<Int,Int>): Person {
        var newPos = pos % Pair(grid.size,grid[0].length)
        while (grid[newPos] == ' ')
            newPos = (newPos + delta) % Pair(grid.size, grid[0].length)
        return newPos
    }

    private fun wrapPart2(pos: Person, delta: Pair<Int,Int>): Person {
        // Change the facing rule as well
        // calculate the delta to the twoDEdge left point
        // add this delta onto the new two D edge left point, varying the direction so that it is along the edge
        // we now have the new position
        // get the new direction by saying that it is along the edge

        if (grid[pos] == ' ') {
            val (edge, lengthDelta) = findEdge(pos - delta, delta)
            val (twoDEdgeCoords,threeDEdgeCoords) = edge

            val newTwoDEdgeCoords = threeDToTwoDEdge[threeDEdgeCoords]!!.first { it != twoDEdgeCoords }
            val newPosn: TwoDPoint = newTwoDEdgeCoords.addDelta(lengthDelta)
            val newFacing = newTwoDEdgeCoords.awayFromEdgeDirection(grid)
            return Person(newPosn.row, newPosn.col, newFacing)
        } else {
            return pos
        }
    }

    private fun move(wrap: (Person, Pair<Int,Int>) -> Person):Person {
        var theInstructions = instructions
        var me = Person(0,0,0)

        // Find start position
        for ((id, tile) in grid[0].withIndex()) {
            if (tile == '.') {
                me = Person(0,id,0)
                break
            }
        }

        // Process instructions
        while (theInstructions.isNotEmpty()) {
            val steps = theInstructions.takeWhile { it in '0'..'9'}.toInt()
            theInstructions = theInstructions.dropWhile { it in '0'..'9' }

            for (step in 0 until steps) {
                if (grid[wrap(me+deltas[me.facingIdx], deltas[me.facingIdx])] == '#') break
                else
                    me = wrap(me+deltas[me.facingIdx], deltas[me.facingIdx])
            }

            if (theInstructions.isEmpty()) break

            val turn = theInstructions.first()
            theInstructions = theInstructions.drop(1)
            me = if (turn == 'R') me.turnRight() else me.turnLeft()
        }
        return me
    }

    private fun score(p: Person): Int = 1000*(p.row + 1) + 4*(p.col + 1) + p.facingIdx

    fun part1(): Int = score(move(::wrapPart1))
    fun part2(): Int = score(move(::wrapPart2))

}

const val CUBE_SIZE = 4

fun main() {
    val d22 = Day22(File("input/Day22Sample.txt").readText(), CUBE_SIZE)
    println("Part 1 ans: ${d22.part1()}")
    println("Part 2 ans: ${d22.part2()}")
}
