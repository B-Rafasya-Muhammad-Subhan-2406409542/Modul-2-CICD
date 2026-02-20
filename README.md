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

## Reflection 2

### 1. Evaluasi Code Quality
Selama proses pengerjaan modul ini dan integrasi dengan SonarCloud, saya menemukan dan memperbaiki beberapa isu terkait kualitas kode (Code Quality) serta meningkatkan pengujian agar memenuhi standar *clean code*:

* Saya menemukan adanya *unused import* di dalam `ProductServiceImpl.java`. Strategi saya adalah menghapusnya karena import yang tidak digunakan akan menambah *cognitive load* bagi developer lain yang membaca kode, serta berpotensi menimbulkan kebingungan atau konflik di masa depan.
* Pada file `ProductControllerTest.java`, string `"redirect:list"` digunakan secara berulang kali. SonarCloud mendeteksinya sebagai *code smell*. Strategi saya adalah mengekstrak string tersebut menjadi sebuah konstanta `private static final String REDIRECT_LIST = "redirect:list";` sehingga kode lebih rapi dan meminimalisir kesalahan pengetikan (*typo*).
* Terdapat ketidaksesuaian penulisan antara nama file HTML (PascalCase, contoh: `CreateProduct.html`) dengan string yang dikembalikan atau diuji di Controller Test (camelCase, contoh: `createProduct`). Saya menyesuaikan unit test agar mengekspektasikan *PascalCase* yang benar, sehingga tidak terjadi *error* pada saat Spring Boot mencoba merender halaman.
* Saya menghapus karakter *backtick* ( \` ) yang terselip pada *hash integrity* Bootstrap di `CreateProduct.html` yang merusak CSS, serta memperbaiki teks *placeholder* yang salah (*copy-paste error*).

Strategi utama saya dalam memperbaiki kode adalah dengan membaca log dari SonarCloud dan GitHub Actions secara teliti, memahami akar masalahnya (apakah itu *logic error* atau *code smell*), lalu memperbaikinya secara bertahap di branch terpisah (`module-2-exercise`) sebelum melakukan *merge*.

**Peningkatan Code Coverage:**

Saya berhasil meningkatkan *code coverage* secara signifikan pada beberapa komponen utama aplikasi dengan melengkapi *unit test*. Peningkatan tersebut meliputi:
* `ProductController`: dari 9% menjadi 100%
* `ProductService`: dari 6% menjadi 100%
* `ProductRepository`: dari 70% menjadi 100%
* `EshopApplication` (main class): dari 37% menjadi 100%

### 2. Evaluasi Implementasi CI/CD
Menurut saya, *workflows* GitHub Actions yang telah saya implementasikan pada proyek ini sudah memenuhi definisi *Continuous Integration* (CI) dan *Continuous Deployment* (CD).

**Mengapa memenuhi Continuous Integration (CI)?**
Saya telah mengonfigurasi *workflow* yang secara otomatis berjalan setiap kali ada *push* atau *pull request* ke repositori. *Workflow* ini melakukan kompilasi, menjalankan *unit test* menggunakan Gradle (JaCoCo), serta melakukan analisis kualitas dan keamanan kode menggunakan SonarCloud dan OSSF Scorecard. Hal ini memastikan bahwa setiap perubahan baru selalu diuji dan divalidasi secara otomatis, sehingga *bug* dapat dideteksi sejak dini sebelum digabungkan ke *branch* utama.

**Mengapa memenuhi Continuous Deployment (CD)?**
Saya juga telah membuat file `Dockerfile` dan menyiapkan mekanisme *pull-based deployment*. Dalam skenario ideal, setiap kali ada perubahan yang di-*merge* ke branch `main`, layanan PaaS (seperti Koyeb atau Render) akan mendeteksi perubahan tersebut dan secara otomatis mem-build *image* Docker serta melakukan *deployment* ke *server production*. Ini menghapus kebutuhan untuk merilis aplikasi secara manual. Meskipun pada praktiknya saat ini saya mengalami kendala teknis terkait verifikasi *billing* (kartu kredit) dari pihak *cloud provider* yang memblokir proses *live deployment*, arsitektur dan *pipeline* CD-nya sendiri sudah terkonfigurasi dan siap berjalan.