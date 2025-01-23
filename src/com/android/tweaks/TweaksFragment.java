/* * Copyright (C) 2023 the risingOS Android Project * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
package com.android.tweaks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

public class TweaksFragment extends DashboardFragment {
    public static final String CATEGORY_KEY = "com.android.settings.category.ia.tweaks";
    private static final String LOG_TAG = "Tweaks";
    
    // Preference key for design switch
    private static final String PREF_DESIGN_SWITCH = "design_switch";
    
    // XML resource IDs
    private static final int XML_TWEAKS = R.xml.tweaks;
    private static final int XML_TWEAKS_V2 = R.xml.tweaks_v2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        
        // Find the design switch preference
        SwitchPreferenceCompat designSwitch = findPreference(PREF_DESIGN_SWITCH);
        if (designSwitch != null) {
            // Set up listener for design switch
            designSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isAlternativeDesign = (boolean) newValue;
                
                // Save the preference
                SharedPreferences prefs = requireContext().getSharedPreferences("TweaksPrefs", Context.MODE_PRIVATE);
                prefs.edit().putBoolean(PREF_DESIGN_SWITCH, isAlternativeDesign).apply();
                
                // Recreate the activity to apply changes
                requireActivity().recreate();
                
                return true;
            });
        }
    }

    @Override
    protected int getPreferenceScreenResId() {
        // Dynamically select XML based on SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("TweaksPrefs", Context.MODE_PRIVATE);
        boolean isAlternativeDesign = prefs.getBoolean(PREF_DESIGN_SWITCH, false);
        return isAlternativeDesign ? XML_TWEAKS_V2 : XML_TWEAKS;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VIEW_UNKNOWN;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_about;
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this /* fragment */, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, TweaksFragment fragment, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new TweaksController(context));
        return controllers;
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        RecyclerView rcv = super.onCreateRecyclerView(inflater, container, icicle);
        GridLayoutManager layoutG = new GridLayoutManager(getActivity(), 2);
        layoutG.setSpanSizeLookup(new SpanSizeLookupG());
        rcv.setLayoutManager(layoutG);
        return rcv;
    }

    class SpanSizeLookupG extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            if (position == 0 || position == 1 || position == 6) {
                return 2;
            } else {
                return 1;
            }
        }
    }

    /** * For Search. */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.tweaks) {
        @Override
        public List<AbstractPreferenceController> createPreferenceControllers(
                Context context) {
            return buildPreferenceControllers(context, null /* fragment */, null /* lifecycle */);
        }
    };
}
