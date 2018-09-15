package ca.mcgill.science.ctf.android.preferences

import android.content.Context
import android.content.SharedPreferences

object Prefs {

    private const val NAME = "ctf.preferences"
    internal lateinit var sp: SharedPreferences

    fun initialize(c: Context) {
        sp = c.applicationContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    var tepidToken by pref("TEPID_TOKEN", "")

    var shortUser by pref("SHORT_USER", "")

    var email by pref("EMAIL", "")

    var role by pref("ROLE", "")

}