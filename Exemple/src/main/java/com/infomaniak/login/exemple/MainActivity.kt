package com.infomaniak.login.exemple

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.exemple.BuildConfig.APPLICATION_ID
import com.infomaniak.login.exemple.BuildConfig.CLIENT_ID
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var infomaniakLogin: InfomaniakLogin

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

    }

    public override fun onDestroy() {
        infomaniakLogin.unbind()
        super.onDestroy()
    }
}
