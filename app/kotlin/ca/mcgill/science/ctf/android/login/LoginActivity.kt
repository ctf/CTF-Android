package ca.mcgill.science.ctf.android.login

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import ca.mcgill.science.ctf.android.R
import ca.mcgill.science.ctf.android.api.tepidApi
import ca.mcgill.science.ctf.android.application.BaseActivity
import ca.mcgill.science.ctf.android.application.MainActivity
import ca.mcgill.science.ctf.android.preferences.Prefs
import ca.mcgill.science.ctf.android.utils.setIcon
import ca.mcgill.science.ctf.android.utils.startActivity
import ca.mcgill.science.ctf.tepid.models.SessionRequest
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.experimental.launch
import java.io.IOException

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        login_input_sam.apply {
            setText(Prefs.email.takeIf(String::isNotBlank) ?: Prefs.shortUser)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    checkLogin()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })

        }

        login_input_password.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    checkLogin()
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })
        }

        login_fab_send.apply {
            setIcon(GoogleMaterial.Icon.gmd_send)
            hide()
            setOnClickListener {
                attemptLogin()
            }
        }
    }

    private fun checkLogin() {
        val isPossible = isLoginPossible()
        if (isPossible == fabVisible) return
        fabVisible = isPossible
        if (fabVisible) login_fab_send.show()
        else login_fab_send.hide()
    }

    private var fabVisible = false

    private fun isLoginPossible(): Boolean =
            login_input_sam.text?.isNotBlank() == true && login_input_password.text?.isNotBlank() == true

    private fun attemptLogin() {
        val sam = login_input_sam.text!!.toString().let {
            if (it.contains(".") && !it.contains("@")) "$it@mail.mcgill.ca" else it
        }
        val password = login_input_password.text!!.toString()
        launch {
            try {
                val session = tepidApi.getSession(SessionRequest(username = sam, password = password)).await()
                Prefs.role = session.role
                Prefs.email = session.user.email ?: ""
                Prefs.shortUser = session.user.shortUser ?: ""
                Prefs.tepidToken = session.authHeader
                startActivity<MainActivity>(clearStack = true)
            } catch (e: IOException) {
                login_input_password.error = getString(R.string.login_failed)
                login_input_password.text?.clear()
            }
        }
    }

    companion object {
        fun launch(context: Activity) {
            // clear prefs
            Prefs.tepidToken = ""
            context.startActivity<LoginActivity>(clearStack = true)
        }
    }
}