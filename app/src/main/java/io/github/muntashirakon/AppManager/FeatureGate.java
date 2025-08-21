// SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-or-later
package io.github.muntashirakon.AppManager;

public final class FeatureGate {
    private FeatureGate() {}

    // Enable root/hidden API features only for the OSS flavor
    public static boolean isRootEnabled() {
        return "oss".equals(BuildConfig.FLAVOR);
    }

    public static boolean isHiddenApiEnabled() {
        return "oss".equals(BuildConfig.FLAVOR);
    }
}