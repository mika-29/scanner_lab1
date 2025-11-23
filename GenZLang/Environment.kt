package GenZLang

class Environment  {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun assign(name: String, value: Any?) {
        if (!values.containsKey(name))
            throw RuntimeException("Undefined variable '$name'")
        values[name] = value
    }

    fun get(name: String): Any? {
        if (!values.containsKey(name)) {
            throw RuntimeException("Undefined variable '$name'")
        }
        return values[name]
    }
}