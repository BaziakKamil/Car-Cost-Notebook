# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Application classes that will be serialized/deserialized over Gson
-keep class pl.kamilbaziak.carcostnotebook.model.** { *; }

# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations

# Fix for java.lang.NoClassDefFoundError: Failed resolution of: Landroid/window/OnBackInvokedCallback;
# We must be very aggressive here to prevent R8 from breaking the compatibility layer.
-dontwarn android.window.OnBackInvokedCallback
-dontwarn android.window.OnBackInvokedDispatcher
-dontwarn android.window.BackEvent
-dontwarn android.window.OnBackAnimationCallback

-keep class android.window.OnBackInvokedCallback { *; }
-keep class android.window.OnBackInvokedDispatcher { *; }
-keep class android.window.BackEvent { *; }
-keep class android.window.OnBackAnimationCallback { *; }

# Additionally, keep the activity methods that might trigger this
-keepclassmembers class androidx.activity.ComponentActivity {
    *** onBackPressedDispatcher;
}