package coroutinetutorial

import kotlinx.coroutines.*
import java.io.IOException

fun main() {
//    launchCoroutineException()
//    asyncCoroutineException()
    //   catchException()
//    coroutineExceptionHandlerMethod()
    tongHopException()
}

/**
 * Tổng hợp nhiều Exception
 * Sẽ như thế nào nếu nhiều children of a coroutine throw Exception. Như chúng ta đã biết khi xảy ra Exception thì coroutine cũng bị stop,
 * chúng ta sẽ có một nguyên tắc chung là "the first exception wins", vậy exception nào xảy ra đầu tiên thì sẽ được trả về CoroutineExceptionHandler.
 *
 * Như chúng ta đã biết, khi coroutine bị stop thì nó sẽ cố chạy code trong khối finally. Nếu như code trong khối finally cũng throw Exception thì sao??.
 * Khi đó các tất cả Exception xảy ra trong tất cả khối finally sẽ bị suppressed. Chúng ta có thể in tất cả chúng ra bằng hàm exception.getSuppressed()
 */
fun tongHopException() = runBlocking {
    val handler =
        CoroutineExceptionHandler { _, exception -> println("Caught $exception with suppressed ${exception.suppressed.contentToString()}") }
    val job = GlobalScope.launch(handler) {
        launch {
            try {
                delay(Long.MAX_VALUE)//delay vô hạn
            } finally {
                throw ArithmeticException()
            }
        }
        launch {
            try {
                delay(Long.MAX_VALUE) // delay vô hạn
            } finally {
                throw IndexOutOfBoundsException()
            }
        }
        launch {
            delay(100)
            throw IOException()
        }
        delay(Long.MAX_VALUE)
    }
    job.join()
}
//CoroutineExceptionHandler
/**
 * CoroutineExceptionHandler được sử dụng như một generic catch block của tất cả coroutine.
 * Exception nếu xảy ra sẽ được bắt và trả về cho một hàm callback là override fun handleException(context: CoroutineContext, exception: Throwable)
 * và chúng ta sẽ dễ dàng log hoặc handle exception trong hàm đó.
 *
 * Chúng ta thấy AssertionError trong khối launch { } đã bị catch và được print ra. Vì chúng ta không gọi deferred.await() nên ArithmeticException trong khối async { }
 * sẽ không xảy ra. Mà cho dù chúng ta có gọi deferred.await() thì CoroutineExceptionHandler cũng sẽ không catch được Exception này
 * vì CoroutineExceptionHandler không thể catch được những Exception được đóng gói vào biến Deferred. Vậy nên bạn phải
 * tự catch Exception như ở mục 2 mình đã trình bày. Và thêm một chú ý nữa là CoroutineExceptionHandler cũng không thể catch Exception xảy ra trong khối runBlocking { }
 *
 */
fun coroutineExceptionHandlerMethod() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Cought $exception")
    }

    val job = GlobalScope.launch(handler) {
        throw AssertionError()
    }
    val deferred = GlobalScope.async(handler) {
        throw ArithmeticException() // Nothing will be printed, relying on user to call deferred.await()
    }
    joinAll(job, deferred)
}

/**
 * Chúng ta thấy Exception đã bị catch. Nhưng nếu như chúng ta launch 100 coroutine thì phải try catch 100 lần sao??. Đừng lo, vì đã có CoroutineExceptionHandler
 */
fun catchException() = runBlocking {
    GlobalScope.launch {
        try {
            println("Throwing exception from launch")
            throw IndexOutOfBoundsException()
            println("Unreached")
        } catch (e: IndexOutOfBoundsException) {
            println("Caught IndexOutOfBoundsException")
        }
    }
    val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw ArithmeticException()
        println("Unreached")
    }
    try {
        deferred.await()
        println("Unreached")
    } catch (e: ArithmeticException) {
        println("Caught ArithmeticException")
    }
}

/**
 * Tóm lại, launch { } gặp Exception thì throw luôn, còn async { } khi gặp Exception thì nó đóng gói Exception đó vào biến deferred.
 * Chỉ khi biến deferred này gọi hàm await() thì Exception mới được throw ra.
 */
fun launchCoroutineException() = runBlocking {
    GlobalScope.launch {
        println("Throwing exception from launch")
        throw IndexOutOfBoundsException()
        println("Unreached")
    }
}

fun asyncCoroutineException() = runBlocking {
    val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw IndexOutOfBoundsException()
        println("Unreached")
    }
    deferred.await()
}