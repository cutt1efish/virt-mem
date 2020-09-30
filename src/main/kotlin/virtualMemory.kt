import java.io.File
import javax.print.attribute.standard.MediaSizeName
import kotlin.system.exitProcess

fun readFileAsLinesUsingUseLines(fileName: String): MutableList<String>
        = File(fileName).useLines { it.toMutableList() }

fun fifo(commands: MutableList<Int>, memorySize: Int, pagesCount: Int): MutableList<Int>{
    val changeList = mutableListOf<Int>()
    val memory = mutableListOf<Int>()
    val pages = Array(pagesCount) { 0 }
    while (memory.size < memorySize ) {
        memory.add(memory.size, commands[0])
        pages[commands[0]] += 1
        commands.removeAt(0)
        changeList.add(memory.size-1)
    }
    var firstPage = 0
    for (i in commands) {
        if (memory.find { it == i } == null ) {
            memory[firstPage] = i
            changeList.add(firstPage)
            pages[i-1] += 1
            firstPage += 1
            if (firstPage == memorySize) firstPage = 0
        }
        else changeList.add(-1)
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

    for (i in fifo(commands, memorySize, pagesCount)) {
        println(i)
    }
}