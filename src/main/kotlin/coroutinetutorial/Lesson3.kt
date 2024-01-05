package coroutinetutorial

import kotlinx.coroutines.*

//Job, Join, Cancellation and Timeouts
/**
 *  Job giữ nhiệm vụ nắm giữ thông tin về lifecycle của coroutine, cancel coroutine, .... Mỗi khi chúng ta launch một coroutine thì nó trả về một đối tượng Job
 */


/**
 * Hàm join() - hãy đợi coroutine chạy xong đã!
 * Chúng ta có thể sử dụng đối tượng Job để thực hiện một số method có sẵn trong mỗi coroutine. Ví dụ ở đây mình sử dụng hàm join().
 * Khi một coroutine gọi hàm join() này thì tiến trình phải đợi coroutine này chạy xong task của mình rồi mới chạy tiếp. Ví dụ:
 *
 * Nhìn output ta có thể dễ dàng thấy khi tiến trình chạy xong dòng code in ra từ "Hello," thì nó gặp lệnh join()
 * và nó không tiếp tục chạy xuống dòng code bên dưới để in tiếp từ "Kotlin" mà chờ coroutine chạy xong task để in ra từ "World" trước cái đã.
 * Đó là công dụng của hàm join()
 */
fun joinMethod() = runBlocking {
    val job = GlobalScope.launch { // launch a new coroutine and keep a reference to its Job
        delay(5000L)
        println("World!")
    }
    println("Hello,")
    job.join() // wait until child coroutine completes
    println("Kotlin")
}


/**
 * Để dừng và hủy bỏ một coroutine đang chạy. Ta có thể dùng method cancel() của biến Job
 *
 * Ở đoạn code trên, mình cho phóng một coroutine và bảo nó in ra câu "I'm sleeping ..." cứ mỗi 500 ms và in đủ 1000 lần như vậy.
 * Và đoạn code dưới, mình cho tiến trình delay 1300 ms trước khi cancel con coroutine mình đã phóng.
 * Kết quả là sau 1300 ms, nó mới chỉ in được có 3 câu "I'm sleeping ..." mà nó đã bị hủy bỏ nên không in tiếp được nữa
 */
fun cancelMethod() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    println("main: Now I can quit.")
}


/**
 * Những lưu ý khi hủy bỏ một coroutine
 *  mình cũng cho phóng một coroutine và bảo nó in ra câu "I'm sleeping ..." cứ mỗi 500 ms và in đủ 5 lần như vậy. Tuy nhiên sau 1300 ms, mình đã gọi hàm cancel()
 *  để hủy bỏ corotine đó, tức là nó chỉ có đủ thời gian để in ra được 3 câu "I'm sleeping ..." nhưng thực tế output
 *  cho thấy nó vẫn chạy bất chấp và in ra đủ 5 câu "I'm sleeping ..." =))

Đó là vì quá trình hủy bỏ coroutine có tính hợp tác (Coroutine cancellation is cooperative).
Một coroutine khi bị cancel thì nó sẽ chỉ set lại một property có tên là isActive trong đối tượng
Job từ true thành false (job.isActive = false), còn tiến trình của nó đang chạy thì sẽ vẫn chạy bất chấp cho đến hết mà không bị dừng lại.
Vậy tại sao, ở đoạn code trong phần 2, tiến trình của coroutine lại được hủy bỏ thành công. Đó là vì hàm delay(500L) ngoài chức năng delay
thì bản thân nó cũng có một chức năng có thể check coroutine này còn sống hay không, nếu không còn sống (job.isActive == false) nó sẽ hủy bỏ
tiến trình của coroutine đó ngay và luôn. Không chỉ riêng hàm delay() mà tất cả các hàm suspend function trong package kotlinx.coroutines đều có khả năng check này.

Vậy chúng ta đã biết thêm một property tuyệt vời của đối tượng Job là isActive. Nó giúp chúng ta kiểm tra xem coroutine đã bị cancel hay chưa.
Thử áp dụng nó vào code để kịp thời ngăn chặn tiến trình của coroutine khi đã có lệnh hủy bỏ coroutine đó xem nào
 */
fun cancelIsCooperative() = runBlocking {

    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (i < 5) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    println("main: Now I can quit.")


}

fun main() {
//    joinMethod()
//    cancelMethod()
//    cancelIsCooperative()
    cancelCheckCooperative()
}

/**
 * Nếu như không có biến isActive thì vòng lặp while sẽ làm cho coroutine in ra vô số câu "I'm sleeping ...". Nhờ có điều kiện isActive nên chúng
 * ta đã ngăn chặn được coroutine sau khi nó đã bị hủy bỏ, khiến nó chỉ có thể in ra 3 câu "I'm sleeping ..."
 */
fun cancelCheckCooperative() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
        while (isActive) {   // Điều kiện i < 5 đã được thay bằng isActive để ngăn chặn coroutine khi nó đã bị hủy
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    println("main: Now I can quit.")
}
