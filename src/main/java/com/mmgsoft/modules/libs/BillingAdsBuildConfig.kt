package com.mmgsoft.modules.libs

@Suppress("MemberVisibilityCanBePrivate")
object BillingAdsBuildConfig {

    val FLAVOR: BuildType =
        BuildType.valueOf(BuildConfig.FLAVOR.replaceFirstChar { it.uppercase() })

    val ROBO_TEST = FLAVOR == BuildType.RoboTest

    enum class BuildType(val value: String) {
        Google("google"),
        Amazon("amazon"),
        RoboTest("roboTest"),
    }
}
