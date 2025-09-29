package presentation.telegram

import kotlinx.coroutines.flow.flowOf
import presentation.telegram.core.screen.BotScreen

class UpdateHandler(
//    service: UpdateService
) {
    val outScreens = flowOf<BotScreen>()/*= service
        .updates
        .mapNotNull {
            updateToScreen(it)
        }*/
//
//    private fun updateToScreen(update: TelegramUpdate): BotScreen {
//        return when (update) {
//            is TelegramShareUpdate -> FuturePriceUpdate(
//                userId = update.userId,
//                state = FuturePriceUpdate.State.ShowUpdate(
//                    share = update.share
//                )
//            )
//
//            is TelegramSharePriceInsufficientUpdate -> FuturePriceInsufficientUpdate(
//                userId = update.userId,
//                share = update.share
//            )
//
//            is TelegramIndicatorUpdate -> IndicatorsUpdateScreen(
//                userId = update.userId,
//                ticker = update.ticker,
//                price = update.price,
//                updateData = update.data
//            )
//        }
//    }
}