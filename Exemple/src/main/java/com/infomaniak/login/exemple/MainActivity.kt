package com.infomaniak.login.exemple

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.exemple.GlobalConstants.APP_UID
import com.infomaniak.login.exemple.GlobalConstants.CLIENT_ID
import com.infomaniak.login.exemple.GlobalConstants.REDIRECT_URI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var infomaniakLogin: InfomaniakLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (infomaniakLogin == null) {
            infomaniakLogin = InfomaniakLogin(
                context = this,
                clientId = CLIENT_ID,
                appUID = APP_UID,
                redirectUri = REDIRECT_URI
            )
        }

        infomaniakLogin?.checkResponse(intent,
            { code ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("code", code)
                intent.putExtra("verifier", infomaniakLogin?.codeVerifier)
                startActivity(intent)
            },
            { error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        )

        loginButton.setOnClickListener {
            infomaniakLogin?.start()
        }

    }

    public override fun onDestroy() {
        infomaniakLogin?.unbind()
        super.onDestroy()
    }
}
