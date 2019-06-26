package com.example.supplytracker

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.room.testing.MigrationTestHelper
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.dylanhua.inventorylist.ItemDatabase
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    companion object {
        private const val TEST_DB = "test-db"
    }

    lateinit var db: SupportSQLiteDatabase

    @Rule @JvmField
    val helper: MigrationTestHelper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
        ItemDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory())

    /*
    @Test
    fun migrate1To2() {
        // Create the database with the initial version 1 schema and
        // insert an item. You cannot use DAO classes because they
        // expect the latest schema.
        db = helper.createDatabase(TEST_DB, 1)
        val values = ContentValues().apply {
            put("column_name", "Test Item 1")
            put("column_amount", 1.0)
            put("column_isFull", 0)
        }
        val insertNum = db.insert("table_item", SQLiteDatabase.CONFLICT_REPLACE, values)
        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ItemDatabase.MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        // Get the latest, migrated, version of the database.
        // Check that the correct data is in the database.
        val cursor = db.query("SELECT * FROM table_item WHERE column_id = ?", arrayOf(insertNum))
        assertEquals(cursor.moveToFirst(), true)
        assertEquals(cursor.getInt(cursor.getColumnIndex("column_id")), 1)
        assertEquals(cursor.getString(cursor.getColumnIndex("column_name")), "Test Item 1")
        assertEquals(cursor.getDouble(cursor.getColumnIndex("column_amount")), 1.0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("column_isFull")), 0)
    }

    @Test
    fun migrate2To3() {
        // Create the database with the initial version 2 schema and
        // insert an item. You cannot use DAO classes because they
        // expect the latest schema.
        db = helper.createDatabase(TEST_DB, 2)
        val values = ContentValues().apply {
            put("column_name", "Test Item 1")
            put("column_amount", 1.0)
            put("column_isFull", 0)
        }
        val insertNum = db.insert("table_item", SQLiteDatabase.CONFLICT_REPLACE, values)
        // Prepare for the next version.
        db.close()

        // Re-open the database with version 3 and provide
        // MIGRATION_2_3 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, ItemDatabase.MIGRATION_2_3)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        // Get the latest, migrated, version of the database.
        // Check that the correct data is in the database.
        val cursor = db.query("SELECT * FROM table_item WHERE column_id = ?", arrayOf(insertNum))
        assertEquals(cursor.moveToFirst(), true)
        assertEquals(cursor.getInt(cursor.getColumnIndex("column_id")), 1)
        assertEquals(cursor.getInt(cursor.getColumnIndex("column_order")), 0)
        assertEquals(cursor.getString(cursor.getColumnIndex("column_listName")), "Unsaved")
        assertEquals(cursor.getString(cursor.getColumnIndex("column_name")), "Test Item 1")
        assertEquals(cursor.getDouble(cursor.getColumnIndex("column_amount")), 1.0)
        assertEquals(cursor.getInt(cursor.getColumnIndex("column_isFull")), 0)
    }*/
}