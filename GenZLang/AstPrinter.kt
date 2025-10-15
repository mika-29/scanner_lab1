package GenZLang

object AstPrinter {
    fun print(expr: Expr): String = when (expr) {
        is Expr.Number -> expr.value.toString()
        is Expr.StringLit -> expr.value
        is Expr.CharLit -> expr.value.toString()
        is Expr.Bool -> expr.value.toString()
        is Expr.Ghosted -> "nil"
        is Expr.Ident -> expr.name
        is Expr.Group -> "(group ${print(expr.expr)})"
        is Expr.Unary -> "(${opName(expr.op)} ${print(expr.right)})"
        is Expr.Binary -> "(${opName(expr.op)} ${print(expr.left)} ${print(expr.right)})"
    }

    private fun opName(op: TokenType): String = when (op) {
        TokenType.ADD -> "+"
        TokenType.MINUS -> "-"
        TokenType.STAR -> "*"
        TokenType.SLASH -> "/"
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
