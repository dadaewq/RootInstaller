package cn.bavelee.rootinstaller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String apkPath = getSourceApkInfo();
        if (apkPath != null) {
            final File apkFile = new File(apkPath);
            String fixedPath;
            showToast("开始安装：" + apkFile.getPath());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ShellUtils.execWithRoot("pm install -r --user 0 \"" + apkPath + "\"") == 0) {
                        showToast(getApkName(apkPath) + " 已安装");
                    } else {
                        showToast(getApkName(apkPath) + " 安装失败");
                    }
                    finish();
                }
            }).start();
        } else {
            showToast("读取 APK 失败");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    public String getApkName(String apkSourcePath) {
        if (apkSourcePath == null) return null;
        PackageManager pm = getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(apkSourcePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            pkgInfo.applicationInfo.sourceDir = apkSourcePath;
            pkgInfo.applicationInfo.publicSourceDir = apkSourcePath;
            return pm.getApplicationLabel(pkgInfo.applicationInfo).toString();
        }
        return "";
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSourceApkInfo() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            return uri.getPath();
        }
        return null;
    }
}
