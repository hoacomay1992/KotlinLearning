package coroutinetutorial

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

//Coroutine Scope
/**
 * khi bạn chạy 10 coroutine để thực thi 10 task trong 1 activity nào đó. Khi Activity đó bị destroy, các result của các task trên không còn cần thiết nữa.
 * Làm thế nào để stop 10 coroutine kia để tránh memory leaks. Tất nhiên, bạn có thể stop thủ công từng coroutine bằng hàm cancel(),
 * nhưng Kotlin Coroutines cung cấp một thằng có khả năng quản lý vòng đời của cả 10 coroutine kia: CoroutineScope
 *
 * CoroutineScope có các đặc điểm sau cần phải ghi nhớ và cẩn thận khi làm việc với Coroutine

 * - Khi một coroutine A được phóng trong CoroutineScope của một coroutine B, thì A là con của B.
 * Coroutine con sẽ sử dụng scope và context của coroutine cha. Nếu coroutine con đó được khai báo trong 1 scope
 * riêng với context riêng thì nó sẽ ưu tiên sử dụng scope đó thay vì của cha nó.
 *
 * - Một coroutine cha luôn chờ đợi để tất cả các coroutine con của nó chạy xong hoàn thành nhiệm vụ
 * - Khi coroutine cha bị hủy, tất cả các con của nó cũng bị hủy theo
 *
 * coroutine builder nữa là coroutineScope { }. Nó cũng chạy tuần tự như runBlocking { } vậy, chỉ khác là nó là một suspend function nên chỉ có thể tạo ra bên
 * trong một suspend function khác hoặc trong một coroutine scope.
 */
/**
 * Giải thích, đầu tiên các code bên trong runBlocking được chạy tuần tự từ trên xuống. Khi nó launch coroutine 1,
 * trong coroutine 1 có delay 200ms nhưng runBlocking sẽ không chờ mà chạy xuống để launch tiếp coroutine 2.
 * Trong coroutine 2 lại launch 1 coroutine con gọi là coroutine 3. Nhưng ở cả coroutine 2 và 3 đều có delay.
 * Đáng lẽ runBlocking phải chạy xuống dòng code cuối để print ra line code 4, nhưng không, nó được in ra cuối cùng.
 * Bởi vì trong cùng 1 scope do runBlocking tạo ra (scope 1) thì bản thân nó phải chờ tất cả các đứa con của nó (coroutine 1,2 và 3)
 * chạy xong rồi nó mới chạy code của nó. Vậy nên, line code 3 bị delay ít nhất là 100ms nên được print ra trước, kế tiếp print line code 1 và line code 2.
 */
//fun main() = runBlocking { // scope 1
//    launch {       // coroutine 1
//        delay(200L)
//        println("Task from runBlocking")   // line code 1
//    }
//    coroutineScope { // coroutine 2   // scope 2
//        launch {   // coroutine 3
//            delay(500L)
//            println("Task from nested launch") // line code 2
//        }
//        delay(100L)
//        println("Task from coroutine scope") // line code 3
//    }
//    println("Coroutine scope is over") // line code 4
//}

/**
 * Khi coroutine cha bị hủy, tất cả các con của nó cũng bị hủy theo
 */
//fun main() = runBlocking<Unit> {
//    val request = launch {
//        launch {
//            delay(100)
//            println("job2: I am a child of the request coroutine")   // line code 1
//            delay(1000)
//            println("job2: I will not execute this line if my parent request is cancelled") // line code 2
//        }
//    }
//    delay(500)
//    request.cancel() // cancel processing of the request
//    delay(1000)
//    println("main: Who has survived request cancellation?") // line code 3
//}


/**
 * Chúng ta đã biết khi cancel coroutine cha thì tất cả coroutine con bị cancel. Tuy nhiên nếu coroutine con đó có scope
 * là GlobalScope thì nó sẽ không bị cancel khi coroutine cha bị hủy. Vì vậy, line code 1 vẫn được print mặc dù bị delay tới 1000ms.
 */
fun main() = runBlocking<Unit> {
    val request = launch {
        // it spawns two other jobs, one with GlobalScope
        GlobalScope.launch {
            println("job1: GlobalScope and execute independently!")
            delay(1000)
            println("job1: I am not affected by cancellation")  // line code 1 này vẫn được in ra mặc dù bị delay 1000ms
        }
        // and the other inherits the parent context
        launch {
            delay(100)
            println("job2: I am a child of the request coroutine")
            delay(1000)
            println("job2: I will not execute this line if my parent request is cancelled")
        }
    }
    delay(500)
    request.cancel() // cancel processing of the request
    delay(1000) // delay a second to see what happens
    println("main: Who has survived request cancellation?")
}

