package com.infomaniak.login.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (intent != null) {
            val code = intent.getStringExtra("code")
            code?.let {
                val infomaniakLogin = InfomaniakLogin(
                    context = this,
                    clientID = CLIENT_ID_EXEMPLE,
                    appUID = APPLICATION_ID_EXEMPLE
                )

                lifecycleScope.launchWhenStarted {
                    lateinit var token: ApiToken;
                    val okHttpClient = OkHttpClient.Builder()
                        .build()
                    infomaniakLogin.getToken(okHttpClient,
                        code,
                        { apiToken ->
                            token = apiToken
                            textView.text = "${apiToken.userId} ${apiToken.accessToken}"
                        }, { error ->
                            when (error) {
                                InfomaniakLogin.ErrorStatus.SERVER -> {
                                    textView.text = "Server error"
                                }
                                else -> {
                                    textView.text = "Error"
                                }
                            }
                        })

                    infomaniakLogin.deleteToken(
                        okHttpClient,
                        token,
                        { Log.e("Login", "success") },
                        { error -> Log.e("Login", error.name) }
                    )
                }
            }
        }
    }
}
