package sk.styk.martin.apkanalyzer.business.task

import android.content.Context
import android.content.pm.PackageManager

import sk.styk.martin.apkanalyzer.business.service.AndroidManifestService

/**
 * Loader async task for loading android manifest content
 *
 * @author Martin Styk
 * @version 15.09.2017.
 */
class AndroidManifestLoader(context: Context, private val packageName: String) : ApkAnalyzerAbstractAsyncLoader<String>(context) {

    private val packageManager: PackageManager = context.packageManager

    override fun loadInBackground(): String {
        return AndroidManifestService(packageManager, packageName).loadAndroidManifest()
    }

    companion object {
        const val ID = 4
    }
}

