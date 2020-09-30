import java.io.File
import kotlin.system.exitProcess

fun readFileAsLinesUsingUseLines(fileName: String): MutableList<String>
        = File(fileName).useLines { it.toMutableList() }

fun fifo(comms: MutableList<Int>, memorySize: Int): MutableList<Int>{
    val commands = comms.toMutableList()
    val changeList = mutableListOf<Int>()
    val memory = mutableListOf<Int>()
    while (memory.size < memorySize ) {
        if (memory.find { it == commands[0] } == null) {
            memory.add(memory.size, commands[0])
            commands.removeAt(0)
            changeList.add(memory.size)
        }
        else {
            changeList.add(-1)
            commands.removeAt(0)
        }
    }
    var firstPage = 0
    for (i in commands) {
        if (memory.find { it == i } == null ) {
            memory[firstPage] = i
            changeList.add(firstPage + 1)
            firstPage += 1
            if (firstPage == memorySize) firstPage = 0
        }
        else changeList.add(-1)
    }
    return changeList
}

fun lru (comms: MutableList<Int>, memorySize: Int): MutableList<Int> {
    val commands = comms.toMutableList()
    val changeList = mutableListOf<Int>()
    val memory = mutableListOf<Pair<Int, Int>>()
    while (memory.size < memorySize) {
        for (i in 0 until memory.size) {
            memory[i] = Pair(memory[i].first, memory[i].second + 1)
        }
        if (memory.find { it.first == commands[0] } == null) {
            memory.add(commands[0] to 0)
            commands.removeAt(0)
            changeList.add(memory.size)
        }
        else {
            changeList.add(-1)
            for (i in 0 until memory.size) {
                if (memory[i].first == commands[0]) {
                    memory[i] = Pair(memory[i].first, 0)
                    commands.removeAt(0)
                }
            }
        }
    }
    for (i in commands) {
        if (memory.find {it.first == i} == null) {
            var idxOfFirst = 0
            for (j in 0 until memory.size) {
                if (memory[j].second > memory[idxOfFirst].second) {
                    idxOfFirst = j
                }
            }
            for (j in 0 until memory.size) {
                if (j != idxOfFirst) {
                    memory[j] = Pair(memory[j].first, memory[j].second + 1)
                }
            }
            memory[idxOfFirst] = Pair(i, 0)
            changeList.add(idxOfFirst + 1)
        }
        else {
            changeList.add(-1)
            for (j in 0 until memory.size) {
                memory[j] = Pair(memory[j].first, memory[j].second + 1)
                if (memory[j].first == i) {
                    memory[j] = Pair(memory[j].first, 0)
                }
            }
        }
    }
    return changeList
}

fun main() {
    val conf = readFileAsLinesUsingUseLines("inpout/conf.txt")[0].split(' ')
    val memorySize = conf[0].toInt()
    val pagesCount = conf[1].toInt()
    if (memorySize >=  pagesCount) {
        println("Некорректный формат данных. Ожидается, что количество кадров меньше размера адресного пространства процессора.")
        exitProcess(0)
    }

    val commandsString = readFileAsLinesUsingUseLines("inpout/commands.txt")
    val commands = mutableListOf<Int>()
    for (i in commandsString) {
        commands.add(i.toInt())
    }

    for (i in fifo(commands, memorySize)) {
        println(i)
    }
    println("___________________________")
    for (i in lru(commands, memorySize)) {
        println(i)
    }
}