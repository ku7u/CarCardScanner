package com.olequacircuits.carcardscanner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Location::class,
        Train::class,
        Car::class,
        AARCode::class,
        ScanRecord::class,
        Waybill::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun trainDao(): TrainDao
    abstract fun carDao(): CarDao
    abstract fun aarCodeDao(): AARCodeDao
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun waybillDao(): WaybillDao

}