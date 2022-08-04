package com.mmgsoft.modules.libs

@Suppress("MemberVisibilityCanBePrivate")
object BillingAdsBuildConfig {

    val BUILD_TYPE: BuildType =
        BuildType.valueOf(BuildConfig.BUILD_TYPE.replaceFirstChar { it.uppercase() })

    val ROBO_TEST = BUILD_TYPE == BuildType.RoboTest

    enum class BuildType(val value: String) {
        Google("google"),
        Amazon("amazon"),
        RoboTest("roboTest"),
        Debug("debug"),
    }
}
