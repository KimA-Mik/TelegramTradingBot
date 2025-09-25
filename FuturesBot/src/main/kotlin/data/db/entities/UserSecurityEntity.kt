package data.db.entities

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserShares : LongIdTable() {
    val percent = double("percent")
    val userId = long("userId")
    val shareId = long("shareId")
    val notified = bool("notified")

    val rsiNotified = bool("rsi_notified").default(false)
    val bollingerBandsNotified = bool("bollinger_bands_notified").default(false)

    val rsiNotificationsEnabled = bool("rsi_notifications_enabled").default(true)
    val bbNotificationsEnabled = bool("bb_notifications_enabled").default(false)
}