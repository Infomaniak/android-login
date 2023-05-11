package com.infomaniak.login.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.infomaniak.lib.login.InfomaniakLogin
import com.infomaniak.login.example.BuildConfig.APPLICATION_ID_EXEMPLE
import com.infomaniak.login.example.BuildConfig.CLIENT_ID_EXEMPLE
import com.infomaniak.login.example.databinding.FragmentMainBinding

class FragmentMainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    companion object {
        fun newInstance() = FragmentMainFragment()
    }

    private lateinit var infomaniakLogin: InfomaniakLogin


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentMainBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infomaniakLogin = InfomaniakLogin(
            context = requireContext(),
            clientID = CLIENT_ID_EXEMPLE,
            appUID = APPLICATION_ID_EXEMPLE,
        )

        binding.webViewLoginButton.setOnClickListener { infomaniakLogin.startWebViewLogin(webViewLoginResultLauncher) }
    }

    private val webViewLoginResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        with(result) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
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
}
