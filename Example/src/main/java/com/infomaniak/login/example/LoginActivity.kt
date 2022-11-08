package com.infomaniak.login.example

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                    val okHttpClient = OkHttpClient.Builder()
                        .build()
                    infomaniakLogin.getToken(okHttpClient,
                        code,
                        { apiToken ->
                            textView.text = "${apiToken.userId} ${apiToken.accessToken}"
                            btnDeconnect.isVisible = true
                            btnDeconnect.setOnClickListener {
                                infomaniakLogin.deconnect(okHttpClient, apiToken) { error ->
                                    Log.e("Login", error.name)
                                    deleteTokenError.text =
                                        "Error in token deletion : ${error.name}"
                                }
                            }
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

    private fun InfomaniakLogin.deconnect(
        okHttpClient: OkHttpClient,
        token: ApiToken,
        onError: (error: InfomaniakLogin.ErrorStatus) -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            deleteToken(okHttpClient, token, onError) {
                lifecycleScope.launch(Dispatchers.Main) { onBackPressed() }
            }
        }
    }
}
