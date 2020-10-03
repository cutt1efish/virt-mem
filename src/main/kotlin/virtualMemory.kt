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

fun opt(comms: MutableList<Int>, memorySize: Int): MutableList<Int> {
    val commands = comms.toMutableList()
    val changeList = mutableListOf<Int>()
    val memory = mutableListOf<Pair<Int, Int>>()
    while (memory.size < memorySize) {
        if (memory.find { it.first == commands[0] } == null) {
            memory.add(Pair(commands[0], -1))
            changeList.add(memory.size)
            commands.removeAt(0)
        }
        else {
            commands.removeAt(0)
            changeList.add(-1)
        }
    }
    for (i in 0 until commands.size) {
        if (memory.find { it.first == commands[i] } != null) {
            changeList.add(-1)
        }
        else {
            for (j in i until commands.size) {
                for (k in 0 until memorySize) {
                    if ((memory[k].first == commands[j]) and (memory[k].second == -1)) {
                        memory[k] = Pair(memory[k].first, j)
                    }
                }
            }
            var maximum = 0
            for (j in 0 until memorySize) {
                if (memory[maximum].second == -1) break
                else if ((memory[j].second > memory[maximum].second) or (memory[j].second == -1)) {
                    maximum = j
                }
            }
            memory[maximum] = Pair(commands[i], -1)
            changeList.add(maximum + 1)
        }
    }
    return changeList
}

fun main() {
    val memorySize = readFileAsLinesUsingUseLines("inpout/conf.txt")[0].toInt()

    val commandsString = readFileAsLinesUsingUseLines("inpout/commands.txt")
    val commands = mutableListOf<Int>()
    for (i in commandsString) {
        commands.add(i.toInt())
    }

    val answer = File("inpout/answer.txt")
    if (answer.exists()) answer.delete()
    var answerContent = ""

    var countOfAnswersFifo = 0
    answerContent += "Результат выполнения FIFO: \n"
    for (i in fifo(commands, memorySize)) {
        answerContent += i.toString() + "\n"
        if (i != -1) countOfAnswersFifo++
    }

    var countOfAnswersLru = 0
    answerContent += "___________________________ \n"
    answerContent += "Результат выполнения LRU: \n"
    for (i in lru(commands, memorySize)) {
        answerContent += i.toString() + "\n"
        if (i != -1) countOfAnswersLru++
    }

    var countOfAnswersOpt = 0
    answerContent += "___________________________ \n"
    answerContent += "Результат выполнения OPT: \n"
    for (i in opt(commands, memorySize)) {
        answerContent += i.toString() + "\n"
        if (i != -1) countOfAnswersOpt++
    }
    answerContent += "___________________________ \n"
    answerContent += "Количество запросов второго типа: \n" +
                     "FIFO $countOfAnswersFifo \n" +
                     "LRU $countOfAnswersLru \n" +
                     "OPT $countOfAnswersOpt \n"
    answer.writeText(answerContent)

}