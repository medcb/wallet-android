package io.horizontalsystems.bankwallet.modules.importcexaccount

import io.horizontalsystems.bankwallet.R

sealed class Cex {
    abstract val name: String
    abstract val id: String
    abstract val icon: Int
    abstract val title: String
    abstract val url: String

    companion object {
        fun all() = listOf(
            CexCoinzix(),
            CexBinance(),
        )

        fun getById(cexId: String) = all().find {
            it.id == cexId
        }
    }
}

class CexBinance : Cex() {
    override val name = "Binance"
    override val id = "binance"
    override val icon = R.drawable.ic_cex_binance_32
    override val title = "Binance"
    override val url = "https://www.binance.com"
}

class CexCoinzix : Cex() {
    override val name = "Coinzix"
    override val id = "coinzix"
    override val icon = R.drawable.ic_cex_coinzix_32
    override val title = "Coinzix"
    override val url = "https://coinzix.com"
}