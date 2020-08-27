# Infomaniak Login Library

Library to simplify login process with Infomaniak oauth 2.0 protocol

## Install

In the `build.gradle` of your lib/app (not root level), add this instruction :
```
implementation 'com.github.Infomaniak:android-login:2.0'
```

## Use

In the `onCreate` method of your Activity/Fragment, instantiate the library :
```
		val loginInstance = InfomaniakLogin(
			context = this,
			clientId = CLIENT_ID,
			redirectUri = "$APP_UID:/oauth2redirect")
```

with these arguments :
- `context` : The current activity/fragment context (will be used to show a Chrometab)
- `clientId` : The client ID of the app
- `redirectUri` : The redirection URL after a successful login (in order to handle the codes)

**Once instantiated, you may use this everywhere in your activity/fragment, for example in a `onClick` method :**

```
		buttonConnect.setOnClickListener {
			loginInstance.start()
		}
```

It'll create a custom chrome tab or launch the browser.
If needed (to see if an error occured), the `start()` method returns a boolean indicating the success (or not) of the operation.


**Or you can use the `loginInstance` to create a login from a webview.**

```
		buttonConnect.setOnClickListener {
			infomaniakLogin.startWebViewLogin(2)
		}
```

It'll create an activity that contains a webview. The `startWebViewLogin` takes as parameter an integer that will correspond to `requestCode` in `onActivityResult`.


```
		override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
			super.onActivityResult(requestCode, resultCode, data)
			if (requestCode == WEB_VIEW_LOGIN_REQ && resultCode == RESULT_OK) {
				val code = data?.extras?.getString(InfomaniakLogin.CODE_TAG)
				val translatedError = data?.extras?.getString(InfomaniakLogin.ERROR_TRANSLATED_TAG)
				val errorCode = data?.extras?.getString(InfomaniakLogin.ERROR_CODE_TAG)
				...
			}
```

This allows you to retrieve the answers from the webview, either the `code` for `auth` in case of success, or in case of error, we retrieve the `error code` and the `translated error message`.
