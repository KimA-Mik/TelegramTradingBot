import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import services.RequestService
import java.util.*


fun main() = runBlocking {
    coroutineScope {
        val t1 = "SBER"
        val t1Price = async(Dispatchers.IO) { RequestService.get().getLastPrice(t1) }

        val t2 = "GAZP"
        val t2Price = async(Dispatchers.IO) { RequestService.get().getLastPrice(t2) }
        println("$t1 price = ${t1Price.await()}")
        println("$t2 price = ${t2Price.await()}")
    }
    val date = Date(1696330380000)
    println(date)
}
