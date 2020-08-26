package com.infomaniak.login.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.lib.login.WebViewLoginActivity
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID
import com.infomaniak.login.example.BuildConfig.CLIENT_ID
import kotlinx.android.synthetic.main.activity_main.loginButton
import kotlinx.android.synthetic.main.activity_main.webViewLoginButton

class MainActivity : AppCompatActivity() {

    private lateinit var infomaniakLogin: InfomaniakLogin

    companion object{
        private const val WEB_VIEW_LOGIN_REQ = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        infomaniakLogin = InfomaniakLogin(
            context = this,
            loginUrl = "https://login.preprod.dev.infomaniak.ch/",
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
            val intent = Intent(this, WebViewLoginActivity::class.java).apply {
                putExtra(WebViewLoginActivity.CLIENT_ID_TAG, BuildConfig.CLIENT_ID)
                putExtra(WebViewLoginActivity.APPLICATION_ID_TAG, BuildConfig.APPLICATION_ID)
            }
            startActivityForResult(intent, WEB_VIEW_LOGIN_REQ)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEB_VIEW_LOGIN_REQ && resultCode == RESULT_OK) {
            val code = data?.extras?.getString(WebViewLoginActivity.CODE_TAG)
            val error = data?.extras?.getString(WebViewLoginActivity.ERROR_TAG)
            Log.e("WebView code", code ?: "")
            Log.e("WebView error", error ?: "")
        }
    }

    public override fun onDestroy() {
        infomaniakLogin.unbind()
        super.onDestroy()
    }
}
