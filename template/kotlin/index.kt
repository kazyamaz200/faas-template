import function.*

fun main(args: Array<String>) {
    var s = ""
    var y = readLine()
    while (y != null) {
        s += y + "\n"
        y = readLine()
    }
    function.main.handler(s)
}
