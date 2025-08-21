// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.io.path

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

/**
 * Centralizes app write/read destinations and SAF trees.
 * All file I/O that targets external/shared storage must go through this contract.
 */
interface PathContract {
    /** Directory inside app-scoped external files for backups. Always exists. */
    fun appBackupsDir(context: Context): File

    /** Optional SAF-chosen export directory. Returns null if not granted. */
    fun exportsTree(context: Context): DocumentFile?

    /** Persist an exports tree URI chosen by user via SAF. */
    fun setExportsTreeUri(context: Context, uri: Uri?)
}