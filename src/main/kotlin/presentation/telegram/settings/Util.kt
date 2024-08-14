package presentation.telegram.settings

fun stateToText(state: Boolean): String {
    return when (state) {
        true -> SETTINGS_ENABLED
        false -> SETTINGS_DISABLED
    }
}