
import kotlinx.coroutines.runBlocking
import services.RequestService
import java.util.*


fun main() {
    println("Hello World!")
    runBlocking {
        val ticker = "SBER"
        val tickerId = RequestService.get().getLastPrice("SBER")
        println("$ticker id = $tickerId")
    }

    val date = Date(1696330380000)
    println(date)

}
