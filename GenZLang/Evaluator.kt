package GenZLang

class Evaluator {
    val env = Environment()

    fun evaluate(expr: Expr): Any? {
        return try {
            eval(expr)
        } catch (e: RuntimeError) {
            println("[ line ${e.token.line} ] Runtime error: ${e.message}")
            null
        }
    }

    private fun eval(expr: Expr): Any? {
        return when (expr) {

            is Expr.Number -> expr.token.literal
            is Expr.Str -> expr.token.literal
            is Expr.CharLit -> expr.token.literal
            is Expr.Bool -> expr.token.literal
            Expr.Ghosted -> null
            is Expr.Ident -> {
                val full = expr.token.lexeme          // "@name", "$age", "%score"
                val identifier = if (full.startsWith("@") || full.startsWith("$") || full.startsWith("%")) {
                    full.substring(1)                  // remove the sigil
                } else {
                    full
                }

                env.get(identifier)                    // look up the value in the environment
            }
            is Expr.Group -> eval(expr.expression)

            is Expr.Unary -> evalUnary(expr)
            is Expr.Binary -> evalBinary(expr)
        }
    }

    private fun evalUnary(expr: Expr.Unary): Any? {
        val right = eval(expr.right)

        return when (expr.op) {
            TokenType.MINUS -> {
                if (right !is Double)
                    throw RuntimeError(expr.right.token(), "Operand must be a number.")
                -right
            }
            TokenType.NOT -> !isTruthy(right)
            else -> right
        }
    }

    private fun evalBinary(expr: Expr.Binary): Any? {   //so may mali pa sa equality if using boolean like (5>3) == true stuff like that
        val left = eval(expr.left)
        val right = eval(expr.right)
        //println("EVAL BINARY: left=$left (${left?.javaClass}), right=$right (${right?.javaClass}), op=${expr.op}")

        return when (expr.op) {

            TokenType.ADD -> addValues(expr, left, right)

            TokenType.MINUS -> numeric(expr, left, right) { a, b -> a - b }
            TokenType.MULTIPLY -> numeric(expr, left, right) { a, b -> a * b }
            TokenType.DIVIDE -> {
                val (a, b) = numericOperands(expr, left, right)
                if (b == 0.0)
                    throw RuntimeError(expr.right.token(), "Division by zero.")
                a / b
            }
            TokenType.MODULO -> numeric(expr, left, right) { a, b -> a % b }
            TokenType.EXPONENT -> numeric(expr, left, right) { a, b -> Math.pow(a, b) }

            TokenType.LESS -> compareNumbers(expr, left, right) { a, b -> a < b }
            TokenType.GREATER -> compareNumbers(expr, left, right) { a, b -> a > b }
            TokenType.L_EQUAL -> compareNumbers(expr, left, right) { a, b -> a <= b }
            TokenType.G_EQUAL -> compareNumbers(expr, left, right) { a, b -> a >= b }

            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            TokenType.NOT_EQUAL -> !isEqual(left, right)

            TokenType.AND -> isTruthy(left) && isTruthy(right)
            TokenType.OR -> isTruthy(left) || isTruthy(right)

            else -> null
        }
    }

    private fun addValues(expr: Expr.Binary, left: Any?, right: Any?): Any {
        return when {
            left is Double && right is Double -> left + right
            left is String && right is String -> left + right
            else -> throw RuntimeError(expr.left.token(),
                "Operands must be two numbers or two strings.")
        }
    }

    private fun numeric(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
        op: (Double, Double) -> Double
    ): Double {
        val (a, b) = numericOperands(expr, left, right)
        return op(a, b)
    }

    private fun numericOperands(expr: Expr.Binary, left: Any?, right: Any?): Pair<Double, Double> {
        if (left !is Double)
            throw RuntimeError(expr.left.token(), "Operands must be numbers.")
        if (right !is Double)
            throw RuntimeError(expr.right.token(), "Operands must be numbers.")
        return Pair(left, right)
    }

    private fun compareNumbers(
        expr: Expr.Binary,
        left: Any?,
        right: Any?,
        op: (Double, Double) -> Boolean
    ): Boolean {
        val (a, b) = numericOperands(expr, left, right)
        return op(a, b)
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {  //so for string ginakuha niya ang isa ka " so its comparing hello and "hello
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        return true
    }

    //printing
    fun execute(stmt:Stmt){
        when (stmt) {
            is Stmt.Print -> {
                val value = eval(stmt.expr)
                println(value)
            }
            is Stmt.ExprStmt -> {
                eval(stmt.expr) // ignore result
            }
            is Stmt.VarDecl -> {
                val value = eval(stmt.initializer)
                env.define(stmt.name.lexeme, value)
            }
            is Stmt.Assign -> {
                val value = eval(stmt.value)
                env.assign(stmt.name.lexeme, value)
            }
            else -> throw RuntimeException("Unimplemented statement type: ${stmt::class.simpleName}")
        }
    }

    fun executeProgram(statements: List<Stmt>) {
        for (stmt in statements) {
            execute(stmt)
        }
    }
}

class RuntimeError(val token: Token, msg: String) : RuntimeException(msg)
