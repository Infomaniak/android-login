# Infomaniak Login Lib

Library to simplify login process with Infomaniak oauth 2.0 protocol

## Install

In the `build.gradle` of your lib/app (not root level), add this instruction :\
`implementation 'com.github.Infomaniak:android-login:1.4'`

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

Once instantiated, you may use this everywhere in your activity/fragment, for example in a `onClick` method :

```
		buttonConnect.setOnClickListener {
			loginInstance.start()
		}
```

It'll create a custom chrome tab or launch the browser.
If needed (to see if an error occured), the `start()` method returns a boolean indicating the success (or not) of the operation.
