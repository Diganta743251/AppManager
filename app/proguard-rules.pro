# Specify compression level
-optimizationpasses 5
# Algorithm for confusion
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# Allow access to and modification of classes and class members with modifiers during optimization
-allowaccessmodification
# Rename file source to "Sourcefile" string
-renamesourcefileattribute SourceFile
# Keep line number
-keepattributes SourceFile,LineNumberTable
# Keep generics
-keepattributes Signature
# Keep all class members that implement the serializable interface
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# Keep all class members that implement the percelable interface
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
    public int describeContents();
    public void writeToParcel(android.os.Parcel, int);
}
# Keep preference fragments
-keep public class * extends androidx.preference.PreferenceFragmentCompat {}
# Keep XmlPullParsers FIXME: Otherwise abstract method exception would occur
-keep public class * extends org.xmlpull.v1.XmlPullParser { *; }
-keep public class * extends org.xmlpull.v1.XmlSerializer { *; }
# Don't minify server-related classes FIXME
-keep public class io.github.muntashirakon.AppManager.servermanager.** { *; }
-keep public class io.github.muntashirakon.AppManager.server.** { *; }
-keep public class io.github.muntashirakon.AppManager.ipc.** { *; }
# Don't minify debug-sepcific resource file
-keep public class io.github.muntashirakon.AppManager.debug.R$raw {*;}
# Don't minify OpenPGP API
-keep public class org.openintents.openpgp.IOpenPgpService { *; }

# DeviceGuard Pro - Compose specific rules
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlin.coroutines.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep DeviceGuard Pro Compose Activities
-keep class com.deviceguard.pro.appmanager.settings.*ComposeActivity { *; }
-keep class com.deviceguard.pro.appmanager.main.MainComposeActivity { *; }
-keep class com.deviceguard.pro.appmanager.widget.QuickActionsWidget { *; }

# Keep Material 3 components
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep public class org.openintents.openpgp.IOpenPgpService2 { *; }
# Don't minify Spake2 library
-keep public class io.github.muntashirakon.crypto.spake2.** { *; }
# Don't minify AOSP private APIs
-keep class android.** { *; }
-keep class com.android.** { *; }
-keep class libcore.util.** { *; }
-keep class org.xmlpull.v1.** { *; }

# Play Store specific optimizations
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Kotlin metadata
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class kotlin.coroutines.** { *; }

# Missing classes for R8 - suppress warnings
-dontwarn com.google.j2objc.annotations.ReflectionSupport
-dontwarn com.google.j2objc.annotations.RetainedWith
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.FeatureDescriptor
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor

# Additional Play Store optimizations
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
