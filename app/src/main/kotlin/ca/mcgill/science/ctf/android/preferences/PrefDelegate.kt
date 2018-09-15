package ca.mcgill.science.ctf.android.preferences

/**
 * Simplified copy of pref delegate from <a href="https://github.com/AllanWang/KAU">KAU</a>
 */
class PrefDelegate<T : Any> internal constructor(
        private val key: String, private val fallback: T, private val pref: Prefs) : Lazy<T> {

    private object UNINITIALIZED

    @Volatile private var _value: Any = UNINITIALIZED
    private val lock = Any()

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED)
                @Suppress("UNCHECKED_CAST")
                return _v1 as T

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED) {
                    @Suppress("UNCHECKED_CAST")
                    _v2 as T
                } else {
                    _value = when (fallback) {
                        is Boolean -> pref.sp.getBoolean(key, fallback)
                        is Float -> pref.sp.getFloat(key, fallback)
                        is Int -> pref.sp.getInt(key, fallback)
                        is Long -> pref.sp.getLong(key, fallback)
                        is String -> pref.sp.getString(key, fallback)
                        else -> throw PrefException(fallback)
                    }
                    @Suppress("UNCHECKED_CAST")
                    _value as T
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy pref $key not initialized yet."

    operator fun setValue(any: Any, property: kotlin.reflect.KProperty<*>, t: T) {
        _value = t
        val editor = pref.sp.edit()
        when (t) {
            is Boolean -> editor.putBoolean(key, t)
            is Float -> editor.putFloat(key, t)
            is Int -> editor.putInt(key, t)
            is Long -> editor.putLong(key, t)
            is String -> editor.putString(key, t)
            else -> throw PrefException(t)
        }
        editor.apply()
    }
}

class PrefException(message: String) : IllegalAccessException(message) {
    constructor(element: Any?) : this("Invalid type in pref cache: ${element?.javaClass?.simpleName ?: "null"}")
}

fun Prefs.pref(key: String, fallback: Boolean) = PrefDelegate(key, fallback, this)
fun Prefs.pref(key: String, fallback: Double) = PrefDelegate(key, fallback.toFloat(), this)
fun Prefs.pref(key: String, fallback: Float) = PrefDelegate(key, fallback, this)
fun Prefs.pref(key: String, fallback: Int) = PrefDelegate(key, fallback, this)
fun Prefs.pref(key: String, fallback: Long) = PrefDelegate(key, fallback, this)
fun Prefs.pref(key: String, fallback: String) = PrefDelegate(key, fallback, this)
