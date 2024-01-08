package coroutinetutorial

/**
 * extensions cung cấp khả năng mở rộng của một class với các function mới, mà không phải kế thừa class hoặc sử dụng một mẫu thiết kế nào như Decorator.
 * Extensions function trong kotlin là function có thể được khai báo trong class/file hiện tại mà không cần sửa đổi các class tùy chỉnh xác định trước khác.
 *
 */
fun main() {
    val str = "Hello"
    val str1 = " Tất cả"
    val str2 = " các bạn"
    println(str.add(str1, str2))
}

class CheckNumber() {
    fun greaterThenTen(x: Int): Boolean {
        return x > 10
    }
}

/**
 * Nếu muốn viết hàm mới kiểm tra x nhỏ hơn 10, với các class do dev viết thì có thể viết thêm hàm check trong class đó.
 * Nhưng đối với các hàm thư viện, chỉ còn cách extend lại. gây lãng phí tài nguyên và code. thay vào đó một giải pháp hay là
 * sử dụng extension như dưới đây.
 */
fun CheckNumber.lessThenTen(x: Int): Boolean {
    return x < 10
}

fun String.add(str1: String, str2: String): String {
    return this + str1 + str2
}