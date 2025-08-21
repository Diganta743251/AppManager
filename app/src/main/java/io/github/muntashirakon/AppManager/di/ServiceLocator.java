// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.AppManager.di;

import android.content.Context;

import androidx.annotation.NonNull;

import io.github.muntashirakon.io.path.DefaultPathContract;
import io.github.muntashirakon.io.path.PathContract;

/**
 * Lightweight service locator for providing app-wide singletons without Hilt.
 */
public final class ServiceLocator {
    private static volatile PathContract sPathContract;

    private ServiceLocator() {}

    @NonNull
    public static PathContract getPathContract(@NonNull Context context) {
        if (sPathContract == null) {
            synchronized (ServiceLocator.class) {
                if (sPathContract == null) {
                    sPathContract = new DefaultPathContract();
                }
            }
        }
        return sPathContract;
    }
}