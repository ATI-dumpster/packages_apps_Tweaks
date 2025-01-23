/*
* Copyright (C) 2023-2024 the RisingOS Android Project
* Copyright (C) 2024-2025 the EverestOS Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.tweaks

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class TweaksFragment : PreferenceFragmentCompat() {
    private lateinit var designManager: DesignManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        designManager = DesignManager(requireContext())
        
        setPreferencesFromResource(
            when (designManager.getCurrentDesign()) {
                DesignType.DESIGN_1 -> R.xml.tweaks
                DesignType.DESIGN_2 -> R.xml.tweaks_v2
            }
        )
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Programmatically set layout based on current design
        val layoutResId = when (designManager.getCurrentDesign()) {
            DesignType.DESIGN_1 -> R.layout.tweaks
            DesignType.DESIGN_2 -> R.layout.tweaks_v2
        }
        view.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val designSwitch: SwitchPreferenceCompat? = findPreference("design_switch")
        designSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            val newDesign = if (isChecked) DesignType.DESIGN_2 else DesignType.DESIGN_1
            designManager.saveDesign(newDesign)
            activity?.recreate()
            true
        }
    }
}

class DesignManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("DesignPrefs", Context.MODE_PRIVATE)

    fun saveDesign(designType: DesignType) {
        prefs.edit().putString("current_design", designType.name).apply()
    }

    fun getCurrentDesign(): DesignType {
        val savedDesign = prefs.getString("current_design", DesignType.DESIGN_1.name)
        return DesignType.valueOf(savedDesign ?: DesignType.DESIGN_1.name)
    }
}

enum class DesignType {
    DESIGN_1,
    DESIGN_2
}
