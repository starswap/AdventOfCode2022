// Can we do this one with Alex's circular calling method for the "prioritise null" bit?

import java.io.File

abstract class ArithmeticNode {
    abstract var humanFlag: Boolean   // is the human in this node's subtree
    abstract var subTreeTotal: Long   // what is the score for this node's subtree
}
data class Operation(val op: (Long, Long) -> Long = {a,b -> a*b},        // operation that this monkey uses
                     val leftInvOp: (Long, Long) -> Long = {a,b -> a*b}, // inverse operation if we have left and need right
                     val rightInvOp: (Long, Long) -> Long = {a,b -> a*b}, // inverse operation if we have right and need left
                     val left: String,                                    // name of the monkey on the left
                     val right: String,                                   // name of the monkey on the right
                     override var humanFlag: Boolean = false,
                     override var subTreeTotal: Long = 0) : ArithmeticNode()
data class Value(val value: Long,
                 val name: String,
                 override var humanFlag: Boolean = false,
                 override var subTreeTotal: Long = 0) : ArithmeticNode()

class Day21(lines: List<String>) {
    private val opRegEx = "([a-z]+): ([a-z]+) ([\\-+*/]) ([a-z]+)".toRegex() // extract operation monkeys
    private val vaRegEx = "([a-z]+): ([0-9]+)".toRegex()                     // extract value monkeys

    private val opMap = mapOf<String,(Long, Long) -> Long>(
        "*" to {a,b -> a*b},
        "-" to {a,b -> a-b},
        "+" to {a,b -> a+b},
        "/" to {a,b -> a/b},
    )
    private val leftInvMap = mapOf<String, (Long, Long) -> Long>( // (left, target)
        "*" to {a, b -> b / a},    // left * right = target, right = target / left
        "-" to {a, b -> a - b},    // left - right = target, right = left - target
        "+" to {a, b -> b - a},    // left + right = target, right = target - left
        "/" to {a, b -> a / b}     // left / right = target, right = left / target
        )
    private val rightInvMap = mapOf<String, (Long, Long) -> Long>( // (target, right)
        "*" to {a, b -> a / b},    // left * right = target, left = target / right
        "-" to {a, b -> a + b},    // left - right = target, left = right + target
        "+" to {a, b -> a - b},    // left + right = target, left = target - right
        "/" to {a, b -> a * b}     // left / right = target, left = right * target
    )

    // From name of node to node object
    private val nodesMap: MutableMap<String, ArithmeticNode> = mutableMapOf()

    init {
        for (line in lines) {
            if (opRegEx.find(line) == null) {  // this line contains a value monkey
                val (name, value) = vaRegEx.find(line)!!.destructured
                nodesMap[name] = Value(value.toLong(), name)
            } else {                          // this line contains an operation monkey
                val (name, childOne, operation, childTwo) = opRegEx.find(line)!!.destructured
                nodesMap[name] = Operation(opMap[operation]!!, leftInvMap[operation]!!, rightInvMap[operation]!!, childOne, childTwo)
            }
        }
    }

    private fun postOrder(n: ArithmeticNode): Long {
        when {
            (n is Operation)  -> {
                n.subTreeTotal = n.op(postOrder(nodesMap[n.left]!!), postOrder(nodesMap[n.right]!!))
                n.humanFlag = nodesMap[n.left]!!.humanFlag || nodesMap[n.right]!!.humanFlag
                return n.subTreeTotal
            }
            (n is Value) -> {
                n.subTreeTotal = n.value
                n.humanFlag = (n.name == "humn")
                return n.value
            }
            else -> {
                return error("Wrong Type")
            }
        }
    }

    private fun need(n: ArithmeticNode, target: Long): Long =            // tells us what to set the human to if this node n needs to be of value target
        when {
            (n is Value && n.name == "humn") -> target                   // Done
            (n is Operation && nodesMap[n.left]!!.humanFlag) -> need(nodesMap[n.left]!!,n.rightInvOp(target,(nodesMap[n.right]!!).subTreeTotal))
            (n is Operation && nodesMap[n.right]!!.humanFlag) -> need(nodesMap[n.right]!!,n.leftInvOp((nodesMap[n.left]!!).subTreeTotal,target))
            else -> error("Wrong Type")
        }

    fun part1(): Long = postOrder(nodesMap["root"]!!)
    fun part2(): Long {
        val root = nodesMap["root"]
        if (root != null && root is Operation) {
            postOrder(root) // make sure to precompute all of the subtree totals and human flags, in case the user for some reason didn't ask for part1 first
            if (nodesMap[root.left]!!.humanFlag)
                return need(nodesMap[root.left]!!, (nodesMap[root.right]!!).subTreeTotal)
            else
                return need(nodesMap[root.right]!!, (nodesMap[root.left]!!).subTreeTotal)
        }
        return error("Wrong type for root; should be Operation")
    }

}
fun main() {
    val d21 = Day21(File("input/Day21.txt").readLines())
    println("Part 1 ans: ${d21.part1()}")
    println("Part 2 ans: ${d21.part2()}")
}
