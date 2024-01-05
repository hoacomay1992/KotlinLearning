package coroutinetutorial

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

//Async & Await

fun main() {
    //asyncMethod()
   // sum()
    lazyAsyncMethod()
}

/**
 *  async kiểu lazy thì coroutine sẽ không chạy ngay. Nó sẽ chỉ chạy code trong block khi có lệnh từ hàm start().
 *  Để khai báo async theo kiểu lazy cũng rất dễ, chỉ cần truyền CoroutineStart.LAZY vào param start trong hàm async là được.
 */
fun lazyAsyncMethod() = runBlocking {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { printOne() }
        val two = async(start = CoroutineStart.LAZY) { printTwo() }
        one.start() // start the first one
        two.start() // start the second one
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

/**
 * ngoài 2 thằng dùng để launch coroutine mà mình đã biết là runBlocking { } và GlobalScope.launch { }, 2 thằng này nó return về kiểu Job.
 * Nay mình sẽ biết thêm một thằng mới cũng để launch coroutine mà không return về kiểu Job nữa, đó là async { }. Chính async sẽ là vị anh hùng giúp ta giải quyết bài toán trên
 *
 * Thứ hai là Deferred<T>: để ý khi bạn return về kiểu Int trong khối block của coroutine thì kết quả trả về của async là kiểu Deferred<Int>,
 * return kiểu String thì trả về kiểu Deferred<String>, không return gì cả thì nó sẽ trả về kiểu Deferred<Unit>. Deferred nó cũng giống Job vậy,
 * nó cũng có thể quản lý lifecycle của coroutine nhưng ngon hơn thằng Job ở chỗ nó mang theo được giá trị kết quả trả về của coroutine.
 * Và khi cần get giá trị này ra thì ta sử dụng hàm await().
 *
 *  await(): như đã giải thích ở trên, await() là một member function của Deferred dùng để get giá trị kết quả trả về. Ví dụ biến kiểu Deferred<Int>
 *      thì gọi hàm await() sẽ trả về giá trị kiểu Int.
 */
fun asyncMethod() = runBlocking {
    val int: Deferred<Int> = async { printInt() }
    val str: Deferred<String> = async { return@async "Sun" }
    val unit: Deferred<Unit> = async { }

    println("Int = ${int.await()}")
    println("String = ${str.await()}")
}

fun printInt(): Int {
    return 10
}

//Như các bạn thấy, chỉ cần 1 giây là đã xử lý được bài toán, nhanh gấp đôi khi sử dụng 1 coroutine (mất 2 giây). Vì ở đây chúng ta sử dụng 2 coroutine
fun sum() = runBlocking {
    val time = measureTimeMillis {
        val one = async { printOne() }
        val two = async { printTwo() }
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")

}

suspend fun printOne(): Int {
    delay(1000L)
    return 10
}

suspend fun printTwo(): Int {
    delay(1000L)
    return 20
}