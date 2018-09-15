package ca.mcgill.science.ctf.android

import android.app.Application
import ca.mcgill.science.ctf.android.preferences.Prefs

class CTFApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Prefs.initialize(this)
    }
}