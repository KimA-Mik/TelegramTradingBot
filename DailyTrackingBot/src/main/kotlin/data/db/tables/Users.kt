package data.db.tables

import domain.user.model.PriceProlongation
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

object Users : LongIdTable("users") {
    val path = varchar(name = "path", length = 256).default("")
    val registered = datetime("registered").defaultExpression(CurrentDateTime)
    val defaultPriceProlongation = enumerationByName<PriceProlongation>(
        "default_price_prolongation", PriceProlongation.entries.maxBy { it.name.length }.name.length
    ).default(PriceProlongation.NONE)

    val enableSrsi = bool("enable_srsi").default(false)
    val timeframesToFire = integer("timeframes_to_fire").default(3)
}