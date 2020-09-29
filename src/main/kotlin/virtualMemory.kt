import java.io.File
import kotlin.system.exitProcess

fun readFileAsLinesUsingUseLines(fileName: String): MutableList<String>
        = File(fileName).useLines { it.toMutableList() }



fun main() {
    val conf = readFileAsLinesUsingUseLines("inpout/conf.txt")[0].split(' ')
    val memorySize = conf[0].toInt()
    val pagesCount = conf[1].toInt()
    if (memorySize >=  pagesCount) {
        println("Некорректный формат данных. Ожидается, что количество кадров меньше размера адресного пространства процессора.")
        exitProcess(0)
    }

    val commandsString = readFileAsLinesUsingUseLines("inpout/commands.txt")
    var commands = mutableListOf<Int>()
    for (i in commandsString) {
        commands.add(i.toInt())
    }

}