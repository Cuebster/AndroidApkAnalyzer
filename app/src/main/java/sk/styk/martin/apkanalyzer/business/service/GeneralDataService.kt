package sk.styk.martin.apkanalyzer.business.service

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import sk.styk.martin.apkanalyzer.model.detail.AppSource
import sk.styk.martin.apkanalyzer.model.detail.GeneralData
import sk.styk.martin.apkanalyzer.util.AndroidVersionHelper
import sk.styk.martin.apkanalyzer.util.InstallLocationHelper
import java.io.File

/**
 * @author Martin Styk
 * @version 30.06.2017.
 */
class GeneralDataService {

    fun get(packageInfo: PackageInfo, packageManager: PackageManager): GeneralData {

        val applicationInfo = packageInfo.applicationInfo

        val isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val minSdk = AndroidManifestService.getMinSdkVersion(applicationInfo, packageManager)
        val appInstaller = findAppInstaller(packageInfo.packageName, packageManager)

        return GeneralData(
                packageName = packageInfo.packageName,
                applicationName = applicationInfo.loadLabel(packageManager)?.toString()
                        ?: applicationInfo.packageName,
                processName = applicationInfo.processName,
                versionName = packageInfo.versionName,
                versionCode = packageInfo.versionCode,
                isSystemApp = isSystemApp,
                uid = applicationInfo.uid,
                description = applicationInfo.loadDescription(packageManager)?.toString(),
                apkDirectory = applicationInfo.sourceDir,
                dataDirectory = applicationInfo.dataDir,

                source = Companion.getAppSource(packageManager, packageInfo.packageName, isSystemApp),
                appInstaller = appInstaller,

                installLocation = InstallLocationHelper.resolveInstallLocation(packageInfo.installLocation),
                apkSize = computeApkSize(applicationInfo.sourceDir),
                firstInstallTime = if (packageInfo.firstInstallTime > 0) packageInfo.firstInstallTime else null,
                lastUpdateTime = if (packageInfo.lastUpdateTime > 0) packageInfo.lastUpdateTime else null,

                minSdkVersion = minSdk,
                minSdkLabel = AndroidVersionHelper.resolveVersion(minSdk),

                targetSdkVersion = applicationInfo.targetSdkVersion,
                targetSdkLabel = AndroidVersionHelper.resolveVersion(applicationInfo.targetSdkVersion),

                icon = applicationInfo.loadIcon(packageManager)
        )
    }

    fun computeApkSize(sourceDir: String): Long = File(sourceDir).length()


    companion object {
        fun getAppSource(packageManager: PackageManager, packageName: String, isSystem: Boolean): AppSource {
            val installer = findAppInstaller(packageName, packageManager)

            return if (installer == AppSource.GOOGLE_PLAY.installerPackageName) AppSource.GOOGLE_PLAY
            else if (installer == AppSource.AMAZON_STORE.installerPackageName) AppSource.AMAZON_STORE
            else if (installer == AppSource.SYSTEM_PREINSTALED.installerPackageName || isSystem) AppSource.SYSTEM_PREINSTALED
            else AppSource.UNKNOWN
        }

        fun findAppInstaller(packageName: String, packageManager: PackageManager): String? {
            return try {
                packageManager.getInstallerPackageName(packageName)
            } catch (e: Exception) {
                null //this means package is not installed
            }
        }
    }
}
