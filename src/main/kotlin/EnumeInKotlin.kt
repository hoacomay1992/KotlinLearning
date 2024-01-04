fun main(args: Array<String>) {
//    var color = Color.BLUE
//    println("value of color BLUE: r = ${color.r}, g = ${color.g}, b = ${color.b}")
//    println(ProtocolState.TALKING.signal())
    var a = IPL.T1
    println(a.test(4))
}

//Enum
/**
 * Enum class là một kiểu dữ liệu (data type) bao gồm một tập các giá trị được đặt tên.
 * Mỗi hằng số enum là một object, được phân cách nhau bởi dấu phẩy.
 * Mọi hằng số enum đều có các thuộc tính để lấy tên và vị trí của nó trong khai báo lớp enum, đó là:
 */
enum class Fruit {
    Apple, Orange
}

/**
 * Kiểu enum cũng có thể có constuctor(hàm khởi tạo) của nó và có thể có dữ liệu tùy chỉnh được liên kết với mỗi hằng số enum
 */
enum class Color(val r: Int, val g: Int, val b: Int) {
    RED(255, 0, 0),
    ORANGE(255, 165, 0),
    BLUE(0, 0, 255)
}

/**
 * Enum với Anonymous class: Hằng số Enum cũng có thể khai báo các class ẩn danh của riêng chúng với các
 * phương thức tương ứng của chúng, cũng như override các phương thức cơ sở.
 */
enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },
    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}

interface TestImplement {
    fun test(a: Int): Int
}

interface TestImplement2 {
    fun test2(b: Int): Int
}

/**
 * một enum class có thể implement một hoặc nhiều interface, cung cấp triển khai một thành viên interface duy nhât cho
 * tất cả các entry hoặc phân tách cho mỗi entry trong class ẩn danh của nó.
 */

enum class IPL : TestImplement, TestImplement2 {
    T1 {
        override fun test(a: Int): Int {
            return a * a
        }
    },
    T2 {
        override fun test(a: Int): Int {
            return a * a
        }
    };

    override fun test2(b: Int): Int {
        return b * b
    }
}
// Use
//val ipl = IPL.T1
//println(ipl.test(2))   		// 4
//val ipl2 = IPL.T2
//println(ipl2.test2(4))