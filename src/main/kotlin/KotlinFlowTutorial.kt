import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * Trong Coroutin, flow là một loại dữ liệu có thể phát ra nhiều giá trị tuần tự, khác với suspend function chỉ trả về một giá trị duy nhất.
 * ví dụ: có thể sử dụng flow để nhận dữ liệu cập nhật trực tiếp từ cơ sở dữ liệu.
 *
 * Flow được xây dựng dựa trên coroutin và có thể cung cấp nhiều giá trị. Về cơ bản, flow là một dòng dữ liệu có thể được tính toán không đồng bộ.
 * các giá trị trả về phải thuộc cùng một loại dữ liệu. ví dụ Flow<Int> là một flow trả về giá trị số nguyên
 *
 * Flow rất giống với Iterator có khả năng tạo ra một dãy giá trị, nhưng flow sử dụng suspen function để tạo và xử lý các giá trị một cách không đồng bộ.
 * ví dụ: flow có thể tạo yêu cầu mạng một cách an toàn để tạo giá trị tiếp theo mà không cần chặn luồng thực thi chính.
 *
 *
 *
 * Hàm tạo flow được thực thi trong một coroutine. Do đó, hàm này hưởng lợi từ cùng các API không đồng bộ, nhưng có một số hạn chế như sau:

Flow mang tính tuần tự. Vì thực thể tạo (producer) nằm trong coroutine nên khi gọi suspend function, thực thể tạo sẽ dừng hoạt động cho đến khi hàm tạm ngưng hoạt động trở lại.
Trong ví dụ, thực thể tạo tạm ngưng cho đến khi yêu cầu của mạng fetchLatestNews hoàn tất. Chỉ khi đó, kết quả này mới được phát vào dòng dữ liệu này.

Với hàm tạo flow, thực thể tạo không thể emit (gửi) giá trị từ một CoroutineContext khác. Vì vậy, đừng gọi emit trong một CoroutineContext khác bằng cách tạo
coroutine mới hoặc bằng cách sử dụng khối mã withContext. Bạn có thể sử dụng hàm tạo flow khác như callbackFlow trong những trường hợp như vậy.

 *
 * Khối flow { } là một builder function giúp ta tạo ra 1 đối tượng Flow.
Code bên trong flow { ... } có thể suspend, điều này có nghĩa là chúng ta có thể gọi các suspend function trong khối flow { }.
Vì vậy function foo() gọi khối flow { } không cần thiết phải là suspend function nữa.
Hàm emit dùng để emit các giá trị từ Flow. Hàm này là suspend function
Hàm collect dùng để get giá trị được emit từ hàm emit. Hàm này cũng là suspend function.
 *
 *
 * Các Flow là các luồng lạnh (cold streams) tương tự như các Sequences. Điều đó có nghĩa là code bên trong flow { } sẽ không chạy cho đến khi Flow gọi hàm collect.
 * Chúng ta có thể thấy mặc dù gọi hàm foo() nhưng code trong Flow vẫn không chạy. Cho đến khi Flow gọi hàm collect thì code
 * trong Flow mới chạy và code đó sẽ chạy lại khi chúng ta gọi lại hàm collect.
 *
 * các cách để create Flow loài hàm builder flow{}
 *
 *  val data = flowOf(1,"abc", 3.4, "def")
 *
 *  Các Collections, Arrays, Sequences hay một kiểu T gì đó đều có thể convert sang Flow thông qua extension function là asFlow().
 *  Hình dưới đây liệt kê đầy đủ các extension function asFlow()
 *  ví dụ:  listOf(1, "abc", 3.4, "def").asFlow().collect { println(it) }
 */
fun main(args: Array<String>) {
    //testSequenceBlockMainThread()
//    testFlowBlockMainThread()
//    testCancellationWithTimeout()
//   testCancellationWithTimeoutSleep()
//    takeOperation()
//    transformOperation()
//    mapOperation()
//    onEachOperation()
//    reduceOperation()
//    foldOperation()
//    zipOperation()
//    flatMapConcatOperation()
    // flatMapMergeOperation()
//    flatMapLatestOperation()
//    flowContext()
//    flowException()
    launchInOperation()
}

/**
 * fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun main() = runBlocking<Unit> {
events()
.onEach { event -> println("Event: $event") }
.collect() // <--- Collecting the flow waits
println("Done")
}

Output:

Event: 1
Event: 2
Event: 3
Done

Như vậy, dòng code println("Done") đã phải đợi flow kết thúc việc collect mới được chạy.
Nếu chúng ta không muốn điều này xảy ra, chúng ta muốn coroutine vẫn tiếp tục chạy xuống code phía dưới dù có đang delay hay collect.
Toán tử launchIn sẽ giúp chúng ta.
 *
 * launchIn  Toán tử này truyền vào một param là CoroutineScope và return một biến Job. Biến job này có thể giúp chúng ta cancel code trong flow mà không
 * cancel hết cả coroutine. Code trong coroutine vẫn tiếp tục chạy.
 */
fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun launchInOperation() = runBlocking<Unit> {
    events()
        .onEach { event -> println("Event: $event") }
        .launchIn(this) // <--- Launching the flow in a separate coroutine
    println("Done")
}

//Flow exception
/**
 * Nguồn thu (comsumer) có khả năng throw Exception nếu code chạy trong nguồn phát (producer) xảy ra Exception.
 *
 * Mặc dù ArithmeticException đã bị catch nhưng nguồn thu/nguồn phát đều dừng hoạt động sau khi catch được Exception.
 *
 * Toán tử catch cho phép try/catch exception.
 *
 * Như vậy toán tử catch không thể catch được exception xảy ra trong hàm collect { }.
 * Có một cách để chúng ta có thể sử dụng toán tử catch để catch cả exception xảy ra trong nguồn thu. Nhờ sự trợ giúp của toán tử onEach
 */

fun flowException() = runBlocking {
//    try {
//        fooException().collect { value -> println("VALUE=$value") }
//    } catch (e: Throwable) {
//        println("Caught $e")
//    }

//    fooException().catch { e ->
//        emit("Caught $e")
//    }.collect { value -> println("VALUE = $value") }

    fooExceptionInt().onEach { value ->
        println("3 / $value = ${3 / value}") // nơi xảy ra Exception
    }.catch { e -> println("Caught $e") }
        .collect()

}

/**
 * Exception đã xảy ra khi i = 0 và hiển nhiên nguồn thu/nguồn phát đều phải dừng hoạt động.
 * Chúng ta hoàn toàn có thể try/catch để catch exception này trong hàm thu.
 */
fun fooException(): Flow<String> = flow {
    for (i in 3 downTo -3) {
        println("3/ $i = ${3 / i}")
        emit(i.toString())
    }
}

fun fooExceptionInt(): Flow<Int> = flow {
    for (i in 3 downTo -3) {
        emit(i) // emit next value
    }
}

//https://viblo.asia/p/cung-hoc-kotlin-coroutine-phan-10-flow-part-3-of-3-aWj53G4o56m
//Flow context
/**
 * Code trong khối flow {...} chạy trên context của nguồn thu (consumer)
 * Dễ hiểu, vì hàm collect (nguồn thu) được gọi bên trong khối runBlocking (sử dụng context với dispatcher là Dispatchers.Main)
 * nên code trong khối flow chạy trên context này tức là chạy trên Dispatchers.Main.
 *
 * Tuy nhiên, trong một số bài toán (đặc biệt là bài toán long-running CPU-consuming code),
 * chúng ta mong muốn code trong khối flow được chạy với Dispatchers.Default (background thread) và update UI với Dispatchers.Main (main thread).
 * Toán tử flowOn sẽ cho phép code trong khối flow được chạy trên bất kỳ context nào ta muốn. Cùng xem code:
 */
fun flowContext() = runBlocking {
    // fooContext().collect { value -> log("Collected $value") }
    fooFlowOn().collect { value -> log("Collected $value") }
}

fun fooFlowOn(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // pretend we are computing it in CPU-consuming way
        log("Emitting $i")
        emit(i) // emit next value
    }
}.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder


fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun fooContext(): Flow<Int> = flow {
    log("Started foo flow")
    for (i in 1..3) {
        emit(i) // nguồn phát
    }
}

//Toán tử flatMapConcat(), flatMapMerge(), flatMapLatest()
/**
 * Công dụng của các toán tử flatMap này đều dùng để xử lý bài toán sau: Giả sử chúng ta có rất nhiều flow
 * là flowA, flowB, flowC, flowD,.... flowA emit data sang cho flowB, flowB nhận và tiếp tục xử lý data đó rồi
 * emit nó sang flowC, cứ như vậy cho đến flow cuối cùng. 3 toán tử này đều là flatMap nên đều được dùng trong bài
 * toán trên, mình sẽ so sánh sự khác nhau của nó bằng 3 đoạn code. Ví dụ chung mình đưa ra cho cả 3 toán tử là:
 * Có một flowA sẽ emit 3 giá trị là số 1, số 2 và số 3 sang cho 1 flowB khác, trước khi nó emit nó bị delay 100ms.
 * Với mỗi giá trị mà flowB nhận được từ flowA, flowB sẽ xử lý và emit ra 2 giá trị First và Second và có delay 500ms giữa 2 lần emit.
 */
fun requestFlow(i: Int): Flow<String> = flow { // Đây là flowB
    emit("$i: First")
    delay(500) // wait 500 ms
    emit("$i: Second")
}

/**
 * flatMapLatest đã hủy tất cả code trong khối của nó là flowB khi nó gặp delay trong flowB và tiếp tục collect data từ flowA.
 * Như vậy sau khi flowA emit ra số 1, số 1 sẽ vào tới flowB gặp delay và flowA đang rất nóng vội để emit tiếp phần tử thứ 2
 * nên flowB sẽ bị hủy ngay tại đó. flowA tiếp tục emit tiếp số 2, số 2 lại vào tới flowB gặp delay nên flowB bị hủy ngay tại đó.
 * flowA tiếp tục emit tiếp số 3 cũng là phần tử cuối cùng, nó lại vào tới flowB gặp delay nhưng nó không bị hủy vì flowA đã emit ra phần tử cuối cùng rồi, ko thể emit thêm được nữa.
 */
fun flatMapLatestOperation() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    // Dưới đây là flowA
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
        .flatMapLatest { requestFlow(it) }
        .collect { value -> // collect and print
            println("$value at ${System.currentTimeMillis() - startTime} ms from start")
        }
}

/**
 *  toán tử này collect tất cả các luồng đến và hợp nhất các giá trị của chúng thành một luồng duy nhất để các giá trị được phát ra càng sớm càng tốt.
 *  Toán từ này nó không đợi flowB emit xong phần tử Second như flatMapConcat mà nó tiếp tục collect tiếp từ flowA. Vậy nên 300ms đầu tiên,
 *  cả 3 phần tử First được in ra trước. delay thêm 500ms sau thì các toán tử Second mới được in ra.
 */
fun flatMapMergeOperation() = runBlocking {
    val startTime = System.currentTimeMillis()
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
        .flatMapMerge { requestFlow(it) }
        .collect { value -> // collect and print
            println("$value at ${System.currentTimeMillis() - startTime} ms from start")
        }
}


/**
 * Nhìn vào các mốc thời gian 100ms (do delay 100ms trong flowA), 600ms (do delay 500ms tiếp theo trong flowB), 700ms (delay 100ms tiếp theo),
 * 1200ms (delay 500ms tiếp theo), 1300ms (delay 100ms tiếp theo), 1800ms (delay 500ms tiếp theo).
 * Vậy toán tử này sẽ chờ đợi đến khi flowB hoàn thành cả 2 emit rồi mới bắt đầu collect data tiếp theo từ flowA.
 */
fun flatMapConcatOperation() = runBlocking {
    val startTime = System.currentTimeMillis()
    (1..3).asFlow().onEach { delay(100) }
        .flatMapConcat { requestFlow(it) }
        .collect { value -> println("$value at ${System.currentTimeMillis() - startTime} ms from start") }
}


fun zipOperation() = runBlocking {
    val nums = (1..3).asFlow()
    val strs = flowOf("one", "two", "three")
    nums.zip(strs) { a, b -> "$a - > $b" }
        .collect { value -> println(value) }
}

/**
 * Toán tử này khá giống toán tử reduce(). Nó cũng có chức năng chính là tính tổng, tuy nhiên nó khác ở chỗ hàm reduce tính tổng từ con số 0 còn hàm fold tính tổng
 * từ một giá trị được cho trước.
 */
fun foldOperation() = runBlocking {
    val sum = (1..3).asFlow()
        .fold(initial = 10) { a, b ->
            println("Tổng đã tích lũy: $a đồng")
            println("Giá trị mới: $b đồng")
            a + b
        }
    println("Kết quả: $sum đồng")
}

/**
 * Hàm reduce cực hữu ích khi chúng ta cần tính tổng cộng dồn tất cả giá trị được phát ra từ nguồn phát
 *
 */
fun reduceOperation() = runBlocking {
    //Đoạn code trên mình phát 3 giá trị là 1, 2, 3. Sau đó qua hàm map để bình phương giá trị đó lên thành 1, 4, 9.
    // Sau đó hàm reduce sẽ cộng dồn 3 giá trị này lại 1 + 4 + 9 = 14 và mình in cái tổng này ra như output.
    val sum = (1..3).asFlow()
        .map { it * it }
        .reduce { a, b -> a + b }
    println(sum)
}


/**
 * Toán tử này dùng khi ta muốn thực hiện một action gì đó trước khi value từ flow được emit.
 */
fun onEachOperation() = runBlocking {
    //Ví dụ muốn mỗi phần tử bị delay 3s trước khi được emit ra.
    val nums = (1..3).asFlow()
        .onEach { delay(3000) }
    val startTime = System.currentTimeMillis()
    nums.collect { value -> println("$value at ${System.currentTimeMillis() - startTime} ms from start") }
}

/**
 * toán tử transform cho phép ta skip phần tử hoặc emit một phần tử nhiều lần còn toán tử map thì không thể skip hay emit multiple times.
 * Với mỗi phần tử nhận được từ nguồn phát, nguồn thu sẽ xử lý biến đổi và emit một và chỉ một giá trị cho nguồn thu (tức là phát 1 thì thu 1, phát 10 thì thu 10).
 */
fun mapOperation() = runBlocking {
    (1..9).asFlow()
        .map { value ->
            value * value
        }
        .collect { reponse -> println(reponse) }
}

/**
 * Toán tử này được dùng để biến đổi giá trị được phát ra từ nguồn phát trước khi emit cho nguồn thu nhận nó.
 * Ngoài công dụng chính là để biến đổi phần tử, nó còn có các công dụng khác như nguồn thu có thể bỏ qua (skip)
 * các giá trị mà nó không muốn nhận từ nguồn phát hoặc chúng ta có thể emit một giá trị nhiều hơn một lần (có nghĩa là phát 10 giá trị nhưng nhận có thể tới 20 giá trị).
 */
fun transformOperation() = runBlocking {
    (1..9).asFlow()
        .transform { value ->
            if (value % 2 == 0) {
                emit(value * value)
                emit(value * value * value)
            }
        }.collect { reponse -> println(reponse) }
}

/**
 * Đoạn code trên mình chỉ lấy 2 phần tử từ nguồn phát bằng hàm take(2) nên sau khi nguồn phát emit được 2 phần tử đầu,
 * nó lập tức throw CancellationException. Vì vậy câu This line will not execute và phần tử 3 không được in ra. Mặc dù vậy,
 * code trong khối finally vẫn được thực thi, tính năng này rất cần thiết khi bạn muốn close resource.
 */
fun takeOperation() = runBlocking {
    numbers().take(2)
        .collect { value -> println(value) }
}

//take operation
fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        println("this line will not execute")
        emit(3)
    } catch (e: CancellationException) {
        println("Exception: ${e.message}")
    } finally {
        println("close resource here")
    }
}


/**
 * Flow tuân thủ việc các nguyên tắc cancellation chung của coroutines.
 * Việc collect của flow chỉ có thể bị hủy khi và chỉ khi flow đang bị suspend (chẳng hạn như gặp hàm delay) và ngược lại flow không thể bị hủy.
 *
 * Trong 4 giây đầu tiên, số 1 và số 2 được in ra. Đến giây thứ 5, đã hết thời gian timeout mà flow đang bị suspend vì hàm delay(2000)
 * (còn 1 giây nữa tức là đến giây thứ 6 thì flow mới hết suspend) nên flow bị cancel và số 3 không được in ra.
 */
fun testCancellationWithTimeout() = runBlocking {
    withTimeoutOrNull(5000) { // Timeout after 5s
        fooCancelDelay().collect { value -> println(value) }
    }
    println("Done")
}

fun fooCancelDelay(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(2000)
        println("Emitting $i")
        emit(i)
    }
}

fun testCancellationWithTimeoutSleep() = runBlocking {
    withTimeout(1000) { // Timeout after 5s
        fooCancelSleep().collect { value -> println(value) }
    }
    println("Done")
}

fun fooCancelSleep(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(2000)
        println("Emitting $i")
        emit(i)
    }
}

/**
 * Tương tự đoạn code ví dụ Sequence, launch một coroutine trên main thread để kiểm tra liệu main thread có bị block không.
 * Và kết quả cho ta thấy rằng Flow không block main thread, bằng chứng là các số 1, 2, 3 được in ra song song với I'm not blocked.

Tóm lại: Sequence xử lý đồng bộ. Nó sử dụng Iterator và block main thead trong khi chờ đợi item tiếp theo được yield.
Flow xử lý bất đồng bộ. Nó sử dụng một suspend function collect để không block main thread trong khi chờ đợi item tiếp theo được emit.
 */
fun testFlowBlockMainThread() = runBlocking {
    launch {
        println(Thread.currentThread().name)
        for (k in 1..3) {
            delay(900)
            println("I'm not blocked $k")
        }
    }
    // Collect the flow
    val time = measureTimeMillis {
        foo().collect { value -> println(value) }
    }
    println("$time s")
}

/**
 * launch một coroutine trên main thread để kiểm tra liệu main thread có bị block không. hàm Thread.currentThread().name
 * để in ra chữ "main" để chắc chắn rằng coroutine chạy trên main thread.vì coroutine chạy trên main thread nhưng nó
 * không block main thread, đây là đặc điểm của coroutine. Do đó coroutine và hàm foo sẽ chạy song song.
 * Và kết quả cho ta thấy rằng hàm foo chứa Sequence đã block main thread,
 * vì vậy mà 3 dòng I'm blocked đã phải chờ Sequence in hết 3 giá trị ra trước rồi mới đến lượt nó được in ra.
 */
fun testSequenceBlockMainThread() = runBlocking {
    launch {
        println(Thread.currentThread().name)
        for (k in 1..3) {
            delay(1000)
            println("I'm blocked $k")
        }
    }
    val time = measureTimeMillis {
        fooSequences().forEach { value -> println(value) }
    }
    println(time)
}

fun foo(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

suspend fun fooCollections(): List<Int> {
    val list = mutableListOf<Int>()
    for (i in 1..3) {
        delay(1000)
        list.add(i)
    }

    return list
}

suspend fun fooSequences(): Sequence<Int> = sequence { // sequence builder
    for (i in 1..3) {
        Thread.sleep(1000)
        yield(i)
    }
}


