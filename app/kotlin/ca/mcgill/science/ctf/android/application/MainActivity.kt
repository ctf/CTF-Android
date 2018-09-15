package ca.mcgill.science.ctf.android.application

import android.os.Bundle
import ca.mcgill.science.ctf.android.api.tepidApi
import ca.mcgill.science.ctf.android.preferences.Prefs
import ca.mcgill.science.ctf.android.utils.awaitOrRedirect
import ca.mcgill.science.ctf.android.utils.logd
import kotlinx.coroutines.experimental.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            val me = tepidApi.getUser(Prefs.shortUser).awaitOrRedirect(this@MainActivity)
                    ?: return@launch
            logd("This is me $me")
        }
    }
}
