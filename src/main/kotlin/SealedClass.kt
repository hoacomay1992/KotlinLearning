fun main(args: Array<String>) {
    var circle = Shape.Circle(4.5f)
    var square = Shape.Square(4)
    var rectangle = Shape.Rectangle(4, 5)

    eval(circle)       //Circle area is 63.585
    eval(square)       //Square area is 16
    eval(rectangle)    //
}

/**
 * Một sealed class là một abstract và không thể được khởi tạo
 * Mặc định các contructor của sealed class có modifier là private
 * tất cả các sub-class của sealed phải được khai báo trong cùng một file
 *
 * sealed class rất quan trọng trong việc đảm bảo type safety bằng cách hạn chế tập các kiểu
 * chỉ định tại thời điểm biên dịch (compile-runtime)

 */
sealed class A(var name: String) {
    object B : A("B")
    object C : A("C") {
        object E : A("E")
    }

    init {
        println("Sealed class A")
    }
}

object D : A("D") {
    object F : A("F")
}

sealed class Shape {
    class Circle(var radius: Float) : Shape()
    class Square(var length: Int) : Shape()
    class Rectangle(var length: Int, var breadth: Int) : Shape()
}

fun eval(e: Shape) =
    when (e) {
        is Shape.Circle -> println("Circle area is ${3.14 * e.radius * e.radius}")
        is Shape.Square -> println("Square area is ${e.length * e.length}")
        is Shape.Rectangle -> println("Rectagle area is ${e.length * e.breadth}")
    }
