package com.example.supplytracker

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask

/**
 * This class represents a Room database that is a database
 * layer on top of an SQLite database. Room uses the DAO to
 * issue queries to its database. When Room queries return
 * data, the queries are automatically run asynchronously
 * on a background thread. Room provides compile-time checks
 * of SQLite statements.
 *
 * Any changes to the database schema requires incrementing
 * the version number and migrating the old database to the
 * latest version.
 */
@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao() : ItemDao

    /**
     * Make the Room database a singleton to prevent having
     * multiple instances of the database opened at the same
     * time.
     */
    companion object {
        @Volatile
        private var INSTANCE : ItemDatabase? = null

        /**
         * Gets a database instance. Room's database builder creates
         * a RoomDatabase object in the application context from the
         * ItemDatabase class and names it "item_database".
         *
         * @param   context     application context
         * @return              RoomDatabase instance
         */
        fun getDatabase(context : Context) : ItemDatabase? {
            if(INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        ItemDatabase::class.java,
                        "item_database.db"
                    ) .fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return INSTANCE
        }

        /**
         * Starts with the table in the given database that already has some items in it so that
         * these items can be seen later in the list display before those items are edited.
         */
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                AsyncTaskPopulateDatabase(INSTANCE).execute()
            }
        }

        /**
         * Load all items from the given database in the background thread.
         */
        private class AsyncTaskPopulateDatabase(database: ItemDatabase?) : AsyncTask<Unit, Unit, Unit>() {
            private val itemDao = database?.itemDao()

            override fun doInBackground(vararg p0: Unit?): Unit? {
                itemDao?.getAllItems()
                return null
            }
        }

    }
}