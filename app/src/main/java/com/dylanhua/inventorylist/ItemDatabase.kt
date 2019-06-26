package com.dylanhua.inventorylist

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
 * latest version. The new version can be tested with the
 * MigrationTest class in androidTest folder.
 */
@Database(entities = [Item::class], version = 1, exportSchema = true)
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

        /*
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    "CREATE TABLE table_item_new (" +
                            "column_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                            "column_name TEXT NOT NULL, " +
                            "column_amount REAL NOT NULL, " +
                            "column_isFull INTEGER NOT NULL)"
                )

                // Copy data from old table to new table
                database.execSQL(
                    "INSERT INTO table_item_new (column_name, column_amount, column_isFull) " +
                            "SELECT column_name, column_amount, column_isFull FROM table_item"
                )

                // Remove the old table
                database.execSQL("DROP TABLE table_item")

                // Change the table name to the correct one
                database.execSQL("ALTER TABLE table_item_new RENAME TO table_item")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL("ALTER TABLE table_item ADD COLUMN column_order INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE table_item ADD COLUMN column_listName TEXT NOT NULL DEFAULT 'Unsaved'")

                val cursor = database.query("SELECT * FROM table_item")
                var i = 0
                while(cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex("column_id"))
                    val cv = ContentValues().apply {
                        put("column_order", i)
                        put("column_listName", "Unsaved")
                    }
                    i++
                    database.update("table_item", CONFLICT_REPLACE, cv, "column_id = $id", null)
                }
            }
        }*/

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
                    ).fallbackToDestructiveMigration()
                        .addCallback(roomCallback)
                        //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
            /**
             * Fills the given database instance with items.
             *
             * @param   db  given database
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                AsyncTaskPopulateDatabase(INSTANCE)
                    .execute()
            }
        }

        /**
         * Load all items from the given database in the background thread.
         */
        private class AsyncTaskPopulateDatabase(database: ItemDatabase?) : AsyncTask<Unit, Unit, Unit>() {
            private val itemDao = database?.itemDao()

            /**
             * Loads all the items from the first list if the first list exists;
             * otherwise, loads unsaved items.
             */
            override fun doInBackground(vararg p0: Unit?): Unit? {
                val savedListNames = itemDao?.getAllSavedListNames()!!
                if(savedListNames.isNotEmpty()) {
                    itemDao.getAllItems(savedListNames[0])
                } else {
                    itemDao.getAllItems(ItemListDisplay.listName)
                }
                return null
            }
        }

    }
}