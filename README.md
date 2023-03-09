# Infomaniak Login Library

Library to simplify login process with Infomaniak oauth 2.0 protocol

## Install

In the `build.gradle` of your lib/app (not root level), add this instruction :

```groovy
implementation 'com.github.Infomaniak:android-login:2.7.0'
```

## Use Login

In the `onCreate` method of your Activity/Fragment, instantiate the library :

```kotlin
val loginInstance = InfomaniakLogin(
    context = this,
    clientId = CLIENT_ID,
    redirectUri = "$APP_UID:/oauth2redirect"
)
```

with these arguments :

- `context` : The current activity/fragment context (will be used to show a Chrometab)
- `clientId` : The client ID of the app
- `redirectUri` : The redirection URL after a successful login (in order to handle the codes)

**Once instantiated, you may use this everywhere in your activity/fragment, for example in a `onClick` method :**

```kotlin
buttonConnect.setOnClickListener {
    loginInstance.start()
}
```

It will create a custom chrome tab or launch the browser.
If needed (to see if an error occurred), the `start()` method returns a boolean indicating the success (or not) of the operation.

**Or you can use the `loginInstance` to create a login from a webview.**

```kotlin
buttonConnect.setOnClickListener {
    infomaniakLogin.startWebViewLogin(/*resultLauncher: ActivityResultLauncher<Intent>*/)
}
```

It will create an activity that contains a WebView. The `startWebViewLogin` takes as parameter a resultLauncher to handle the
activity results.

```kotlin
private val webViewLoginResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
    with(result) {
        if (resultCode == RESULT_OK) {
            val authCode = data?.getStringExtra(InfomaniakLogin.CODE_TAG)
            val translatedError = data?.getStringExtra(InfomaniakLogin.ERROR_TRANSLATED_TAG)
            when {
                translatedError?.isNotBlank() == true -> showError(translatedError)
                authCode?.isNotBlank() == true -> authenticateUser(authCode)
                else -> showError(getString(RCore.string.anErrorHasOccurred))
            }
        }
    }
}
```

This allows you to retrieve the answers from the WebView, either the `code` for `auth` in case of success, or in case of error, we
retrieve the `error code` and the `translated error message`.

Add this to the manifest, to authorize the WebView :

```xml

<activity android:name="com.infomaniak.lib.login.WebViewLoginActivity" />
```

## Use Create account

**From your `loginInstance`, you need to call the method `startCreateAccountWebView`**

```kotlin
infomaniakLogin.startCreateAccountWebView(
    resultLauncher = createAccountResultLauncher,
    createAccountUrl = BuildConfig.CREATE_ACCOUNT_URL,
    successHost = BuildConfig.CREATE_ACCOUNT_SUCCESS_HOST,
    cancelHost = BuildConfig.CREATE_ACCOUNT_CANCEL_HOST,
)
```

with these arguments :

- `resultLauncher` : Send back the result, it's an `ActivityResultLauncher`
- `createAccountUrl` : The url of the account creation page
- `successHost` : The host name when the account creation was successful
- `cancelHost` : The host name to connect, cancel will be invoke

**Create your `resultLauncher` to handle the `activity results`**

```kotlin
private val createAccountResultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
    if (result.resultCode == RESULT_OK) {
        val translatedError = result.data?.getStringExtra(InfomaniakLogin.ERROR_TRANSLATED_TAG)
        when {
            translatedError.isNullOrBlank() -> infomaniakLogin.startWebViewLogin(webViewLoginResultLauncher, false)
            else -> showError(translatedError)
        }
    }
}
```

**Finally, you need to add `WebViewLoginActivity` to the manifest**

```xml

<activity android:name="com.infomaniak.lib.login.WebViewCreateAccountActivity" />
```

## License

    Copyright 2021 Infomaniak
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
