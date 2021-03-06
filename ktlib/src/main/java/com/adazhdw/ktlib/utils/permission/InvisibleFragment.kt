package com.adazhdw.ktlib.utils.permission

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment


/**
 * author: daguozhu
 * created on: 2019/11/4 19:43
 * description:
 */

typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment : Fragment() {
    private var callback: PermissionCallback? = null

    fun requestNow(cb: PermissionCallback, permissions: Array<out String>) {
        callback = cb
        requestPermissions(permissions, 1024)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1024) {
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted, deniedList) }
        }
    }
}