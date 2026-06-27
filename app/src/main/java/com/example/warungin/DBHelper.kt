package com.example.warungin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "warungin.db"
        private const val DATABASE_VERSION = 4

        // Nama Tabel
        const val TABLE_USER = "user"
        const val TABLE_PRODUK = "produk"
        const val TABLE_PENJUALAN = "penjualan"

        // Kolom Tabel User
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_PASSWORD = "password"

        // Kolom Tabel Produk
        const val COL_PRODUK_ID = "id"
        const val COL_PRODUK_NAMA = "nama"
        const val COL_PRODUK_KATEGORI = "kategori"
        const val COL_PRODUK_HARGA = "harga"
        const val COL_PRODUK_STOK = "stok"
        const val COL_PRODUK_DESKRIPSI = "deskripsi"
        const val COL_PRODUK_FOTO = "foto"

        // Kolom Tabel Penjualan
        const val COL_PENJUALAN_ID = "id"
        const val COL_PENJUALAN_ID_PRODUK = "id_produk"
        const val COL_PENJUALAN_NAMA_PRODUK = "nama_produk"
        const val COL_PENJUALAN_JUMLAH = "jumlah"
        const val COL_PENJUALAN_TOTAL = "total"
        const val COL_PENJUALAN_TANGGAL = "tanggal"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Query Pembuatan Tabel User
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_USERNAME TEXT UNIQUE,
                $COL_USER_PASSWORD TEXT
            )
        """.trimIndent()

        // Query Pembuatan Tabel Produk
        val createProdukTable = """
            CREATE TABLE $TABLE_PRODUK (
                $COL_PRODUK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUK_NAMA TEXT,
                $COL_PRODUK_KATEGORI TEXT,
                $COL_PRODUK_HARGA REAL,
                $COL_PRODUK_STOK INTEGER,
                $COL_PRODUK_DESKRIPSI TEXT,
                $COL_PRODUK_FOTO TEXT
            )
        """.trimIndent()

        // Query Pembuatan Tabel Penjualan
        val createPenjualanTable = """
            CREATE TABLE $TABLE_PENJUALAN (
                $COL_PENJUALAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PENJUALAN_ID_PRODUK INTEGER,
                $COL_PENJUALAN_NAMA_PRODUK TEXT,
                $COL_PENJUALAN_JUMLAH INTEGER,
                $COL_PENJUALAN_TOTAL REAL,
                $COL_PENJUALAN_TANGGAL TEXT
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createProdukTable)
        db.execSQL(createPenjualanTable)

        // Insert Akun Default
        val userValues = ContentValues().apply {
            put(COL_USER_USERNAME, "admin")
            put(COL_USER_PASSWORD, "admin123")
        }
        db.insert(TABLE_USER, null, userValues)

        // Insert Data Produk Dummy Realistis Indonesia
        val dummyProdukList = listOf(
            ContentValues().apply {
                put(COL_PRODUK_NAMA, "Gula Pasir Lokal 1kg")
                put(COL_PRODUK_KATEGORI, "Bahan Makanan")
                put(COL_PRODUK_HARGA, 17000.0)
                put(COL_PRODUK_STOK, 15)
                put(COL_PRODUK_DESKRIPSI, "Gula pasir putih manis berkualitas untuk kebutuhan sehari-hari kemasan 1kg.")
                put(COL_PRODUK_FOTO, "gula")
            },
            ContentValues().apply {
                put(COL_PRODUK_NAMA, "Madu Hutan Murni 250ml")
                put(COL_PRODUK_KATEGORI, "Bahan Makanan")
                put(COL_PRODUK_HARGA, 95000.0)
                put(COL_PRODUK_STOK, 8)
                put(COL_PRODUK_DESKRIPSI, "Madu murni alami dipanen langsung dari hutan belantara Sumatra, tanpa pemanis buatan.")
                put(COL_PRODUK_FOTO, "madu")
            }
        )

        for (produk in dummyProdukList) {
            db.insert(TABLE_PRODUK, null, produk)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PENJUALAN")
        onCreate(db)
    }

    // Method: cekLogin()
    fun cekLogin(username: String, sandi: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COL_USER_USERNAME = ? AND $COL_USER_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, sandi))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Method: insertProduk()
    fun insertProduk(nama: String, kategori: String, harga: Double, stok: Int, deskripsi: String, foto: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PRODUK_NAMA, nama)
            put(COL_PRODUK_KATEGORI, kategori)
            put(COL_PRODUK_HARGA, harga)
            put(COL_PRODUK_STOK, stok)
            put(COL_PRODUK_DESKRIPSI, deskripsi)
            put(COL_PRODUK_FOTO, foto)
        }
        return db.insert(TABLE_PRODUK, null, values)
    }

    // Method: getAllProduk()
    fun getAllProduk(): List<Produk> {
        val list = ArrayList<Produk>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUK ORDER BY $COL_PRODUK_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUK_ID))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUK_NAMA))
                val kategori = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUK_KATEGORI))
                val harga = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUK_HARGA))
                val stok = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUK_STOK))
                val deskripsi = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUK_DESKRIPSI))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUK_FOTO))
                list.add(Produk(id, nama, kategori, harga, stok, deskripsi, foto))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Method: updateProduk()
    fun updateProduk(id: Int, nama: String, kategori: String, harga: Double, stok: Int, deskripsi: String, foto: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PRODUK_NAMA, nama)
            put(COL_PRODUK_KATEGORI, kategori)
            put(COL_PRODUK_HARGA, harga)
            put(COL_PRODUK_STOK, stok)
            put(COL_PRODUK_DESKRIPSI, deskripsi)
            put(COL_PRODUK_FOTO, foto)
        }
        return db.update(TABLE_PRODUK, values, "$COL_PRODUK_ID = ?", arrayOf(id.toString()))
    }

    // Method: deleteProduk()
    fun deleteProduk(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PRODUK, "$COL_PRODUK_ID = ?", arrayOf(id.toString()))
    }

    // Method: updateStok()
    fun updateStok(id: Int, newStok: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PRODUK_STOK, newStok)
        }
        return db.update(TABLE_PRODUK, values, "$COL_PRODUK_ID = ?", arrayOf(id.toString()))
    }

    // Method: insertPenjualan() (stok produk otomatis berkurang ditangani di pemanggil setelah insert berhasil)
    fun insertPenjualan(idProduk: Int, namaProduk: String, jumlah: Int, total: Double, tanggal: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_PENJUALAN_ID_PRODUK, idProduk)
            put(COL_PENJUALAN_NAMA_PRODUK, namaProduk)
            put(COL_PENJUALAN_JUMLAH, jumlah)
            put(COL_PENJUALAN_TOTAL, total)
            put(COL_PENJUALAN_TANGGAL, tanggal)
        }
        return db.insert(TABLE_PENJUALAN, null, values)
    }

    // Method: getRiwayat()
    fun getRiwayat(): List<Penjualan> {
        val list = ArrayList<Penjualan>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PENJUALAN ORDER BY $COL_PENJUALAN_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PENJUALAN_ID))
                val idProduk = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PENJUALAN_ID_PRODUK))
                val namaProduk = cursor.getString(cursor.getColumnIndexOrThrow(COL_PENJUALAN_NAMA_PRODUK))
                val jumlah = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PENJUALAN_JUMLAH))
                val total = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PENJUALAN_TOTAL))
                val tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_PENJUALAN_TANGGAL))
                list.add(Penjualan(id, idProduk, namaProduk, jumlah, total, tanggal))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
