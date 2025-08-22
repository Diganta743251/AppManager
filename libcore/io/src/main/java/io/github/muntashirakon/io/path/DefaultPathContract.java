// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.io.path;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.documentfile.provider.DocumentFile;
import java.io.File;

public class DefaultPathContract implements PathContract {
    private static final String PREFS_NAME = "io_path_contract";
    private static final String KEY_EXPORTS_TREE_URI = "exports_tree_uri";

    @Override
    public File appBackupsDir(Context context) {
        File backupsDir = new File(context.getExternalFilesDir(null), "backups");
        backupsDir.mkdirs();
        return backupsDir;
    }

    @Override
    public DocumentFile exportsTree(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String uriString = prefs.getString(KEY_EXPORTS_TREE_URI, null);
        if (uriString == null) return null;
        
        try {
            return DocumentFile.fromTreeUri(context, Uri.parse(uriString));
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public void setExportsTreeUri(Context context, Uri uri) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (uri == null) {
            editor.remove(KEY_EXPORTS_TREE_URI);
        } else {
            editor.putString(KEY_EXPORTS_TREE_URI, uri.toString());
        }
        editor.apply();
    }
}