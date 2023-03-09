/*
 * Infomaniak Login - Android
 * Copyright (C) 2023 Infomaniak Network SA
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.infomaniak.lib.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible

open class LoginWebViewClient(
    private val activity: Activity,
    private val progressBar: ProgressBar,
    private val appUID: String
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        progressBar.progress = 0
        progressBar.isVisible = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        progressBar.progress = 100
        progressBar.isGone = true
    }

    @Deprecated("Support API below 24")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return !isValidUrl(url)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        error?.certificate?.apply {
            if (issuedBy?.cName == "localhost" && issuedTo?.cName == "localhost") return
        }
        errorResult(InfomaniakLogin.SSL_ERROR_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError) {
        onReceivedError(view, error.errorCode, error.description.toString(), request?.url.toString())
    }

    @Deprecated("Support API below 23")
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String, failingUrl: String) {
        if (isValidUrl(failingUrl)) {
            errorResult(description ?: "")
        }
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        if (request?.method == "GET") errorResult(InfomaniakLogin.HTTP_ERROR_CODE)
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

    private fun successResult(code: String) = with(activity) {
        val intent = Intent().apply {
            putExtra(InfomaniakLogin.CODE_TAG, code)
        }
        setResult(AppCompatActivity.RESULT_OK, intent)
        finish()
    }

    protected fun errorResult(errorCode: String) = with(activity) {
        val intent = Intent().apply {
            putExtra(InfomaniakLogin.ERROR_CODE_TAG, errorCode)
            putExtra(InfomaniakLogin.ERROR_TRANSLATED_TAG, translateError(errorCode))
        }
        setResult(AppCompatActivity.RESULT_OK, intent)
        finish()
    }

    private fun translateError(errorCode: String): String = with(activity) {
        return when (errorCode) {
            InfomaniakLogin.WEBVIEW_ERROR_CODE_INTERNET_DISCONNECTED -> getString(R.string.connection_error)
            InfomaniakLogin.WEBVIEW_ERROR_CODE_CONNECTION_REFUSED -> getString(R.string.connection_error)
            InfomaniakLogin.ERROR_ACCESS_DENIED -> getString(R.string.access_denied)
            else -> getString(R.string.an_error_has_occurred)
        }
    }
}
