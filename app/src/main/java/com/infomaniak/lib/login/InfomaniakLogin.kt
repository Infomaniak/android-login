package com.infomaniak.lib.login

import android.content.ComponentName
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.URLUtil
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import java.security.MessageDigest
import java.security.SecureRandom

class InfomaniakLogin(
    private val context: Context,
    private val clientId: String,
    private val redirectUri: String) {

    companion object {
        private const val CHROME_STABLE_PACKAGE = "com.android.chrome"
        private const val SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService"
        private const val LOGIN_URL = "https://login.infomaniak.com/authorize/"
        private const val DEFAULT_RESPONSE_TYPE = "code"
        private const val DEFAULT_ACCESS_TYPE = "offline"
    }

    private lateinit var codeChallengeMethod: String
    internal lateinit var codeChallenge: String
    internal lateinit var codeVerifier: String
    lateinit var loginUrl: String

    private var tabClient: CustomTabsClient? = null
    private var tabConnection: CustomTabsServiceConnection? = null
    private val tabIntent: CustomTabsIntent by lazy {
        CustomTabsIntent.Builder()
            .run {
                build()
            }.also { customTabIntent ->
                customTabIntent.intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
    }

    init {
        // Generate the codes for PKCE Challenge
        generatePkceCodes()
        // Generate the complete login URL based on codes and arguments
        generateUrl()
    }

    fun start(): Boolean {
        var success = false
        if (URLUtil.isValidUrl(loginUrl)) {
            when {
                isChromeCustomTabsSupported(context) -> bindCustomTabsService(loginUrl)
                else -> {
                    success = showOnDefaultBrowser((loginUrl))
                }
            }
        }
        return success
    }

    private fun unbind() {
        try {
            context.unbindService(tabConnection!!)
        } catch (ignore: Exception) {
            Log.e("kLogin error", "The login service cannot be unbinded")
        }
    }

    private fun showOnDefaultBrowser(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e("kLogin error", "Unable to start")
            false
        }
    }

    private fun bindCustomTabsService(url: String) {
        tabConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                componentName: ComponentName,
                client: CustomTabsClient
            ) {
                tabClient = client
                launchCustomTab(url)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                tabClient = null
            }
        }

        unbind()
        CustomTabsClient.bindCustomTabsService(context, CHROME_STABLE_PACKAGE, tabConnection!!)
    }

    private fun launchCustomTab(url: String) {
        tabClient?.warmup(0L)
        tabIntent.launchUrl(context, Uri.parse(url))
    }

    private fun isChromeCustomTabsSupported(context: Context): Boolean {
        val serviceIntent = Intent(SERVICE_ACTION).apply {
            setPackage(CHROME_STABLE_PACKAGE)
        }
        val resolveInfos: MutableList<ResolveInfo>? =
            context.packageManager.queryIntentServices(serviceIntent, 0)
        return !resolveInfos.isNullOrEmpty()
    }


    private fun generatePkceCodes() {
        codeChallengeMethod = "S256"

        val preferenceName = "pkce_step_codes"
        val verifierTag = "code_verifier"
        val challengeTag = "code_challenge"

        val prefs: SharedPreferences = context.getSharedPreferences(preferenceName, MODE_PRIVATE)
        val verifier = prefs.getString(verifierTag, null)
        val challenge = prefs.getString(challengeTag, null)

        if (challenge == null || verifier == null) {
            codeVerifier = generateCodeVerifier()
            codeChallenge = generateCodeChallenge(codeVerifier)
            val editor = context.getSharedPreferences(preferenceName, MODE_PRIVATE).edit()
            editor.putString(verifierTag, codeVerifier)
            editor.putString(challengeTag, codeChallenge)
            editor.apply()
        } else {
            codeVerifier = verifier
            codeChallenge = challenge
        }
    }

    private fun generateUrl() {
        loginUrl = LOGIN_URL +
                "?response_type=$DEFAULT_RESPONSE_TYPE" +
                "&access_type=$DEFAULT_ACCESS_TYPE" +
                "&client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&code_challenge_method=$codeChallengeMethod" +
                "&code_challenge=$codeChallenge"
    }

    private fun generateCodeVerifier(): String {
        val sr = SecureRandom()
        val code = ByteArray(33)
        sr.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(bytes, 0, bytes.size)
        val digest = md.digest()
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
