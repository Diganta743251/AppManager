// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.AppManager.rules;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.github.muntashirakon.AppManager.rules.compontents.ComponentUtils;
import io.github.muntashirakon.AppManager.rules.compontents.ComponentsBlocker;
import io.github.muntashirakon.AppManager.utils.ContextUtils;

/**
 * Export rules to external directory either for a single package or multiple packages.
 *
 * @see RulesImporter
 */
public class RulesExporter {
    @NonNull
    private final Context mContext;
    @Nullable
    private List<String> mPackagesToExport;
    @NonNull
    private final List<RuleType> mTypesToExport;
    @NonNull
    private final int[] mUserIds;

    public RulesExporter(@NonNull List<RuleType> typesToExport, @Nullable List<String> packagesToExport,
                         @NonNull int[] userIds) {
        mContext = ContextUtils.getContext();
        mPackagesToExport = packagesToExport;
        mTypesToExport = typesToExport;
        mUserIds = userIds;
    }

    public void saveRules(Uri uri) throws IOException {
        if (mPackagesToExport == null) mPackagesToExport = ComponentUtils.getAllPackagesWithRules(mContext);
        try (OutputStream outputStream = mContext.getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) throw new IOException("Content provider has crashed.");
            for (String packageName: mPackagesToExport) {
                for (int userHandle : mUserIds) {
                    // Get a read-only instance
                    try (ComponentsBlocker cb = ComponentsBlocker.getInstance(packageName, userHandle)) {
                        ComponentUtils.storeRules(outputStream, cb.getAll(mTypesToExport), true);
                    }
                }
            }
        }
    }

    /**
     * Write rules to a file path chosen by contract routing.
     * If flag is ON and SAF tree set, writes there; otherwise falls back to legacy dir.
     */
    public void saveRulesAutoRouted(@NonNull String fileName) throws IOException {
        java.io.File target;
        if (io.github.muntashirakon.AppManager.settings.Prefs.BackupRestore.usePathContract()) {
            androidx.documentfile.provider.DocumentFile tree = io.github.muntashirakon.AppManager.di.ServiceLocator
                    .getPathContract(mContext)
                    .exportsTree(mContext);
            if (tree != null) {
                // Create/replace child in SAF tree
                androidx.documentfile.provider.DocumentFile child = tree.findFile(fileName);
                if (child != null) child.delete();
                child = tree.createFile("text/tab-separated-values", fileName);
                if (child == null) throw new IOException("Could not create export file in SAF tree.");
                Uri dest = child.getUri();
                try (OutputStream outputStream = mContext.getContentResolver().openOutputStream(dest)) {
                    if (mPackagesToExport == null) mPackagesToExport = ComponentUtils.getAllPackagesWithRules(mContext);
                    if (outputStream == null) throw new IOException("Content provider has crashed.");
                    for (String packageName: mPackagesToExport) {
                        for (int userHandle : mUserIds) {
                            try (ComponentsBlocker cb = ComponentsBlocker.getInstance(packageName, userHandle)) {
                                ComponentUtils.storeRules(outputStream, cb.getAll(mTypesToExport), true);
                            }
                        }
                    }
                }
                return;
            } else {
                io.github.muntashirakon.AppManager.utils.UIUtils.displayShortToast(io.github.muntashirakon.AppManager.R.string.pref_exports_tree_unset_warning);
            }
        }
        // Legacy fallback
        io.github.muntashirakon.io.Path base = io.github.muntashirakon.AppManager.settings.Prefs.Storage.getAppManagerDirectory();
        io.github.muntashirakon.io.Path file = base.createNewFile(fileName, null);
        try (OutputStream outputStream = file.openOutputStream()) {
            if (mPackagesToExport == null) mPackagesToExport = ComponentUtils.getAllPackagesWithRules(mContext);
            for (String packageName: mPackagesToExport) {
                for (int userHandle : mUserIds) {
                    try (ComponentsBlocker cb = ComponentsBlocker.getInstance(packageName, userHandle)) {
                        ComponentUtils.storeRules(outputStream, cb.getAll(mTypesToExport), true);
                    }
                }
            }
        }
    }
}
