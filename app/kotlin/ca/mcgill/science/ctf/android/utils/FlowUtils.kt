package ca.mcgill.science.ctf.android.utils

import android.content.Context
import ca.mcgill.science.ctf.android.login.LoginActivity
import kotlinx.coroutines.experimental.Deferred
import java.io.IOException

suspend fun <T> Deferred<T>.awaitOrRedirect(context: Context): T? =
    try {
        await()
    } catch (e: IOException) {
        LoginActivity.launch(context)
        null
    }