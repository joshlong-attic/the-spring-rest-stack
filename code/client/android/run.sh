export ANDROID_HOME=/Users/jlong/bin/android-sdk-macosx 
mvn -Dandroid.sdk.path=$ANDROID_HOME clean install
mvn  -Dandroid.sdk.path=$ANDROID_HOME  android:emulator-start 
echo "Run mvn android:deploy when the emulator's started" 
#mvn android:deploy 
#adb logcat