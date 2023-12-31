/*
 * Copyright (C) 2022 The Android Open Source Project
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

package com.android.providers.media.photopicker.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.DeviceConfig;

import androidx.annotation.ChecksSdkIntAtLeast;

import com.android.modules.utils.build.SdkLevel;

/**
 * Util class for whether we should show the safety protection resources.
 */
public class SafetyProtectionUtils {
    private static final String SAFETY_PROTECTION_RESOURCES_ENABLED = "safety_protection_enabled";

    /**
     * Determines whether we should show the safety protection resources.
     * We show the resources only if
     * (1) the build version is T or after and
     * (2) the feature flag safety_protection_enabled is enabled and
     * (3) the config value config_safetyProtectionEnabled is enabled/true and
     * (4) the resources exist (currently the resources only exist on GMS devices)
     */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean shouldShowSafetyProtectionResources(Context context) {
        try {
            return SdkLevel.isAtLeastT()
                    && DeviceConfig.getBoolean(DeviceConfig.NAMESPACE_PRIVACY,
                            SAFETY_PROTECTION_RESOURCES_ENABLED, false)
                    && context.getResources().getBoolean(
                            Resources.getSystem()
                                    .getIdentifier("config_safetyProtectionEnabled",
                                            "bool", "android"))
                    && context.getDrawable(android.R.drawable.ic_safety_protection) != null
                    && !context.getString(
                            android.R.string.safety_protection_display_text).isEmpty();
        } catch (Resources.NotFoundException e) {
            // We should expect the resources to not exist for non-pixel devices
            // (except for the OEMs that opt-in)
            return false;
        }
    }
}
