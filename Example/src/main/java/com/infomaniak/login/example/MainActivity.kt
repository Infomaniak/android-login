package com.infomaniak.login.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import com.infomaniak.login.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var infomaniakLogin: InfomaniakLogin

    override fun onCreate(savedInstanceState: Bundle?) = with(binding) {
        super.onCreate(savedInstanceState)
        setContentView(root)

        infomaniakLogin = InfomaniakLogin(
            context = this@MainActivity,
            clientID = CLIENT_ID_EXEMPLE,
            appUID = APPLICATION_ID_EXEMPLE,
        )

        infomaniakLogin.checkResponse(
            intent = intent,
            onSuccess = { code ->
                val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                    putExtra("code", code)
                }
                startActivity(intent)
            },
            onError = { error -> Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show() },
        )

        loginButton.setOnClickListener { infomaniakLogin.start() }

        webViewLoginButton.setOnClickListener { infomaniakLogin.startWebViewLogin(webViewLoginResultLauncher) }

        fragmentLoginButton.setOnClickListener { startActivity(Intent(this@MainActivity, FragmentMainActivity::class.java)) }
    }

    private val webViewLoginResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        with(result) {
            if (resultCode == RESULT_OK) {
                val code = data?.extras?.getString(InfomaniakLogin.CODE_TAG)

                if (!code.isNullOrBlank()) {
                    Log.d("WebView code", code)
                    val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                        putExtra("code", code)
                    }
                    startActivity(intent)
                } else {
                    val errorCode = data?.extras?.getString(InfomaniakLogin.ERROR_CODE_TAG)
                    val translatedError = data?.extras?.getString(InfomaniakLogin.ERROR_TRANSLATED_TAG)

                    Toast.makeText(this@MainActivity, translatedError, Toast.LENGTH_LONG).show()
                    Log.d("WebView error", errorCode ?: "")
                }
            }
        }
    }

    public override fun onDestroy() {
        infomaniakLogin.unbind()
        super.onDestroy()
    }
}
