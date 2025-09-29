package presentation.telegram.core

sealed class NavigationGraphNode(val destination: String)
object NavigationRoot : NavigationGraphNode("") {
    object Security : NavigationGraphNode("security") {
        object EditTicker : NavigationGraphNode("edit_ticker")
        object EditPrice : NavigationGraphNode("edit_price")
        object EditNote : NavigationGraphNode("edit_note")
        object EditPercentage : NavigationGraphNode("edit_percentage")
    }
}
