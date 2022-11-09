package com.infomaniak.login.example

import android.os.Bundle
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
            intent.getStringExtra("code")?.let {
                val infomaniakLogin = InfomaniakLogin(
                    context = this,
                    clientID = CLIENT_ID_EXEMPLE,
                    appUID = APPLICATION_ID_EXEMPLE
                )

                lifecycleScope.launchWhenStarted {
                    val okHttpClient = OkHttpClient.Builder().build()

                    infomaniakLogin.getToken(okHttpClient, it,
                        { apiToken ->
                            getTokenStatus.text = "${apiToken.userId} ${apiToken.accessToken}"

                            btnDeconnect.apply {
                                isVisible = true
                                setOnClickListener { infomaniakLogin.deconnect(okHttpClient, apiToken) }
                            }
                        }, { error ->
                            when (error) {
                                InfomaniakLogin.ErrorStatus.SERVER -> getTokenStatus.text = "Server error"
                                else -> getTokenStatus.text = "Error"
                            }
                        })
                }
            }
        }
    }

    private fun InfomaniakLogin.deconnect(okHttpClient: OkHttpClient, token: ApiToken) {
        lifecycleScope.launch(Dispatchers.IO) {
            deleteToken(okHttpClient, token,
                {
                    deleteTokenStatus.text = "Delete token success"
                }, { error ->
                    deleteTokenStatus.text = "Error in token deletion : ${error.name}"
                }
            )
        }
    }
}
