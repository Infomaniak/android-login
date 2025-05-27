/*
 * Infomaniak Login - Android
 * Copyright (C) 2025 Infomaniak Network SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.infomaniak.lib.login.crossapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import splitties.init.appCtx
import java.security.MessageDigest

class CrossAppLoginImpl : CrossAppLogin {

    override val accounts: Flow<List<CrossAppLogin.ExternalAccount>> = flow {
        TODO("Parallelize things, with error handling, and possibly auto-retries")

        appCtx.packageManager.getPackageInfo("", 0).also {
            it.firstInstallTime
            it.lastUpdateTime
        }
    }

    @RequiresApi(28)
    private fun forApi28(targetPackageName: String) {
        val pkgInfo = appCtx.packageManager.getPackageInfo(targetPackageName, PackageManager.GET_SIGNING_CERTIFICATES)
        pkgInfo.signingInfo?.let {
            it.apkContentsSigners
            it.publicKeys
            it.schemeVersion
            it.signingCertificateHistory
            it.hasMultipleSigners()
            it.hasPastSigningCertificates()
        }
    }

    @Suppress("Deprecation")
    private fun forPreApi28(targetPackageName: String) {
        val pkgInfo = appCtx.packageManager.getPackageInfo(targetPackageName, PackageManager.GET_SIGNATURES)
        pkgInfo.signatures?.forEach {
            String(MessageDigest.getInstance("SHA-256").digest(it.toByteArray()))

        }
    }
}
