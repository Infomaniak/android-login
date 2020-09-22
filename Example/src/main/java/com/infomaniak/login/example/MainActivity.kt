package com.infomaniak.login.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID
import com.infomaniak.login.example.BuildConfig.CLIENT_ID
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var infomaniakLogin: InfomaniakLogin

    companion object {
        private const val WEB_VIEW_LOGIN_REQ = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infomaniakLogin = InfomaniakLogin(
            context = this,
            clientID = CLIENT_ID,
            appUID = APPLICATION_ID
        )

        infomaniakLogin.checkResponse(intent,
            { code ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("code", code)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )

        loginButton.setOnClickListener {
            infomaniakLogin.start()
        }

        webViewLoginButton.setOnClickListener {
            infomaniakLogin.startWebViewLogin(WEB_VIEW_LOGIN_REQ)
        }

        fragmentLoginButton.setOnClickListener {
            val intent = Intent(this, FragmentMainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEB_VIEW_LOGIN_REQ && resultCode == RESULT_OK) {
            val code = data?.extras?.getString(InfomaniakLogin.CODE_TAG)
            val translatedError = data?.extras?.getString(InfomaniakLogin.ERROR_TRANSLATED_TAG)
            val errorCode = data?.extras?.getString(InfomaniakLogin.ERROR_CODE_TAG)

            if (!translatedError.isNullOrBlank()) {
                Toast.makeText(this, translatedError, Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    putExtra("code", code)
                }
                startActivity(intent)
            }
            Log.e("WebView code", code ?: "")
            Log.e("WebView error", errorCode ?: "")
        }
    }

    public override fun onDestroy() {
        infomaniakLogin.unbind()
        super.onDestroy()
    }
}
