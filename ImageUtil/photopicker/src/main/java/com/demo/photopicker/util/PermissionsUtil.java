package com.demo.photopicker.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class PermissionsUtil {

    public interface OnRequestPermissionListener {
        void onAllow();
        void onRefuse(boolean shouldShowRequestPermissionRationale);
    }

    public static boolean checkPermission(Fragment fragment, String permission) {
        RxPermissions rxPermissions = new RxPermissions(fragment);
        return checkPermission(rxPermissions, permission);
    }

    public static boolean checkPermission(FragmentActivity activity, String permission) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        return checkPermission(rxPermissions, permission);
    }

    public static void request(Fragment fragment, final OnRequestPermissionListener listener, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            if (fragment.getContext() != null) {
                Toast.makeText(fragment.getContext(), "缺少要申请的权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        RxPermissions rxPermissions = new RxPermissions(fragment);
        request(rxPermissions, listener, permissions);
    }

    public static void request(FragmentActivity activity, final OnRequestPermissionListener listener, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            Toast.makeText(activity, "缺少要申请的权限", Toast.LENGTH_SHORT).show();
            return;
        }

        RxPermissions rxPermissions = new RxPermissions(activity);
        request(rxPermissions, listener, permissions);
    }

    private static boolean checkPermission(RxPermissions rxPermissions, String permission) {
        return rxPermissions.isGranted(permission);
    }

    private static void request(RxPermissions rxPermissions, final OnRequestPermissionListener listener, String... permissions) {
        rxPermissions.requestEachCombined(permissions).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (listener != null) {
                    if (permission.granted) {
                        listener.onAllow();
                    }
                    else {
                        listener.onRefuse(permission.shouldShowRequestPermissionRationale);
                    }
                }
            }
        });
    }


}
