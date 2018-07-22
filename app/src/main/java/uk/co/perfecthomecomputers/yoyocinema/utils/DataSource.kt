package uk.co.perfecthomecomputers.yoyocinema.utils

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class DataSource(context: Context) {
    private var database: SQLiteDatabase? = null
    private val dbHelper: DBHelper = DBHelper(context)

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    fun execute(query: String) {
        database!!.execSQL(query)
    }

    fun query(query: String): Cursor {
        val cursor = database!!.rawQuery(query, null)
        cursor.moveToFirst()
        return cursor
    }
}
