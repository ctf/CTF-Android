package ca.mcgill.science.ctf.fragments

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import ca.mcgill.science.ctf.MainActivity
import ca.mcgill.science.ctf.R
import ca.mcgill.science.ctf.auth.AccountUtil
import ca.mcgill.science.ctf.requests.CTFSpiceService
import ca.mcgill.science.ctf.requests.LogoutRequest
import com.gc.materialdesign.widgets.Dialog
import com.octo.android.robospice.SpiceManager
import com.octo.android.robospice.persistence.exception.SpiceException
import com.octo.android.robospice.request.listener.RequestListener
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {
    private var token: String? = null
    private val requestManager = SpiceManager(CTFSpiceService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = arguments.getString(KEY_TOKEN)
        val args = arguments
        if (args != null) {
            token = args.getString(KEY_TOKEN)
        }

    }

    override fun onStart() {
        super.onStart()
        requestManager.start(activity) //TODO remove and let RequestActivity handle Spice requests
    }

    override fun onCreatePreferences(bundle: Bundle, s: String) {
        preferenceManager.sharedPreferencesName = "CAPSULE_PREFERENCES"
        addPreferencesFromResource(R.xml.preferences)

        findPreference("pref_logout").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AccountUtil.removeAccount()
            activity.finish()
            true
        }

        findPreference("pref_language").onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
            if (o is String) {
                setLocale(activity, o)
                (activity as MainActivity).reload()
                //todo this brings us back to the main page, want to get back to settings page (fixed 2016/12/28)
            }
            true
        }

        findPreference("pref_theme").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            (activity as MainActivity).reload()
            false
        }

        findPreference("pref_about").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val dialog = Dialog(activity, "About", "...")
            dialog.show()
            true
        }

        findPreference("pref_logout").onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val session = AccountUtil.getSession()
            requestManager.execute(LogoutRequest(token, session.id), object : RequestListener<Void> {
                override fun onRequestFailure(spiceException: SpiceException) {
                    AccountUtil.removeAccount()
                    activity.finish()
                }

                override fun onRequestSuccess(v: Void) {
                    AccountUtil.removeAccount()
                    activity.finish()
                }
            })
            false
        }
    }

    override fun onStop() {
        if (requestManager.isStarted) requestManager.shouldStop()
        super.onStop()
    }

    /**
     * Static initializer for SettingsFragment
     */
    companion object {

        val KEY_TOKEN = "TOKEN"

        fun newInstance(token: String): SettingsFragment {
            val frag = SettingsFragment()
            val args = Bundle()
            args.putString(KEY_TOKEN, token)
            frag.arguments = args
            return frag
        }

        /**
         * helper method to change the system locale from one supported language to another
         *
         * @param activity a context
         * @param lang     ISO 639-1 code (e.g., "en", "fr", "zh", etc.)
         */
        fun setLocale(activity: Activity, lang: String) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)
        }
    }

}
