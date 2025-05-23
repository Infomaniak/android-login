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

package com.infomaniak.lib.login.ext

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.infomaniak.lib.login.databinding.ActivityWebViewLoginBinding

internal fun ActivityWebViewLoginBinding.handleEdgeToEdge() {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
        val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        appBarLayout.updatePadding(top = systemBar.top)
        view.updatePadding(left = systemBar.left, right = systemBar.right, bottom = systemBar.bottom)
        WindowInsetsCompat.CONSUMED
    }
}
