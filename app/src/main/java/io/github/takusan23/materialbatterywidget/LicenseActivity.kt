package io.github.takusan23.materialbatterywidget

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import io.github.takusan23.materialbatterywidget.databinding.ActivityLicenseBinding

/**
 * ライセンス画面
 * */
class LicenseActivity : AppCompatActivity() {

    private val MATERIAL_LICENSE = """
           --- material-components/material-components-android ---
           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at

               http://www.apache.org/licenses/LICENSE-2.0

           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.
    """.trimIndent()

    private val MATERIAL_ICONS = """
        --- Templarian/MaterialDesign ---
        # Icons: Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
        Some of the icons are redistributed under the Apache 2.0 license. All other
        icons are either redistributed under their respective licenses or are
        distributed under the Apache 2.0 license.
    """.trimIndent()

    private val viewBinding by lazy { ActivityLicenseBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.activityLicenseTextview.text = listOf(MATERIAL_LICENSE, MATERIAL_ICONS).joinToString(separator = "\n\n")

        // EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { root, insets ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.updatePadding(
                left = systemInsets.left,
                top = systemInsets.top,
                right = systemInsets.right,
                bottom = systemInsets.bottom
            )
            insets
        }
    }
}