package domain.common

fun getFutureSharePrice(sharePrice: Double, futurePrice: Double): Double {
//        val factor = future.lotSize?.toDouble() ?: when (d.toInt()) {
//            in 5..50 -> 10.0
//            in 50..500 -> 100.0
//            in 500..1500 -> 1000.0
//            in 1500..5000 -> 2000.0
//            in 5000..50000 -> 10000.0
//            in 50000..150000 -> 100000.0
//            else -> 1.0
//        }


    if (sharePrice == 0.0) return 0.0
    if (futurePrice == 0.0) return 0.0

    val fraction = futurePrice / sharePrice

    if (fraction < 50.0) return futurePrice / 10.0
    if (fraction < 500.0) return futurePrice / 100.0
    if (fraction < 1500.0) return futurePrice / 1000.0
    if (fraction < 5000.0) return futurePrice / 2000.0
    if (fraction < 50000.0) return futurePrice / 10000.0
    if (fraction < 150000.0) return futurePrice / 100000.0

    return 0.0
}

fun percentBetweenDoubles(number1: Double, number2: Double): Double {
    return (number1 - number2) / number2 * 100.0
}