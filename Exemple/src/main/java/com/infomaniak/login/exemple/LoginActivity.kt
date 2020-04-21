package com.infomaniak.login.exemple

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.exemple.BuildConfig.APPLICATION_ID
import com.infomaniak.login.exemple.BuildConfig.CLIENT_ID
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
                    clientID = CLIENT_ID,
                    appUID = APPLICATION_ID
                )

                lifecycleScope.launchWhenStarted {
                    val okHttpClient = OkHttpClient.Builder()
                        .build()
                    infomaniakLogin.getToken(okHttpClient,
                        code,
                        { apiToken ->
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
                }
            }
        }
    }
}
