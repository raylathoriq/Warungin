# Warungin - Aplikasi Manajemen UMKM & Katalog Sembako

**Warungin** adalah aplikasi Android berbasis Kotlin yang dirancang untuk membantu pengelolaan operasional UMKM kelontong atau bahan pokok (sembako). Aplikasi ini memiliki sistem manajemen inventori, kasir pencatatan penjualan offline, serta integrasi dengan kulakan supplier online (groceries API). 

Aplikasi ini mendukung pemisahan peran secara penuh antara **Pemilik UMKM (Admin)** dan **Pembeli / Pengunjung (Guest/Visitor)**.

---

## 🚀 Fitur Utama & Pembagian Peran

### 1. Mode Pemilik UMKM (Admin)
Mendapatkan hak akses penuh untuk mengelola operasional toko:
* **Login Autentikasi**: Masuk menggunakan kredensial admin default.
* **Manajemen Katalog (CRUD)**: Menambah, mengubah, dan menghapus produk katalog dengan URL gambar yang langsung di-preview.
* **Kulakan Sembako Online (Jelajah)**: Mengimpor produk secara real-time dari API supplier sembako grosir online (menggunakan groceries API dari DummyJSON) langsung ke database lokal.
* **Manajemen Inventori**: Memperbarui jumlah stok barang secara cepat via tombol `[-]` dan `[+]` di list.
* **Pencatatan Penjualan**: Mencatat transaksi kasir penjualan offline secara otomatis, menghitung total belanja real-time, dan memotong stok di database lokal.

### 2. Mode Pembeli / Pengunjung (Guest/Visitor)
Mendapatkan hak akses terbatas (read-only) untuk berinteraksi dengan produk tanpa manipulasi data:
* **Masuk Instan**: Cukup dengan menekan tombol **"MASUK SEBAGAI PENGUNJUNG"** di layar Login.
* **Dashboard Katalog Terbatas**: Tab *Inventori* dan *Penjualan* disembunyikan. FAB tambah produk dan menu Jelajah supplier grosir juga disembunyikan demi keamanan data operasional.
* **Aksi Pengunjung (Implicit Intent)**:
  * **Chat WA**: Mengirim pesan pemesanan otomatis ke nomor WhatsApp pemilik berisi nama produk dan harganya.
  * **Lokasi Toko**: Membuka Google Maps (atau browser) untuk memetakan koordinat toko fisik.
  * **Cari Browser**: Mengetuk gambar produk untuk mencari informasi produk di Google Search browser secara otomatis.
* **Profil Pengunjung**: Nama profil berubah menjadi "Pengunjung Warungin" dengan username `@guest` dan opsi beralih kembali ke mode login admin.

---

## 🛠️ Tech Stack & Library

* **Bahasa**: Kotlin
* **Database**: SQLite murni (via `DBHelper`)
* **Library HTTP**: Volley (untuk API supplier online)
* **Library Image Loader**: Glide (untuk rendering URL foto produk)
* **UI**: XML Layouts dengan **View Binding** & Material Design 3
* **Target SDK**: compileSdk 36, targetSdk 34, minSdk 26

---

## 💻 Prasyarat & Cara Menjalankan

### Prasyarat System
1. **JDK / Java SDK**: Versi **17** atau **21** (karena menggunakan Android Gradle Plugin versi modern).
2. **Android Studio**: Android Studio (Koala / Ladybug atau versi terbaru direkomendasikan).

### Cara Menjalankan di Android Studio
1. Clone repositori ini:
   ```bash
   git clone https://github.com/raylathoriq/Warungin.git
   ```
2. Buka Android Studio, pilih **Open** dan arahkan ke direktori hasil clone.
3. Tunggu proses **Gradle Sync** hingga selesai.
4. Hubungkan emulator atau perangkat Android fisik (aktifkan USB Debugging).
5. Klik tombol **Run (Run 'app')** di Android Studio.

---

## 🔑 Kredensial Demo / Uji Coba

* **Akun Admin (Pemilik UMKM)**:
  * **Username**: `admin`
  * **Password**: `admin123`
* **Akun Pengunjung**:
  * Tekan tombol outline **`MASUK SEBAGAI PENGUNJUNG`** di halaman masuk utama.
