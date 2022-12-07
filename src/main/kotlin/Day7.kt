import java.io.File
import java.lang.Exception
import kotlin.math.min

const val INF: Long = 100000000000000

abstract class FileDir(val name: String, var size: Long, val superPath: String)
class LinuxFile(name: String, size: Long, superPath: String) : FileDir(name, size, superPath)
class Directory(name: String, size: Long, superPath: String, val children: MutableMap<String, FileDir>) :
    FileDir(name, size, superPath)

class Day7(private val lines: List<String>) {
    private val dirStructure = Directory("", 0, "", mutableMapOf())
    private val maxPart1DirectorySize = 100000
    private val maxDiskSpace = 70000000
    private val freeSpaceNeeded = 30000000

    private var lineNo = 1

    init {
        build(dirStructure)
    }

    fun part1(): Long {
        return count(dirStructure)
    }

    fun part2(): Long {
        val toDelete = freeSpaceNeeded - (maxDiskSpace - dirStructure.size)
        return minSuitableDir(toDelete, dirStructure)
    }

    private fun count(currentPointer: Directory): Long {
        // total space used up by directories in subtree rooted at currentPointer, with size less than threshold
        var cnt: Long = 0
        if (currentPointer.size < maxPart1DirectorySize) {
            cnt += currentPointer.size
        }
        for ((_, child) in currentPointer.children) {
            if (child is Directory) {
                cnt += count(child)
            }
        }
        return cnt
    }

    private fun minSuitableDir(spaceNeeded: Long, currentPointer: Directory): Long {
        var result = INF
        if (currentPointer.size > spaceNeeded) {// frees up enough space. If it doesn't then its children definitely won't
            result = currentPointer.size  // we can take this directory
            for ((_, child) in currentPointer.children) { // recurse to try and take child directories
                if (child is Directory) // and not a file
                    result = min(result, minSuitableDir(spaceNeeded, child))
            }
        }
        return result
    }

    private fun build(currentPointer: Directory): Long {
        var sizeDelta: Long = 0 // total size of new files we discovered at or below the current subdirectory

        while (lineNo < lines.size) {
            val cmd = lines[lineNo].split(" ")[1] // ($, cmd, args)
            if (cmd == "ls") {
                lineNo++                                                                      // we've done the line with the ls on.
                while (lineNo < lines.size && lines[lineNo].split(" ")[0] != "$") { // create all files discovered in the ls
                    val (type, name) = lines[lineNo].split(" ")
                    if (name !in currentPointer.children) {
                        if (type == "dir") {
                            currentPointer.children[name] = Directory(name, 0, currentPointer.superPath + currentPointer.name + "/", mutableMapOf())
                        } else {
                            currentPointer.children[name] = LinuxFile(name, type.toLong(),  // for a file the "type" is actually the size of the file
                                    currentPointer.superPath + currentPointer.name + "/")
                            sizeDelta += type.toInt() // maintain size of the current directory
                        }
                    }
                    lineNo++
                }
            } else { // change directory
                val targetDir = lines[lineNo].split(" ")[2] // where are we going to?
                lineNo++ // dealt with the cd line already

                if (targetDir == "/") {
                    build(dirStructure) // start from the top again. Any changes made there won't affect this directory in a way that we need to update here so we can throw away the return value
                } else if (targetDir == "..") {
                    break // allow ourselves to return to the previous level
                } else {
                    if (currentPointer.children[targetDir] != null && currentPointer.children[targetDir] is Directory) {
                        sizeDelta += build(currentPointer.children[targetDir] as Directory) // find out the size of the subdirectory, and add this on
                    } else {
                        throw Exception("Tried to cd into a file or a directory which doesn't exist")
                    }
                }
            }
        }
        currentPointer.size += sizeDelta
        return sizeDelta
    }
}

fun main() {
    val lines: List<String> = File("input/Day7.txt").readLines()
    val d7: Day7 = Day7(lines)

    println("Part 1 ans: ${d7.part1()}")
    println("Part 2 ans: ${d7.part2()}")

}