package com.lody.virtual;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.lody.virtual.client.core.InstallStrategy;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.InstallResult;

import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

/**
 * @author Lody
 */
public class GmsSupport {
    private static final HashSet<String> GOOGLE_APP = new HashSet<>();
    private static final HashSet<String> GOOGLE_SERVICE = new HashSet<>();
    public static final String GOOGLE_FRAMEWORK_PACKAGE = "com.google.android.gsf";

    static {
        GOOGLE_APP.add("com.android.vending");
        GOOGLE_APP.add("com.google.android.play.games");
        GOOGLE_APP.add("com.google.android.wearable.app");
        GOOGLE_APP.add("com.google.android.wearable.app.cn");
        GOOGLE_SERVICE.add(GOOGLE_FRAMEWORK_PACKAGE);
        GOOGLE_SERVICE.add("com.google.android.gms");
        GOOGLE_SERVICE.add("com.google.android.gsf.login");
        GOOGLE_SERVICE.add("com.google.android.backuptransport");
        GOOGLE_SERVICE.add("com.google.android.backup");
        GOOGLE_SERVICE.add("com.google.android.configupdater");
        GOOGLE_SERVICE.add("com.google.android.syncadapters.contacts");
        GOOGLE_SERVICE.add("com.google.android.feedback");
        GOOGLE_SERVICE.add("com.google.android.onetimeinitializer");
        GOOGLE_SERVICE.add("com.google.android.partnersetup");
        GOOGLE_SERVICE.add("com.google.android.setupwizard");
        GOOGLE_SERVICE.add("com.google.android.syncadapters.calendar");
    }

    public static boolean isGoogleFrameworkInstalled() {
        return VirtualCore.get().isAppInstalled("com.google.android.gms");
    }

    public static boolean isGoogleService(String packageName) {
        return GOOGLE_SERVICE.contains(packageName);
    }

    public static boolean isGoogleAppOrService(String str) {
        return GOOGLE_APP.contains(str) || GOOGLE_SERVICE.contains(str);
    }

    public static boolean isOutsideGoogleFrameworkExist() {
        return VirtualCore.get().isOutsideInstalled("com.google.android.gms");
    }

    private static void installPackages(Set<String> list, int userId) {
        VirtualCore core = VirtualCore.get();
        for (String packageName : list) {
            if (core.isAppInstalledAsUser(userId, packageName)) {
                continue;
            }
            ApplicationInfo info = null;
            try {
                info = VirtualCore.get().getUnHookPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                // Ignore
                continue;
            }
            if (info.sourceDir == null || !hasDex(info.sourceDir)) {
                VLog.w("GmsSupport", "Error when find dex for path: " + info.sourceDir);
                continue;
            }
            if (userId == 0) {
                InstallResult result =  core.installPackage(info.sourceDir, InstallStrategy.NOT_COPY_APK);
                if(result.isSuccess){
                    VLog.i("GmsSupport", "install ok:"+info.packageName);
                }else{
                    VLog.i("GmsSupport", "install fail:"+info.packageName+",error="+result.error);
                }
            } else {
                core.installPackageAsUser(userId, packageName);
            }
        }
    }

    public static void installGApps(int userId) {
        installPackages(GOOGLE_SERVICE, userId);
        installPackages(GOOGLE_APP, userId);
        if (!VirtualCore.get().isAppInstalled(GOOGLE_FRAMEWORK_PACKAGE)) {
            remove(GOOGLE_FRAMEWORK_PACKAGE);
        }
    }

    public static void remove(String packageName) {
        GOOGLE_SERVICE.remove(packageName);
        GOOGLE_APP.remove(packageName);
    }

    public static boolean isInstalledGoogleService() {
        return VirtualCore.get().isAppInstalled("com.google.android.gms");
    }

    public static boolean hasDex(String apkPath) {
        boolean hasDex = false;
        if (apkPath != null) {
            if (!apkPath.contains("/system/app") && !apkPath.startsWith("/system/priv-app")) {
                return true;
            }

            try {
                ZipFile zipfile = new ZipFile(apkPath);
                if (zipfile.getEntry("classes.dex") != null) {
                    hasDex = true;
                }
                zipfile.close();
            } catch (Throwable e) {
               //ignore
            }
        }
        return hasDex;
    }
}