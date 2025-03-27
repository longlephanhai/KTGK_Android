package com.example.ktgk_23it144

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Patterns

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "CustomerDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CUSTOMER = "Customer"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone_number"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_CUSTOMER ("+
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                +
                "$COLUMN_NAME TEXT,"
                +
                "$COLUMN_EMAIL TEXT,"
                +
                "$COLUMN_PHONE TEXT ")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMER")
        onCreate(db)
    }

    // Kiểm tra dữ liệu hợp lệ
    private fun isValidCustomer(name: String, email: String, phone: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) return false
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        if (!Patterns.PHONE.matcher(phone).matches()) return false
        return true
    }

    // Thêm khách hàng
    fun addCustomer(customer: Customer): Boolean {
        if (!isValidCustomer(customer.name, customer.email, customer.phone_number)) return false

        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, customer.name)
            put(COLUMN_EMAIL, customer.email)
            put(COLUMN_PHONE, customer.phone_number)
        }
        val result = db.insert(TABLE_CUSTOMER, null, contentValues)
        db.close()
        return result != -1L
    }

    // Lấy danh sách khách hàng
    @SuppressLint("Range")
    fun getCustomers(): List<Customer> {
        val customerList = mutableListOf<Customer>()
        val db = this.readableDatabase

        // read customers
        val cursor:Cursor=db.rawQuery("SELECT * FROM $TABLE_CUSTOMER",null)
        // add to list
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
                customerList.add(Customer(id, name, email, phone))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return customerList
    }

    // Cập nhật thông tin khách hàng
    fun updateCustomer(customer: Customer): Boolean {
        if (!isValidCustomer(customer.name, customer.email, customer.phone_number)) return false

        val db = this.writableDatabase
        // get values
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, customer.name)
            put(COLUMN_EMAIL, customer.email)
            put(COLUMN_PHONE, customer.phone_number)
        }
        val result = db.update(TABLE_CUSTOMER, contentValues, "$COLUMN_ID=?", arrayOf(customer.id.toString()))
        db.close()
        return result > 0
    }

    // Xóa khách hàng
    fun deleteCustomer(id: Int): Boolean {
        val db = this.writableDatabase
        //delete customer
        val result = db.delete(TABLE_CUSTOMER, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}