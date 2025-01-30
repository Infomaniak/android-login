package com.infomaniak.login.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.infomaniak.lib.login.ApiToken
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import com.infomaniak.login.example.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)

        if (intent != null) {
            intent.getStringExtra("code")?.let {
                val infomaniakLogin = InfomaniakLogin(
                    context = this@LoginActivity,
                    clientID = CLIENT_ID_EXEMPLE,
                    appUID = APPLICATION_ID_EXEMPLE,
                )

                lifecycleScope.launch {
                    val okHttpClient = OkHttpClient.Builder().build()

                    val tokenResult = infomaniakLogin.getToken(okHttpClient, code = it)
                    updateUi(tokenResult, infomaniakLogin, okHttpClient)
                }
            }
        }
    }

    private fun ActivityLoginBinding.updateUi(
        tokenResult: InfomaniakLogin.TokenResult,
        infomaniakLogin: InfomaniakLogin,
        okHttpClient: OkHttpClient,
    ) {
        when (tokenResult) {
            is InfomaniakLogin.TokenResult.Success -> {
                val apiToken = tokenResult.apiToken
                getTokenStatus.text = "${apiToken.userId} ${apiToken.accessToken}"

                btnLogout.apply {
                    isVisible = true
                    setOnClickListener { infomaniakLogin.logout(okHttpClient, apiToken) }
                }
            }

            is InfomaniakLogin.TokenResult.Error -> when (tokenResult.errorStatus) {
                InfomaniakLogin.ErrorStatus.SERVER -> getTokenStatus.text = "Server error"
                else -> getTokenStatus.text = "Error"
            }
        }
    }

    private fun InfomaniakLogin.logout(okHttpClient: OkHttpClient, token: ApiToken) = with(binding) {
        lifecycleScope.launch {
            when (val errorStatus = deleteToken(okHttpClient, token)) {
                null -> deleteTokenStatus.text = "Delete token success"
                else -> deleteTokenStatus.text = "Error in token deletion : ${errorStatus.name}"
            }
        }
    }
}
