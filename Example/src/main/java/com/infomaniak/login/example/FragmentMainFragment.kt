package com.infomaniak.login.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import kotlinx.android.synthetic.main.main_fragment.*

class FragmentMainFragment : Fragment() {

    companion object {
        fun newInstance() = FragmentMainFragment()
        private const val WEB_VIEW_LOGIN_REQ = 42
    }

    private lateinit var infomaniakLogin: InfomaniakLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        infomaniakLogin = InfomaniakLogin(
            context = requireContext(),
            clientID = CLIENT_ID_EXEMPLE,
            appUID = APPLICATION_ID_EXEMPLE
        )

        webViewLoginButton.setOnClickListener {
            infomaniakLogin.startWebViewLogin(WEB_VIEW_LOGIN_REQ, this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WEB_VIEW_LOGIN_REQ && resultCode == AppCompatActivity.RESULT_OK) {
            val code = data?.extras?.getString(InfomaniakLogin.CODE_TAG)

            if (!code.isNullOrBlank()) {
                Log.d("WebView code", code)
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    putExtra("code", code)
                }
                startActivity(intent)
            } else {
                val errorCode = data?.extras?.getString(InfomaniakLogin.ERROR_CODE_TAG)
                val translatedError = data?.extras?.getString(InfomaniakLogin.ERROR_TRANSLATED_TAG)

                Toast.makeText(requireContext(), translatedError, Toast.LENGTH_LONG).show()
                Log.d("WebView error", errorCode ?: "")
            }
        }
    }

}