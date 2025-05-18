package com.github.zimoyin.asdk.accessibility

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AutoSdkAccessibilityService : AccessibilityService() {

    init {
        instance = this
    }

    companion object {

        val LAST_ID: String = AutoSdkAccessibilityService::class.java.name

        /**
         * 当前辅助功能服务实例
         */
        var instance: AutoSdkAccessibilityService? = null
            private set

        /**
         * 检查 AndroidManifest.xml 是否存在 android.permission.SYSTEM_ALERT_WINDOW 权限
         */
        fun hasSystemAlertWindowPermission(context: Context): Boolean {
            return isPermissionDeclared(context, Manifest.permission.SYSTEM_ALERT_WINDOW)
        }


        /**
         * 检查 AndroidManifest.xml 是否存在 android.permission.ACCESSIBILITY_SERVICE 权限
         */
        fun hasAccessibilityPermission(context: Context): Boolean {
            return isPermissionDeclared(context, "android.permission.ACCESSIBILITY_SERVICE")
        }

        /**
         * 检查 AndroidManifest.xml 是否存在 android.permission.ACCESSIBILITY_SERVICE 权限
         */
        @SuppressLint("QueryPermissionsNeeded")
        fun isAccessibilityServiceDeclared(context: Context): Boolean {
            val services = context.packageManager.queryIntentServices(
                Intent("android.accessibilityservice.AccessibilityService"),
                PackageManager.GET_META_DATA
            )

            for (serviceInfo in services) {
                if (serviceInfo.serviceInfo.packageName == context.packageName) {
                    // 检查是否声明了 BIND_ACCESSIBILITY_SERVICE 权限
                    if (serviceInfo.serviceInfo.permission != "android.permission.BIND_ACCESSIBILITY_SERVICE") {
                        return false
                    }

                    // 检查是否声明了 meta-data
                    val metaData = serviceInfo.serviceInfo.metaData
                    return !(metaData == null || !metaData.containsKey("android.accessibilityservice"))
                }
            }

            return false
        }

        /**
         * 检查是否声明了指定权限
         */
        private fun isPermissionDeclared(context: Context, permission: String): Boolean {
            return try {
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                packageInfo.requestedPermissions?.contains(permission) == true
            } catch (e: Exception) {
                false
            }
        }


        /**
         * 检查当前辅助功能服务是否已启用
         */
        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            return getAccessibilityManager(context)?.isEnabled == true
        }

        /**
         * 获取 AccessibilityManager
         */
        fun getAccessibilityManager(context: Context): AccessibilityManager? {
            return context.getSystemService(ACCESSIBILITY_SERVICE) as? AccessibilityManager
        }

        /**
         * 获取 AccessibilityServiceInfo
         */
        fun getAccessibilityServiceInfo(context: Context): AccessibilityServiceInfo? {
            val accessibilityManager = getAccessibilityManager(context) ?: return null
            val serviceInfo = accessibilityManager.installedAccessibilityServiceList.firstOrNull {
                it.id.endsWith(LAST_ID)
            }
            return serviceInfo
        }

        /**
         * 打开系统设置页面，跳转到辅助功能页面
         * @param context 上下文，可传入 Activity 或 Application
         */
        fun openAccessibilitySettings(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


    /**
     * 当系统检测到 UI 变化（如窗口更新、控件点击）时才会触发
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        AccessibilityListener.send(event)
    }

    override fun onInterrupt() {
        // TODO
    }

    /**
     * 模拟点击
     * @param x x 坐标
     * @param y y 坐标
     */
    fun clickAt(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
            lineTo(x, y)
        }

        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, 100L))
            .build()

        dispatchGesture(gestureDescription, null, null)
    }

    /**
     * 模拟点击
     * @param path 模拟点击的路径
     * @param start 模拟点击的开始时间
     * @param end 模拟点击的结束时间
     */
    fun clickAt(path:Path, start: Long, end: Long) {
        val gestureDescription = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, start, end))
            .build()

        dispatchGesture(gestureDescription, null, null)
    }
}

