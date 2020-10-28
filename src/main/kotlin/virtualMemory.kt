import java.io.File

fun fileToList(fileName: String): MutableList<String> {
    return File(fileName).useLines { it.toMutableList() }
}

fun fifo(comms: MutableList<Int>, memorySize: Int): MutableList<Int>{
    val commands = comms.toMutableList() // создаю копию, чтоб не менялся оригинал
    val changesList = mutableListOf<Int>()
    val memory = mutableListOf<Int>() // в каждой ячейке памяти будет номер страницы

    makeMemoryFullFifo(memory, memorySize, commands, changesList)

    var firstPage = 0
    for (i in commands) {
        if (memory.find { it == i } == null ) {
            memory[firstPage] = i
            changesList.add(firstPage + 1)
            firstPage += 1
            if (firstPage == memorySize) firstPage = 0
        }
        else changesList.add(-1)
    }
    return changesList
}

fun makeMemoryFullFifo(memory: MutableList<Int>, memorySize: Int, commands: MutableList<Int>, changesList: MutableList<Int>) {
    while (memory.size < memorySize) {
        if (memory.find { it == commands[0] } == null) {
            memory.add(memory.size, commands[0])
            commands.removeAt(0)
            changesList.add(memory.size)
        } else {
            changesList.add(-1)
            commands.removeAt(0)
        }
    }
}

fun lru(comms: MutableList<Int>, memorySize: Int): MutableList<Int> {
    val commands = comms.toMutableList()
    val changesList = mutableListOf<Int>()
    val memory = mutableListOf<Pair<Int, Int>>() // первый аргумент в паре отвечает за номер страницы, второй аргумент отвечает за время, которое к этой странице не обращались
                                                 // одно изменение в памяти = одна единица времени
    makeMemoryFullLru(memory, memorySize, commands, changesList)

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
            changesList.add(idxOfFirst + 1)
        }
        else {
            changesList.add(-1)
            for (j in 0 until memory.size) {
                memory[j] = Pair(memory[j].first, memory[j].second + 1)
                if (memory[j].first == i) {
                    memory[j] = Pair(memory[j].first, 0)
                }
            }
        }
    }
    return changesList
}

private fun makeMemoryFullLru(
        memory: MutableList<Pair<Int, Int>>,
        memorySize: Int,
        commands: MutableList<Int>,
        changesList: MutableList<Int>
) {
    while (memory.size < memorySize) {
        for (i in 0 until memory.size) {
            memory[i] = Pair(memory[i].first, memory[i].second + 1)
        }
        if (memory.find { it.first == commands[0] } == null) {
            memory.add(commands[0] to 0)
            commands.removeAt(0)
            changesList.add(memory.size)
        } else {
            changesList.add(-1)
            for (i in 0 until memory.size) {
                if (memory[i].first == commands[0]) {
                    memory[i] = Pair(memory[i].first, 0)
                    commands.removeAt(0)
                }
            }
        }
    }
}

fun opt(comms: MutableList<Int>, memorySize: Int): MutableList<Int> {
    val commands = comms.toMutableList()
    val changesList = mutableListOf<Int>()
    val memory = mutableListOf<Pair<Int, Int>>() // здесь первый элемент в паре - номер страницы, второй элемент - следующее вхождение страницы
                                                 // (если второй элемент -1, значит это вхождение последнее или следующее еще не найдено)

    makeMemoryFullOpt(memory, memorySize, commands, changesList)

    for (i in 0 until commands.size) {
        if (memory.find { it.first == commands[i] } != null) {
            changesList.add(-1)
        }
        else {
            // ищем следующие запросы страниц
            for (j in i until commands.size) {
                for (k in 0 until memorySize) {
                    if ((memory[k].first == commands[j]) and (memory[k].second == -1)) {
                        memory[k] = Pair(memory[k].first, j)
                    }
                }
            }
            // ищем, что заменить
            var maximum = 0
            for (j in 0 until memorySize) {
                if (memory[maximum].second == -1) break
                else if ((memory[j].second > memory[maximum].second) or (memory[j].second == -1)) {
                    maximum = j
                }
            }
            memory[maximum] = Pair(commands[i], -1)
            changesList.add(maximum + 1)
        }
    }
    return changesList
}

private fun makeMemoryFullOpt(
        memory: MutableList<Pair<Int, Int>>,
        memorySize: Int,
        commands: MutableList<Int>,
        changesList: MutableList<Int>
) {
    while (memory.size < memorySize) {
        if (memory.find { it.first == commands[0] } == null) {
            memory.add(Pair(commands[0], -1))
            changesList.add(memory.size)
            commands.removeAt(0)
        } else {
            commands.removeAt(0)
            changesList.add(-1)
        }
    }
}

fun makeAnswer(memorySize: Int, commands: MutableList<Int>) {
    val answer = File("inpout/answer.txt")
    if (answer.exists()) answer.delete()
    var answerContent = ""

    answerContent += "Результат выполнения FIFO: \n"
    var pair = partOfAnswerAndCountOf2Type(fifo(commands, memorySize))
    val countOfAnswersFifo = pair.first
    answerContent += pair.second
    answerContent += "___________________________ \n"

    answerContent += "Результат выполнения LRU: \n"
    pair = partOfAnswerAndCountOf2Type(lru(commands, memorySize))
    val countOfAnswersLru = pair.first
    answerContent += pair.second
    answerContent += "___________________________ \n"

    answerContent += "Результат выполнения OPT: \n"
    pair = partOfAnswerAndCountOf2Type(opt(commands, memorySize))
    val countOfAnswersOpt = pair.first
    answerContent += pair.second
    answerContent += "___________________________ \n"

    answerContent += "Количество запросов второго типа: \n" +
            "FIFO $countOfAnswersFifo \n" +
            "LRU $countOfAnswersLru \n" +
            "OPT $countOfAnswersOpt \n"
    answer.writeText(answerContent)
}

fun partOfAnswerAndCountOf2Type(algo: MutableList<Int>): Pair<Int, String> {
    var answerContent = ""
    var countOfAnswers2type = 0
    for (i in algo) {
        answerContent += i.toString() + "\n"
        if (i != -1) countOfAnswers2type++
    }
    return Pair(countOfAnswers2type, answerContent)
}

fun getInput(): Pair<Int, MutableList<Int>> {
    val memorySize = fileToList("inpout/conf.txt")[0].toInt()

    val commandsString = fileToList("inpout/commands.txt")
    val commands = mutableListOf<Int>()
    for (i in commandsString) {
        commands.add(i.toInt())
    }
    return Pair(memorySize, commands)
}

fun main() {
    val (memorySize, commands) = getInput()
    makeAnswer(memorySize, commands)
}