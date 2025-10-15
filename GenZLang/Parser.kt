package GenZLang

class ParseError(message: String) : RuntimeException(message)

class Parser(private val tokens: List<Token>) {
    private var current = 0

    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]
    private fun isAtEnd() = peek().type == TokenType.END_OF_FILE
    private fun advance(): Token { if (!isAtEnd()) current++; return previous() }
    private fun check(type: TokenType) = !isAtEnd() && peek().type == type
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) { advance(); return true }
        }
        return false
    }
    private fun consume(type: TokenType, msg: String): Token {
        if (check(type)) return advance()
        throw ParseError("Expected $type but got ${peek().lexeme}. $msg")
    }

    // ----------------------------
    // ENTRY POINT
    // ----------------------------
    fun parseExpression(): Expr = parseLogicalOr()

    // ----------------------------
    // RECURSIVE EXPRESSION RULES
    // ----------------------------

    private fun parseLogicalOr(): Expr {
        var expr = parseLogicalAnd()
        while (match(TokenType.OR)) {
            val op = previous().type
            val right = parseLogicalAnd()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseLogicalAnd(): Expr {
        var expr = parseEquality()
        while (match(TokenType.AND)) {
            val op = previous().type
            val right = parseEquality()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseEquality(): Expr {
        var expr = parseRelational()
        while (match(TokenType.EQUAL_EQUAL, TokenType.NOT_EQUAL)) {
            val op = previous().type
            val right = parseRelational()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseRelational(): Expr {
        var expr = parseAdditive()
        while (match(TokenType.LESS, TokenType.GREATER, TokenType.L_EQUAL, TokenType.G_EQUAL)) {
            val op = previous().type
            val right = parseAdditive()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseAdditive(): Expr {
        var expr = parseTerm()
        while (match(TokenType.ADD, TokenType.MINUS)) {
            val op = previous().type
            val right = parseTerm()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseTerm(): Expr {
        var expr = parseFactor()
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.MODULO)) {
            val op = previous().type
            val right = parseFactor()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    private fun parseFactor(): Expr {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            val op = previous().type
            val right = parseFactor()
            return Expr.Unary(op, right)
        }
        return parsePower()
    }

    private fun parsePower(): Expr {
        var base = parsePrimary()
        if (match(TokenType.EXPONENT)) {
            val op = previous().type
            val right = parseFactor() // right-associative
            base = Expr.Binary(base, op, right)
        }
        return base
    }

    private fun parsePrimary(): Expr {
        when {
            match(TokenType.NUM) -> return Expr.Number(previous().literal as Double)
            match(TokenType.STR) -> return Expr.StringLit(previous().literal as String)
            match(TokenType.CHAR) -> return Expr.CharLit(previous().literal as Char)
            match(TokenType.TRUE) -> return Expr.Bool(true)
            match(TokenType.FALSE) -> return Expr.Bool(false)
            match(TokenType.NULL) -> return Expr.Ghosted
            match(TokenType.IDENTIFIER) -> return Expr.Ident(previous().lexeme)
            match(TokenType.LPAR) -> {
                val expr = parseExpression()
                consume(TokenType.RPAR, "Expect ')' after expression.")
                return Expr.Group(expr)
            }
            else -> throw ParseError("Unexpected token '${peek().lexeme}'")
        }
    }
}
