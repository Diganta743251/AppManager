// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.io.path

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File

private const val PREFS_NAME = "io_path_contract"
private const val KEY_EXPORTS_TREE_URI = "exports_tree_uri"

class DefaultPathContract : PathContract {
    override fun appBackupsDir(context: Context): File =
        File(context.getExternalFilesDir(null), "backups").apply { mkdirs() }

    override fun exportsTree(context: Context): DocumentFile? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uri = prefs.getString(KEY_EXPORTS_TREE_URI, null) ?: return null
        return try {
            DocumentFile.fromTreeUri(context, Uri.parse(uri))
        } catch (_: Throwable) {
            null
        }
    }

    override fun setExportsTreeUri(context: Context, uri: Uri?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            if (uri == null) remove(KEY_EXPORTS_TREE_URI) else putString(KEY_EXPORTS_TREE_URI, uri.toString())
        }.apply()
    }
}