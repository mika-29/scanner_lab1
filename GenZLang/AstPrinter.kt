package GenZLang

object AstPrinter {
    fun printExpr(expr: Expr) {
        println(print(expr))
    }

    fun print(expr: Expr): Any {
        return when (expr) {
            is Expr.Number -> expr.token.lexeme.toDouble()
            is Expr.Str -> expr.token.lexeme
            is Expr.CharLit -> expr.token.lexeme
            is Expr.Bool -> expr.token.lexeme
            is Expr.Ghosted -> "nil"
            is Expr.Ident -> expr.token.lexeme
            is Expr.Group -> "(group ${print(expr.expression)})"
            is Expr.Unary -> "(${opName(expr.op)} ${print(expr.right)})"
            is Expr.Binary -> return "(${opName(expr.op)} ${print(expr.left)} ${print(expr.right)})"
        }
    }

    private fun opName(op: TokenType): String = when (op) {
        TokenType.ADD -> "+"
        TokenType.MINUS -> "-"
        TokenType.MULTIPLY -> "*"
        TokenType.DIVIDE -> "/"
        TokenType.MODULO -> "%"
        TokenType.EXPONENT -> "^"
        TokenType.EQUAL_EQUAL -> "=="
        TokenType.NOT_EQUAL -> "!="
        TokenType.LESS -> "<"
        TokenType.GREATER -> ">"
        TokenType.L_EQUAL -> "<="
        TokenType.G_EQUAL -> ">="
        TokenType.AND -> "&"
        TokenType.OR -> "|"
        TokenType.NOT -> "!"
        else -> op.name
    }
}



