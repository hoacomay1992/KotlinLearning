package coroutinetutorial

/**
 * Generic là một tính năng mà cho phép chúng ta có thể định nghĩa và truy cập các class, method, properties bằng cách sử dụng (hay implementation)
 * các kiểu dữ liệu khác nhau (Hay một cách chung chung) mà vẫn sẽ hoạt động bình thường.
 *
 * Quy ước trong Genetics:
 *      T - Type (Kiểu dữ liệu bất kỳ thuộc Wrapper class: String, Integer, Long, Float,...)
 *      E - Element (phần tử-được sử dụng trong Collection Framework)
 *      K - Key (Khóa)
 *      V - Value (Giá trị)
 *      N - Number (Kiểu số: Integer, Number, Double, Float,...)
 */
fun main() {
//    studentBuilder()
//    personOutObjectBuilder()
    personInObjectBuilder()
}

/**
 * Từ khóa out và in trong Generics.
 *      - Khi muốn gán generic type cho bất kỳ super type của nó, thì sử dụng từ khóa out.
 *      - khi muốn gán generic type cho bất kỳ sub-type của nó sử dụng từ khóa in.
 */
open class Father()
class Son() : Father()

/**
 *    //Khởi tạo class Person sử dụng tính năng Generic có sử dụng từ khóa out
 */
class PersonOut<out T>(val value: T) {}

/**
 * Khởi tạo 2 đối tượng fatherObject và sonObject từ class Person, ta thấy fatherObject là 1 super type của sonObject và vì đã sử dụng từ khóa out ở class Person nên
 * việc gán giá trị của sonObject cho fatherObject là có thể được
 */
fun personOutObjectBuilder() {
    val sonObject: PersonOut<Son> = PersonOut(Son())
    println(sonObject.javaClass.simpleName)
    val fatherObject: PersonOut<Father>
    fatherObject = sonObject
    println(fatherObject.javaClass.simpleName)
}

class PersonIn<in T>() {
    fun say() {
        println("Hello")
    }
}

fun personInObjectBuilder() {
    val fatherObject: PersonIn<Father> = PersonIn()
    val sonObject: PersonIn<Son>
    sonObject = fatherObject
    sonObject.say()
}

/**
 * Cách để định nghĩa kiểu generic cho properties của một class
 * data properties có thể là các kiểu nguyên thủy hoạc một object vv.
 */
class Student<T>(private val data: T) {
    fun showData() {
        println(data)
    }
}

fun studentBuilder() {
    val student: Student<String> = Student("Tri")
    val student1: Student<Int> = Student(18)
    student.showData()
    student1.showData()
}