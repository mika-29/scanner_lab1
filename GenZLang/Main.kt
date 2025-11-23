package GenZLang

fun main() {
    val evaluator = Evaluator()
    while (true) {
        print("> ")
        //val line = readLine() ?: break
        val line = readlnOrNull() ?: break
        if (line.isBlank()) continue

        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)

        try {
            val statements = parser.parseProgram()
            evaluator.executeProgram(statements)
            //if (result!= null) println(formatResult(result))
            //else println("nil")
            //AstPrinter.printExpr(expr)
        } catch (e: ParseError) {
            //val token = e.token
            //if (token.type == TokenType.END_OF_FILE){
                println("[line 1] Error at '${e.message}'")
           //} else {
                //println("[line ${token.line}] Error at '${token.lexeme}': ${e.message}")
            }
        }
}

fun formatResult(value: Any?): String {
    return when (value) {
        null -> "nil"
        is Double -> {
            val intVal = value.toInt()
            if (value == intVal.toDouble()) intVal.toString()
            else value.toString()
        }
        is String -> value
        else -> value.toString()
    }
}