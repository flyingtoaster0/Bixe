-dontobfuscate
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

# Need variable optimization for dex; parameter optimization for retrofit; returnvalue for humanify
-optimizations !code/allocation/variable,!method/removal/parameter,!method/propagation/returnvalue

# Dagger
-dontwarn dagger.internal.codegen.**
-keepclassmembers class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class dagger.* { *; }
-keep class javax.inject.* { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection
# prevent obfuscation on files dagger needs

#Butter knife

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# okhttp
-dontwarn com.squareup.okhttp.**

# retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }

-dontwarn rx.internal.util.unsafe.**
-dontwarn okio.**

# MenuBuilder
-keep class !android.support.v7.internal.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }