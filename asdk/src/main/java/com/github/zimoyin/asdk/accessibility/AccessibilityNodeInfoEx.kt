package com.github.zimoyin.asdk.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.github.zimoyin.asdk.accessibility.AutoSdkAccessibilityService.Companion.instance
import com.github.zimoyin.asdk.logger
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.random.Random

object AccessibilityListener {
    private val accessibilityListener = ConcurrentHashMap<UUID, (AccessibilityEvent) -> Unit>()

    fun onAccessibilityEvent(callback: (AccessibilityEvent) -> Unit) {
        val id = UUID.randomUUID()
        accessibilityListener[id] = callback
    }

    fun removeAccessibilityEvent(id: UUID) {
        accessibilityListener.remove(id)
    }

    fun send(event: AccessibilityEvent) {
        accessibilityListener.forEach {
            runCatching { it.value.invoke(event) }.onFailure {
                logger.error(it)
            }
        }
    }
}

fun AccessibilityNodeInfo.selector(callback: AccessibilityNodeInfoWrapper.() -> Unit): List<AccessibilityNodeInfo> {
    return AccessibilityNodeInfoWrapper(this).apply { callback() }.done()
}

fun AccessibilityNodeInfo.selector(): AccessibilityNodeInfoWrapper =
    AccessibilityNodeInfoWrapper(this)

fun AccessibilityNodeInfo.forEach(callback: (AccessibilityNodeInfo) -> Unit) {
    for (i in 0 until childCount) {
        val node = getChild(i)
        callback(node)
        node.forEach(callback)
    }
}

val AccessibilityNodeInfo.rootNode: AccessibilityNodeInfo
    get() {
        var root = this
        while (root.parent != null) {
            root = root.parent
        }
        return root
    }

fun AccessibilityNodeInfo.filter(callback: (AccessibilityNodeInfo) -> Boolean): MutableList<AccessibilityNodeInfo> {
    val list = mutableListOf<AccessibilityNodeInfo>()
    forEach {
        if (callback(it)) {
            list.add(it)
        }
    }
    return list
}

val AccessibilityNodeInfo.content: String?
    get() {
        return text?.toString()
    }

val AccessibilityNodeInfo.description: String?
    get() {
        return this.contentDescription?.toString()
    }

/**
 * 点击节点范围内的任意空间
 */
fun AccessibilityNodeInfo?.click(
    service: AccessibilityService = requireNotNull(instance),
    minDuration: Long = 1L,
    maxDuration: Long = 200L
): Boolean {
    if (this == null) return false
    val bounds = Rect().apply { this@click.getBoundsInScreen(this) }
    if (bounds.isEmpty) return false

    // 在控件边界内生成随机坐标
    val randomX = Random.nextInt(bounds.left, bounds.right)
    val randomY = Random.nextInt(bounds.top, bounds.bottom)

    val path = Path().apply {
        moveTo(randomX.toFloat(), randomY.toFloat())
        lineTo(randomX.toFloat(), randomY.toFloat())
    }

    val gesture = GestureDescription.Builder()
        .addStroke(
            GestureDescription.StrokeDescription(
                path,
                0L,
                Random.nextLong(minDuration, maxDuration + 1)
            )
        )
        .build()

//    return service.dispatchGesture(gesture, null, null)
    return service.dispatchGesture(
        gesture,
        object : AccessibilityService.GestureResultCallback() {
            override fun onCancelled(gestureDescription: GestureDescription) {
                super.onCancelled(gestureDescription)
            }

            override fun onCompleted(gestureDescription: GestureDescription) {
                super.onCompleted(gestureDescription)
            }
        },
        null
    )
}

/**
 * 点击节点
 * 注意：点击节点时，如果节点不可点击，则会返回false。
 * 一般情况下在控件上面都会有图标或者文本，如果匹配到了文本或者图标，非特殊情况下是不能被点击的，因此需要获取父或者子节点进行点击
 * @return 是否点击成功
 */
fun AccessibilityNodeInfo?.clickNode(): Boolean {
    return this?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
}


/**
 * 点击节点
 * 注意：点击节点时，如果节点不可点击，则会查找父节点
 */
fun AccessibilityNodeInfo?.clickMatchNode(): Boolean {
    if (this == null) return false
    return if (isClickable) {
        performAction(AccessibilityNodeInfo.ACTION_CLICK)
    } else {
        parent.clickMatchNode()
    }
}

/**
 * 长按节点
 * 注意：长按节点时，如果节点不可点击，则会返回false。
 * 一般情况下在控件上面都会有图标或者文本，如果匹配到了文本或者图标，非特殊情况下是不能被点击的，因此需要获取父或者子节点进行点击
 */
fun AccessibilityNodeInfo?.longClickNode(): Boolean {
    return this?.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK) == true
}

