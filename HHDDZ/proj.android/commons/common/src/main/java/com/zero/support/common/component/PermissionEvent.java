package com.zero.support.common.component;

import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionEvent {
    private final PermissionModel model;
    private final String[] permissions;
    private final int[] grantResults;
    private final List<String> deniedPermissions = new ArrayList<>();
    private final List<String> grantPermissions = new ArrayList<>();
    private final boolean permanentlyDenied;

    public PermissionEvent(PermissionModel model, String[] permissions, int[] grantResults) {
        this.model = model;
        this.permissions = permissions;
        this.grantResults = grantResults;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantPermissions.add(permissions[i]);
            } else {
                deniedPermissions.add(permissions[i]);
            }
        }
        permanentlyDenied = PermissionHelper.hasPermissionPermanentlyDenied(model.requireViewModel().requireActivity(), model, deniedPermissions.toArray(new String[0]));
    }

    public String[] getPermissions() {
        return permissions;
    }

    public int[] getGrantResults() {
        return grantResults;
    }

    public boolean isPermanentlyDenied() {

        return permanentlyDenied;
    }

    @Override
    public String toString() {
        return "PermissionEvent{" +
                "model=" + model +
                ", permissions=" + Arrays.toString(permissions) +
                ", grantResults=" + Arrays.toString(grantResults) +
                '}';
    }

    public boolean isGranted() {
        return deniedPermissions.size() == 0;
    }
}
