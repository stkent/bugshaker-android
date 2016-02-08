# Make sure the GoogleMap class is protected; we determine whether or not to look for maps in the
# view hierarchy by attempting to load this class by name (reflectively):
-keep class com.google.android.gms.maps.GoogleMap
