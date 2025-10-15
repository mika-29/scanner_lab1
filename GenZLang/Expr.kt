package GenZLang

sealed class Expr {
    data class Number(val value: Double) : Expr()
    data class Str(val value: String) : Expr()
    data class CharLit(val value: Char) : Expr()
    data class Bool(val value: Boolean) : Expr()
    object Ghosted : Expr() // represents null
    data class Ident(val name: String) : Expr()
    data class Group(val expr: Expr) : Expr()
    data class Unary(val op: TokenType, val right: Expr) : Expr()
    data class Binary(val left: Expr, val op: TokenType, val right: Expr) : Expr()
}
