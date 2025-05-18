package io.github.zimoyin.asdk.accessibility

import android.view.accessibility.AccessibilityNodeInfo


/**
 * 包装一个 AccessibilityNodeInfo 集合，提供链式条件过滤能力，仿照 Auto.js 的节点选择器风格。
 *
 * @property node 待过滤的节点列表
 */
class AccessibilityNodeInfoWrapper(val node: AccessibilityNodeInfo) {
    private val conditions = mutableListOf<(AccessibilityNodeInfo) -> Boolean>()

    /**
     * 筛选文本等于 [text] 的节点。
     */
    fun text(text: String): AccessibilityNodeInfoWrapper {
        conditions += { it.text?.toString() == text }
        return this
    }

    /**
     * 筛选文本去除空格后等于 [text] 的节点。
     */
    fun textTrim(): AccessibilityNodeInfoWrapper {
        conditions += { it.text?.toString()?.trim() == it.text?.toString() }
        return this
    }

    /**
     * 筛选文本包含 [substr] 的节点。
     */
    fun textContains(substr: String): AccessibilityNodeInfoWrapper {
        conditions += { it.text?.toString()?.contains(substr) == true }
        return this
    }

    /**
     * 筛选文本匹配正则 [regex] 的节点。
     */
    fun textMatches(regex: Regex): AccessibilityNodeInfoWrapper {
        conditions += { it.text?.toString()?.matches(regex) == true }
        return this
    }

    /**
     * 筛选类名等于 [className] 的节点。
     */
    fun className(className: String): AccessibilityNodeInfoWrapper {
        conditions += { it.className.toString() == className }
        return this
    }

    /**
     * 筛选资源 ID 等于 [id] 的节点。
     */
    fun id(id: String): AccessibilityNodeInfoWrapper {
        conditions += { it.viewIdResourceName == id }
        return this
    }

    /**
     * 筛选包名等于 [packageName] 的节点。
     */
    fun pkg(packageName: String): AccessibilityNodeInfoWrapper {
        conditions += { it.packageName == packageName }
        return this
    }

    /**
     * 筛选 contentDescription 等于 [desc] 的节点。
     */
    fun description(desc: String): AccessibilityNodeInfoWrapper {
        conditions += { it.contentDescription?.toString() == desc }
        return this
    }

    /**
     * 筛选节点可点击的。
     */
    fun clickable(boolean: Boolean = true): AccessibilityNodeInfoWrapper {
        conditions += { it.isClickable == boolean}
        return this
    }

    /**
     * 筛选节点可见的（isVisibleToUser 为 true）。
     */
    fun visible(): AccessibilityNodeInfoWrapper {
        conditions += { it.isVisibleToUser }
        return this
    }

    private fun conditionResult(info: AccessibilityNodeInfo): Boolean {
        for (condition in conditions) {
            if (!condition(info)) {
                return false
            }
        }
        return true
    }

    /**
     * 执行所有累积的条件过滤，返回符合条件的节点列表。
     * 调用完成后会清空已设置的条件，以便下一次重用。
     *
     * @return 符合所有条件的节点列表
     */
    fun done(): List<AccessibilityNodeInfo> = node.filter { info ->
        conditionResult(info)
    }.also {
        conditions.clear()
    }

    fun textStartsWith(string: String): AccessibilityNodeInfoWrapper {
        conditions += { it.text?.toString()?.startsWith(string) == true }
        return this
    }
}
