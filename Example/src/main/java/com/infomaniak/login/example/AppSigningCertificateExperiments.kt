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

package com.infomaniak.login.example

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT

fun Context.printInfomaniakAppCertificatesInfoForExperiments() {
    packageManager.getPackageInfo("com.infomaniak.drive", PackageManager.GET_SIGNATURES)?.signatures?.let {
        println("We have ${it.size} signatures:")
        it.forEachIndexed { index, signature ->
            val number = index + 1
            println("$number: ${signature.toCharsString()}")
        }
    }

    if (SDK_INT >= 28) packageManager.getPackageInfo("com.infomaniak.drive", PackageManager.GET_SIGNING_CERTIFICATES)?.signingInfo?.let { signingInfo ->
        if (SDK_INT >= 35) {
            println("[New API]: We have ${signingInfo.publicKeys.size} public keys:")
            signingInfo.publicKeys.forEachIndexed { index, key ->
                val number = index + 1
                println("$number: [${key.algorithm}, ${key.format}] -> ${key.encoded}")
            }
        }
        if (signingInfo.hasMultipleSigners()) {
            println("[New API]: We have ${signingInfo.apkContentsSigners.size} signatures:")
            signingInfo.apkContentsSigners.forEachIndexed { index, signature ->
                val number = index + 1
                println("$number: ${signature.toCharsString()}")
            }
        } else {
            println("[New API]: We have ${signingInfo.signingCertificateHistory.size} historical signatures:")
            if (signingInfo.hasPastSigningCertificates()) {
                println("Note: We have past signing certificates")
            }
            signingInfo.signingCertificateHistory.forEachIndexed { index, signature ->
                val number = index + 1
                println("$number: ${signature.toCharsString()}")
            }
        }
    }
    println("Done")
}
