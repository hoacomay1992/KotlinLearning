package coroutinetutorial

import kotlinx.coroutines.*

//Coroutine context và Dispatcher

/**
 * Mỗi coroutine trong Kotlin đều có một context được thể hiện bằng một instance của interface CoroutineContext. Context này là một tập các element cấu hình cho coroutine
 *
 * các element trong coroutine bao gồm:
 *     - Job: nắm giữ thông tin về lifecycle của coroutine
 *     - Dispatcher: Quyết định thread nào mà coroutine sẽ chạy trên đó. Có các loại dispatcher sau:
 *         - Dispatchers.Main: chạy trên main UI thread
 *         - Dispatchers.IO: chạy trên background thread của thread pool. Thường được dùng khi Read, write files, Database, Networking
 *         - Dispatchers.Default: chạy trên background thread của thread pool. Thường được dùng khi sorting a list, parse Json, DiffUtils
 *         - newSingleThreadContext("name_thread"): chạy trên một thread do mình đặt tên
 *         - newFixedThreadPoolContext(3, "name_thread"): sử dụng 3 threads trong shared background thread pool
 * Job và Dispatcher là 2 element chính trong CoroutineContext. Ngoài ra còn một số element khác như:
 *    - CoroutineName("name"): đặt tên cho coroutine
 *    - NonCancellable: không thể cancel kể cả khi đã gọi method cancel coroutine
 *
 *  Toán thử plus (+) để thêm các element vào coroutineContext
 *  Sử dụng toán tử cộng để set nhiều loại element cho coroutine context như sau:
 *         // set context khi sử dụng runBlocking { } để start coroutine
 *           runBlocking(Dispatchers.IO + Job()) {
 *          }
 *
 *          // hoặc set context khi sử dụng launch { } để start coroutine
 *            GlobalScope.launch(newSingleThreadContext("demo_thread") + CoroutineName("demo_2") + NonCancellable) {
 *
 *            }
 *
 */


/**
 * Nếu không set coroutine context cho coroutine thì default nó sẽ nhận Dispatchers.Default làm dispatcher và tạo ra một Job() để quản lý coroutine.
 *         GlobalScope.launch {
 *          //tương đương với GlobalScope.launch (Dispatchers.Default + Job()) { }
 *        }
 */
fun defaultContext() {
    GlobalScope.launch {
        // tương đương với GlobalScope.launch (Dispatchers.Default + Job()) { }
    }
}

/**
 * Chúng ta có thể get được context coroutine thông qua property coroutineContext trong mỗi coroutine.
 * Chúng ta có thể thêm các element vào một coroutineContext bằng cách sử dụng toán tử cộng +
 */
fun getCoroutineContext() = runBlocking {
    println("A context with name: ${coroutineContext + CoroutineName("test")}")
}

/**
 * Hàm withContext: Nó là một suspend function cho phép coroutine chạy code trong block với một context cụ thể do chúng ta quy định.
 * Ví dụ chúng ta sẽ chạy đoạn code dưới và sẽ print ra context và thread để kiểm tra:
 *
 * Công dụng tuyệt vời của hàm withContext sẽ được chúng ta sử dụng hầu hết trong các dự án.
 * Cụ thể chúng ta sẽ get data dưới background thread và cần UI thread để update UI:
 */
fun withContextSwitchThread() {
    newSingleThreadContext("thread1").use { ctx1 ->
        // tạo một context là ctx1 chứ chưa launch coroutine.
        // ctx1 sẽ có 1 element là dispatcher quyết định coroutine sẽ chạy trên 1 thread tên là thread1
        println("ctx1 - ${Thread.currentThread().name}")

        newSingleThreadContext("thread2").use { ctx2 ->
            // tạo một context là ctx2 chứ vẫn chưa launch coroutine
            // ctx2 sẽ có 1 element là dispatcher quyết định coroutine sẽ chạy trên 1 thread tên là thread2
            println("ctx2 - ${Thread.currentThread().name}")

            runBlocking(ctx1) {
                // coroutine đang chạy trên context ctx1 và trên thread thread1
                println("Started in ctx1 - ${Thread.currentThread().name}")

                // sử dụng hàm withContext để chuyển đổi context từ ctx1 qua ctx2
                withContext(ctx2) {
                    // coroutine đang chạy với context ctx2 và trên thread thread2
                    println("Working in ctx2 - ${Thread.currentThread().name}")
                }

                // coroutine đã thoát ra block withContext nên sẽ chạy lại với context ctx1 và trên thread thread1
                println("Back to ctx1 - ${Thread.currentThread().name}")
            }
        }
        println("out of ctx2 block - ${Thread.currentThread().name}")
    }
    println("out of ctx1 block - ${Thread.currentThread().name}")
}

/**
 * Công dụng tuyệt vời của hàm withContext sẽ được chúng ta sử dụng hầu hết trong các dự án.
 * Cụ thể chúng ta sẽ get data dưới background thread và cần UI thread để update UI:
 */
fun withContextFunc() {
    GlobalScope.launch(Dispatchers.IO) {
        // do background task
        withContext(Dispatchers.Main) {
            // update UI
        }
    }
}

fun main() {
//    withContextSwitchThread()
//    dispatcher()
    unconfinedDispatcher()
}

/**
 * Các loại Dispatcher trong Coroutine
 */

fun dispatcher() = runBlocking<Unit> {
    launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
        println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
        println("Default               : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }
}

/**
 * Kết quả là ban đầu coroutine chạy trên main thread. Sau khi bị delay 1 giây thì chạy tiếp trên background thread chứ không phải chạy trên main thread nữa.
 * Bởi vì dispatcher Dispatchers.Unconfined này chạy một coroutine không giới hạn bất kỳ thread cụ thể nào. Ban đầu coroutine
 * chưa được confined (tạm dịch là siết lại vậy) thì nó sẽ chạy trên current thread. Ở đây current thread đang chạy là main thread
 * nên nó sẽ chạy trên main thread cho đến khi nó bị suspend (ở đây ta dùng hàm delay để suspend nó). Sau khi coroutine đó resume thì
 * nó sẽ không chạy trên current thread nữa mà chạy trên background thread.
 */
fun unconfinedDispatcher() = runBlocking {
    launch(Dispatchers.Unconfined) { // chưa được confined (siết lại) nên nó sẽ chạy trên main thread
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        // hàm delay() sẽ làm coroutine bị suspend sau đó resume lại
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }
}