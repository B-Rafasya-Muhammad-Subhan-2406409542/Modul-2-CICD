## Reflection 1

### 1. Clean Code Principles
Dalam mengimplementasikan fitur "Edit Product" dan "Delete Product", saya telah menerapkan beberapa prinsip *Clean Code* untuk menjaga kualitas kode:

*  Saya menggunakan nama variabel dan fungsi yang deskriptif. Contohnya di `ProductController.java`, nama fungsi seperti `editProductPage`, `editProductPost`, dan `deleteProduct` secara jelas menggambarkan tujuannya (menampilkan halaman edit, memproses edit, dan menghapus produk), sehingga kode mudah dipahami tanpa perlu banyak komentar.
*  Saya memisahkan berdasarkan kegunaannya dengan jelas. `ProductController` hanya mengatur jalur komunikasi (HTTP request), `ProductService` menangani logika bisnis, dan `ProductRepository` fokus pada manipulasi data.
*  Di `ProductRepository.java`, saya menggunakan salah satu fitur Java, yaitu Lambda Expression untuk menghapus produk:
    ```java
    productData.removeIf(product -> product.getProductId().equals(id));
    ```
  Cara ini jauh lebih ringkas dan mudah dibaca dibandingkan menggunakan perulangan `for` loop.

### 2. Secure Coding Practices
Saya telah menerapkan beberapa praktik pemrograman aman:

*  Saya menggunakan UUID (`UUID.randomUUID()`) untuk ID produk, bukan integer berurut (1, 2, 3). Ini mencegah serangan **IDOR (Insecure Direct Object Reference)** karena ID produk menjadi sulit ditebak oleh pihak yang tidak bertanggung jawab.

### 3. Mistakes and Areas for Improvement
Setelah mengevaluasi kode awal saya, saya menemukan celah keamanan pada fitur **Delete Product** dan telah memperbaikinya.

**Kesalahan:**
Awalnya, saya membuat fitur hapus menggunakan metode `GET` (`@GetMapping`) dan tombol link sederhana (`<a>`) di HTML.

**Mengapa itu bermasalah:**
1.   Metode `GET` seharusnya hanya untuk mengambil data (read-only), bukan untuk mengubah atau menghapus data di server.
2.   Menggunakan `GET` untuk menghapus data membuat aplikasi rentan terhadap serangan **Cross-Site Request Forgery (CSRF)**. Penyerang bisa membuat link jebakan yang akan menghapus produk secara otomatis jika diklik oleh pengguna.

Saya telah memperbaiki kode tersebut dengan mengubah metodenya menjadi `POST`:
1.  **Di Controller:** Saya mengganti anotasi menjadi `@PostMapping("/delete")` agar hanya menerima permintaan POST.
2.  **Di View (HTML):** Saya mengubah tombol link (`<a>`) menjadi sebuah `<form>`:
    ```html
    <form th:action="@{/product/delete}" method="post">
        <input type="hidden" name="productId" th:value="${product.productId}" />
        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
    </form>
    ```
Perubahan ini memastikan fitur hapus berjalan sesuai standar protokol HTTP dan lebih aman dari serangan CSRF sederhana.

## Reflection 2
Setelah menulis Unit Test, saya merasa lebih percaya diri dengan kualitas kode saya. Unit test bertindak sebagai pengaman yang memastikan bahwa setiap bagian kecil (unit) dari logika aplikasi berjalan sesuai harapan, terutama untuk skenario-skenario tepi (*edge cases*) yang mungkin terlewat saat pengujian manual.

Mengenai jumlah unit test dalam satu class, tidak ada angka pasti. Jumlah tes harus cukup untuk mencakup
1.  **Positive Scenarios**
2.  **Negative Scenarios**
3.  **Edge Cases**

Untuk memastikan unit test sudah cukup, kita bisa menggunakan **Code Coverage**. Namun, 100% Code Coverage tidak menjamin kode bebas dari bug atau error.
Code coverage hanya memastikan baris kode tersebut *pernah dijalankan*, tetapi tidak menjamin kebenaran logika bisnisnya.
Code coverage mungkin tidak mendeteksi bug yang disebabkan oleh kesalahan pemahaman requirement sistem.

Jika saya membuat functional test baru untuk memverifikasi jumlah item dalam daftar produk dengan cara menyalin (*copy-paste*) prosedur setup dan variabel instance yang sama dari `CreateProductFunctionalTest.java`, menurut saya itu akan menurunkan kualitas kode.

Masalah *Clean Code* yang terjadi adalah **Code Duplication**. Ini melanggar prinsip **DRY (Don't Repeat Yourself)**.

Saya menduplikasi konfigurasi setup seperti `@LocalServerPort`, `@Value base url`, dan method `@BeforeEach setup` ke dalam class baru. Jika di masa depan saya perlu mengubah logika setup (misalnya mengganti port atau konfigurasi base URL), saya harus mengubahnya di semua file test satu per satu. Ini tidak efisien dan rentan kesalahan (*error-prone*).


Solusi yang lebih bersih adalah dengan membuat **Base Test Class** (Inheritance).
1.  Buat satu class induk (misalnya `BaseFunctionalTest`) yang berisi semua konfigurasi umum (setup port, base URL, inisialisasi driver).
2.  Class test lainnya (`CreateProductFunctionalTest`, `ProductListFunctionalTest`, dll) cukup melakukan **extends** ke class induk tersebut.
3.  Dengan begitu, kode setup hanya ditulis satu kali dan bisa digunakan kembali, membuat kode lebih rapi dan mudah di-maintain.