# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5          # 指定代码的压缩级别
-dontusemixedcaseclassnames   # 是否使用大小写混合
-dontpreverify           # 混淆时是否做预校验
#-verbose                # 混淆时是否记录日志
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses



-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

#-keepattributes *Annotation* #保护注解

#混淆前后的映射
-printmapping mapping.txt


# 保持哪些类不被混淆
-keep public class * extends android.app.Application
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}



#如果引用了v4或者v7包
-dontwarn android.support.**
-dontwarn com.baidu.**