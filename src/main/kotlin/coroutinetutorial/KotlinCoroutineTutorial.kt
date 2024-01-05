import coroutinetutorial.runBlockThreadCoroutine

/**
 * Coroutines về cơ bản có thể hiểu nó như một "light-weight" thread, nhưng nó không phải là 1 thread, chúng chỉ hoạt động tương tự 1 thread.
 * Hàng nghìn coroutines có thể được bắt đầu cùng một lúc, còn nếu hàng nghìn thread chạy thì performance sẽ trả 1 cái giá rất đắt.
 * Tóm lại, giá phải trả cho 1 thread là rất đắt, còn coroutine thì gần như là hàng free. Quá tuyệt vời cho performance
 *
 * Như đã phân tích ở mục II, việc viết code xử lý bất đồng bộ rất là lộn xộn và khó debug. Còn với Kotlin Coroutine,
 * code được viết như thể chúng ta đang viết code đồng bộ, từ trên xuống, không cần bất kỳ cú pháp đặc biệt nào,
 * ngoài việc sử dụng một hàm gọi là launch. (Hàm này giúp khởi động coroutine).
 * Function xử lý task bất đồng bộ được viết giống y như khi ta viết function xử lý task đồng bộ.
 * Sự khác biệt duy nhất là từ khóa suspend được thêm vào trước từ khóa fun. Và chúng ta có thể return bất kỳ kiểu dữ liệu nào chúng ta muốn.
 * Điều mà Thread không làm được mà phải cần tới AsyncTask.
 *
 * Kotlin Coroutine là nền tảng độc lập. Cho dù bạn đang viết code JavaScript hay bất kỳ nền tảng nào khác, cách viết code implement Kotlin Coroutine sẽ đều giống nhau.
 * Trình biên dịch sẽ đảm nhiệm việc điều chỉnh nó cho từng nền tảng
 */
fun main(args: Array<String>) {
    //launchFirstCoroutine()
    runBlockThreadCoroutine()

}