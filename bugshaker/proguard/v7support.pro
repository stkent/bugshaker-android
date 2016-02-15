# Make sure the AppCompat AlertDialog class is protected; if a library consumer has requested
# AppCompat dialogs, we attempt to load this class by name (reflectively) before using it:
-keep class android.support.v7.app.AlertDialog
