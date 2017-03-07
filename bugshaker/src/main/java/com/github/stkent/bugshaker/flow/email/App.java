package com.github.stkent.bugshaker.flow.email;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

public final class App {

    @NonNull
    private final String name;

    @NonNull
    private final String versionName;

    private final int versionCode;

    @NonNull
    private final InstallSource installSource;

    public App(@NonNull final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final ApplicationInfo applicationInfo = context.getApplicationInfo();
        final PackageInfo packageInfo = getPackageInfo(context);
        final String installerPackageName = packageManager.getInstallerPackageName(context.getPackageName());

        name = applicationInfo.loadLabel(packageManager).toString();
        versionName = packageInfo.versionName;
        versionCode = packageInfo.versionCode;
        installSource = InstallSource.fromInstallerPackageName(installerPackageName);
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getVersionName() {
        return versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    @NonNull
    public InstallSource getInstallSource() {
        return installSource;
    }

    @NonNull
    private PackageInfo getPackageInfo(@NonNull final Context context) {
        final PackageManager packageManager = context.getPackageManager();

        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException ignored) {
            //noinspection ConstantConditions: packageInfo should always be available for the embedding app.
            return null;
        }
    }

}
