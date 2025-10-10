package presentation.telegram.core

sealed class NavigationGraphNode(val destination: String)
object NavigationRoot : NavigationGraphNode("") {
    object SecuritySearch : NavigationGraphNode("security_search")
    object SecurityList : NavigationGraphNode("security_list") {
        object SecurityDetails : NavigationGraphNode("security_details") {
            object EditPrice : NavigationGraphNode("edit_price")
            object EditNote : NavigationGraphNode("edit_note")
            object EditPercentage : NavigationGraphNode("edit_percentage")
        }
    }
}
