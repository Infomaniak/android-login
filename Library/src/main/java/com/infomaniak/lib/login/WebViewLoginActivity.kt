package com.infomaniak.lib.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.infomaniak.lib.login.InfomaniakLogin.Companion.LOGIN_URL_TAG
import kotlinx.android.synthetic.main.activity_web_view_login.*
import java.util.*

class WebViewLoginActivity : AppCompatActivity() {

    private lateinit var appUID: String

    companion object {

        const val APPLICATION_ID_TAG = "appUID"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view_login)
        setSupportActionBar(toolbar)

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        val loginUrl = intent.extras?.getString(LOGIN_URL_TAG)
            ?: throw MissingFormatArgumentException(LOGIN_URL_TAG)
        appUID = intent.extras?.getString(APPLICATION_ID_TAG)
            ?: throw MissingFormatArgumentException(APPLICATION_ID_TAG)

        webview.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    return !isValidUrl(url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return !isValidUrl(url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = 0
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    progressBar.progress = 100
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    errorResult(InfomaniakLogin.SSL_ERROR_CODE)
                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (isValidUrl(request?.url.toString())) {
                        errorResult(error?.description?.toString() ?: "")
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    if (isValidUrl(failingUrl)) {
                        errorResult(description ?: "")
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    if (request?.method == "GET") errorResult(InfomaniakLogin.HTTP_ERROR_CODE)
                }
            }

            webview.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    progressBar.progress = newProgress
                    if (newProgress == 100) {
                        progressBar.visibility = View.GONE
                    }
                }
            }
            loadUrl(loginUrl)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.webview_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.doneItem -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    private fun isValidUrl(url: String?): Boolean {
        if (url == null) return false
        if (onAuthResponse(Uri.parse(url))) return false
        return url.contains("login.infomaniak.com")
                || url.contains("oauth2redirect")
                || url.contains("gstatic.com")
                || url.contains("google.com/recaptcha")
    }

    private fun onAuthResponse(uri: Uri?): Boolean {
        return if (uri?.scheme == appUID) {
            uri.getQueryParameter("code")?.let { code ->
                successResult(code)
            } ?: run {
                errorResult(uri.getQueryParameter("error") ?: "")
            }
            true
        } else false
    }

    private fun successResult(code: String) {
        val intent = Intent().apply {
            putExtra(InfomaniakLogin.CODE_TAG, code)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun errorResult(errorCode: String) {
        val intent = Intent().apply {
            putExtra(InfomaniakLogin.ERROR_CODE_TAG, errorCode)
            putExtra(InfomaniakLogin.ERROR_TRANSLATED_TAG, translateError(errorCode))
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun translateError(errorCode: String): String {
        return when (errorCode) {
            InfomaniakLogin.WEBVIEW_ERROR_CODE_INTERNET_DISCONNECTED -> getString(R.string.connection_error)
            InfomaniakLogin.WEBVIEW_ERROR_CODE_CONNECTION_REFUSED -> getString(R.string.connection_error)
            InfomaniakLogin.ERROR_ACCESS_DENIED -> getString(R.string.access_denied)
            else -> getString(R.string.an_error_has_occurred)
        }
    }
}