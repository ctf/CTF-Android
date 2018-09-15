package ca.mcgill.science.ctf.android.api

import ca.mcgill.science.ctf.android.preferences.Prefs
import ca.mcgill.science.ctf.tepid.api.TepidApi
import ca.mcgill.science.ctf.tepid.api.TokenInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TEPID_BASE_URL = "https://tepid.science.mcgill.ca:8443/tepid/"

val tepidApi: TepidApi by lazy {

    val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(Prefs::tepidToken))

    Retrofit.Builder()
            .baseUrl(TEPID_BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client.build())
            .build()
            .create(TepidApi::class.java)
}