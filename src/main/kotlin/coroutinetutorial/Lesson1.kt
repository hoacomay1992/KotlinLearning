package coroutinetutorial

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

/**
 * Suspend function cho phép ta làm được điều vi diệu hơn. Đó là suspend function có khả năng ngừng hay gián đoạn việc thực thi một lát
 * (trạng thái ngừng là trạng thái suspend) và có thể tiếp tục thực thi lại khi cần thiết. Như hình ảnh dưới đây:
 * functionA bị gián đoạn để functionB chạy và sau khi functionB chạy xong thì function A tiếp tục chạy tiếp.
 *
 * Đặc điểm:
 *  - Suspend function được đánh dấu bằng từ từ khóa suspend
 *  - Chỉ có thể được gọi suspend function bên trong một suspend function khác hoặc bên trong một coroutine
 */

fun launchFirstCoroutine() {
    val start = System.currentTimeMillis()
    GlobalScope.launch {
        delay(10000L)
        println("World, started at ${System.currentTimeMillis() - start}")
    }
    println(" Hello, started at ${System.currentTimeMillis() - start}")
    Thread.sleep(20000L)
    println("Kotlin, started at ${System.currentTimeMillis() - start}")
}

/**
 * Như đã biết coroutine có khả năng chạy mà non-blocking thread. Giả sử, trong trường hợp bạn muốn coroutine chạy blocking thread (chạy tuần tự) thì sao?
Khi đó chúng ta sẽ có block runBlocking { }. Tương tự như block launch { }, bên trong block runBlocking { } cũng là một coroutine được tạo ra và chạy.

Nếu để ý ta sẽ thấy từ World được in ra sau từ Hello là 5 giây. Như vậy có nghĩa là main thread đã bị blocking chờ khi xong hàm delay 5s mới chạy xuống đoạn code println("World").
 */
fun runBlockThreadCoroutine() {
    runBlocking {
        println("Hello")
        delay(5000)
    }
    println("World")
}

/**
 *
 */
val time = measureTimeMillis { coroutineLightWeight() }

fun coroutineLightWeight() = runBlocking {
    repeat(100_000) {
        launch { println("Hello everyone") }
    }
}
