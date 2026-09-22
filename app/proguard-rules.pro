# Room specific rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>(...);
}

# Keep the PEPSchedule enum and its members
-keep enum com.adgh.rabiesreminder.PEPSchedule { *; }

# Keep the MgRecord entity and its fields
-keep class com.adgh.rabiesreminder.MgRecord { *; }

# Keep the TypeConverters and their methods
-keep class com.adgh.rabiesreminder.Converters { *; }

# Keep the Dao and its methods
-keep interface com.adgh.rabiesreminder.HistoryDao { *; }

# General Room rules to prevent issues with obfuscation
-keep class androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keep @androidx.room.Database abstract class *
-keep @androidx.room.TypeConverters class *
