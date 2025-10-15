package GenZLang

fun main() {
    while (true) {
        print("> ")
        val line = readLine() ?: break

        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)

        try {
            val expr = parser.parseExpression()
            println("✅ Parsed successfully: $expr")
        } catch (e: ParseError) {
            println("❌ Parse error: ${e.message}")
        }
    }
}
