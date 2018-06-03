package com.jntele.troy.jntelelte;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/5/21.
 */

public class PermissionUtil {
    /**
     * 是否需要检查权限
     */
    private static boolean needCheckPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static List<String> getDeniedPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return null;
        }
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            return deniedPermissions;
        }

        return null;
    }

    /**
     * 是否拥有权限
     */
    public static boolean hasPermissons(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否拒绝了再次申请权限的请求（点击了不再询问）
     */
    public static boolean deniedRequestPermissonsAgain(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return false;
        }
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        for (String permission : deniedPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //当用户之前已经请求过该权限并且拒绝了授权这个方法返回true
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 打开app详细设置界面<br/>
     * <p>
     * 在 onActivityResult() 中没有必要对 resultCode 进行判断，因为用户只能通过返回键才能回到我们的 App 中，<br/>
     * 所以 resultCode 总是为 RESULT_CANCEL，所以不能根据返回码进行判断。<br/>
     * 在 onActivityResult() 中还需要对权限进行判断，因为用户有可能没有授权就返回了！<br/>
     */
    public static void startApplicationDetailsSettings(@NonNull Activity activity, int requestCode) {
        Toast.makeText(activity, "请点击打开相关权限", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 申请权限<br/>
     * 使用onRequestPermissionsResult方法，实现回调结果或者自己普通处理
     *
     * @return 是否已经获取权限
     */
    public static boolean requestPermissions(Activity activity, int requestCode, String... permissions) {

        if (!needCheckPermission()) {
            return true;
        }
        if (!hasPermissons(activity, permissions)) {
            if (deniedRequestPermissonsAgain(activity, permissions)) {
                startApplicationDetailsSettings(activity, requestCode);
                //返回结果onActivityResult
            } else {
                List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
                if (deniedPermissions != null) {
                    ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                    //返回结果onRequestPermissionsResult
                }
            }
            return false;
        }
        return true;
    }

    /**
     * 申请权限返回方法
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults,Activity activity) {
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                denied.add(perm);
//                Log.d("TroyInfo",perm);
            }

        }
//        Log.d("TroyInfo","Len" + denied.size());
        if (denied.size() != 0) {
            startApplicationDetailsSettings(activity, requestCode);
        }
    }
}