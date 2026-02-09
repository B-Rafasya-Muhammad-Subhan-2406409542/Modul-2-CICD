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