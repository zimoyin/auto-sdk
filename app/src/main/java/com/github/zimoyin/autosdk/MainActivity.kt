package com.github.zimoyin.autosdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.github.zimoyin.autosdk.ui.utils.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.zimoyin.asdk.accessibility.AutoSdkAccessibilityService
import com.github.zimoyin.asdk.accessibility.click
import com.github.zimoyin.asdk.accessibility.clickNode
import com.github.zimoyin.asdk.accessibility.selector
import com.github.zimoyin.asdk.getToast
import com.github.zimoyin.asdk.logger
import com.github.zimoyin.asdk.show
import com.github.zimoyin.autosdk.ui.theme.AutoSDKTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Test(
                        this@MainActivity,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Test(context: MainActivity, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,//设置水平居中对齐
        verticalAlignment = Alignment.CenterVertically//设置垂直居中对齐
    ) {
        Column {
            Button {
                getToast(context).show(AutoSdkAccessibilityService.instance)
                val accessibilityServiceDeclared =
                    AutoSdkAccessibilityService.isAccessibilityServiceDeclared(context)
                logger.info("accessibilityServiceDeclared: $accessibilityServiceDeclared")

                val hasAccessibilityPermission =
                    AutoSdkAccessibilityService.hasAccessibilityPermission(context)
                logger.info("hasAccessibilityPermission: $hasAccessibilityPermission")

                val hasSystemAlertWindowPermission =
                    AutoSdkAccessibilityService.hasSystemAlertWindowPermission(context)
                logger.info("hasSystemAlertWindowPermission: $hasSystemAlertWindowPermission")

                val accessibilityServiceEnabled =
                    AutoSdkAccessibilityService.isAccessibilityServiceEnabled(context)
                logger.info("accessibilityServiceEnabled: $accessibilityServiceEnabled")
            }

            Button {
                AutoSdkAccessibilityService.openAccessibilitySettings(context)
            }

            Button{
                val service = AutoSdkAccessibilityService.instance?:return@Button
                service.rootInActiveWindow
                    .selector()
                    .text("Button")
                    .done()
                    .firstOrNull()
                    .click()
                    .apply {
                        logger.info("click: $this")
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AutoSDKTheme {
        Test(MainActivity())
    }
}