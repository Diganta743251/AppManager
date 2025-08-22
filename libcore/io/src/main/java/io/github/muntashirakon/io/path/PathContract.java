// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.io.path;

import android.content.Context;
import android.net.Uri;
import androidx.documentfile.provider.DocumentFile;
import java.io.File;

/**
 * Centralizes app write/read destinations and SAF trees.
 * All file I/O that targets external/shared storage must go through this contract.
 */
public interface PathContract {
    /** Directory inside app-scoped external files for backups. Always exists. */
    File appBackupsDir(Context context);

    /** Optional SAF-chosen export directory. Returns null if not granted. */
    DocumentFile exportsTree(Context context);

    /** Persist an exports tree URI chosen by user via SAF. */
    void setExportsTreeUri(Context context, Uri uri);
}