import java.io.File
import kotlin.random.Random

fun main() {
    val conf = File("generator/conf.txt")
    if (conf.exists()) conf.delete()
    val commands = File("generator/commands.txt")
    if (commands.exists()) commands.delete()

    val confMax: Int
    val pagesMax: Int
    val commandsMax: Int
    if (conf.exists()) {
        val maxs = fileToList("generator/gen.txt")
        confMax = maxs[0].toInt()
        pagesMax = maxs[1].toInt()
        commandsMax = maxs[2].toInt()
    }
    else {
        confMax = 100
        pagesMax = 1000
        commandsMax = 10000
    }

    val confContent = "${Random.nextInt(0 , confMax)}"
    var commandsContent = ""
    val pagesCount = Random.nextInt(confContent.toInt(), pagesMax)
    for (i in 0..Random.nextInt(pagesCount, commandsMax)) {
        commandsContent += "${Random.nextInt(0, pagesCount) + 1}\n"
    }

    conf.writeText(confContent)
    commands.writeText(commandsContent)
}