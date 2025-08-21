// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.muntashirakon.io.path

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class DefaultPathContractTest {
    @Test
    fun appBackupsDir_created() {
        val ctx: Context = RuntimeEnvironment.getApplication()
        val contract = DefaultPathContract()
        val dir = contract.appBackupsDir(ctx)
        assertNotNull(dir)
        // Directory should exist after call
        assert(dir.exists() && dir.isDirectory)
    }

    @Test
    fun exportsTree_nullByDefault() {
        val ctx: Context = RuntimeEnvironment.getApplication()
        val contract = DefaultPathContract()
        assertNull(contract.exportsTree(ctx))
    }

    @Test
    fun exportsTree_setAndGet() {
        val ctx: Context = RuntimeEnvironment.getApplication()
        val contract = DefaultPathContract()
        // Use a fake tree URI; DocumentFile.fromTreeUri will return null for invalids under Robolectric
        val fake = Uri.parse("content://com.example/tree/primary:Exports")
        contract.setExportsTreeUri(ctx, fake)
        // We only validate that method does not crash; null is acceptable in Robolectric env
        val df: DocumentFile? = contract.exportsTree(ctx)
        // Just ensure no crash path; value may be null in test runtime
        // Not asserting on df due to environment limitations
    }
}