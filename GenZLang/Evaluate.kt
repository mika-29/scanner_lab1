package GenZLang

fun Expr.token(): Token = when (this) {
        is Expr.Number -> token
        is Expr.Str -> token
        is Expr.CharLit -> token
        is Expr.Bool -> token
        is Expr.Ident -> token
        else -> Token(TokenType.NULL, "", null, 1)
}