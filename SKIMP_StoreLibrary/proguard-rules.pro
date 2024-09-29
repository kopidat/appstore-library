# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 난독화를 진행하지 않는다.
-keep class skimp.store.lib.member.SKIMP_Store_M_Lib { *; }
-keep class skimp.store.lib.partner.SKIMP_Store_P_Lib { *; }
-keep class skimp.store.lib.LibHelper$OnResultListener { *; }
-keep class skimp.store.lib.LibHelper$Result { *; }
-keep class skimp.store.lib.LibHelper$TBL_LOGIN_USER { *; }
-keep class skimp.store.lib.LibHelper$LoginUserInfo { *; }
-keep class skimp.store.lib.net.data.** { *; }