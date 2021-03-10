package io.horizontalsystems.bankwallet.modules.coin

import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.chartview.*
import io.horizontalsystems.chartview.models.ChartPoint
import io.horizontalsystems.chartview.models.MacdInfo
import io.horizontalsystems.core.entities.Currency
import io.horizontalsystems.xrateskit.entities.*
import java.lang.Long.max
import java.math.BigDecimal

data class ChartInfoViewItem(
        val chartData: ChartData,
        val chartType: ChartView.ChartType,
        val diffValue: BigDecimal
)

data class ChartPointViewItem(
        val date: Long,
        val price: CurrencyValue,
        val volume: CurrencyValue?,
        val macdInfo: MacdInfo?
)

data class CoinDetailsViewItem(
        val currency: Currency,
        val rateValue: BigDecimal,
        val marketCap: BigDecimal,
        val circulatingSupply: CoinModule.CoinCodeWithValue,
        val totalSupply: CoinModule.CoinCodeWithValue,
        val rateHigh24h: BigDecimal,
        val rateLow24h: BigDecimal,
        val volume24h: BigDecimal,
        val marketCapDiff24h: BigDecimal,
        val coinMeta: CoinMeta,
        val rateDiffs: Map<TimePeriod, Map<String, BigDecimal>>
)

data class LastPoint(
        val rate: BigDecimal,
        val timestamp: Long
)

class RateChartViewFactory {
    fun createChartInfo(type: ChartType, chartInfo: ChartInfo, lastPoint: LastPoint?): ChartInfoViewItem {
        val chartData = createChartData(chartInfo, lastPoint)
        val chartType = when (type) {
            ChartType.TODAY -> ChartView.ChartType.TODAY
            ChartType.DAILY -> ChartView.ChartType.DAILY
            ChartType.WEEKLY -> ChartView.ChartType.WEEKLY
            ChartType.WEEKLY2 -> ChartView.ChartType.WEEKLY2
            ChartType.MONTHLY -> ChartView.ChartType.MONTHLY
            ChartType.MONTHLY3 -> ChartView.ChartType.MONTHLY3
            ChartType.MONTHLY6 -> ChartView.ChartType.MONTHLY6
            ChartType.MONTHLY12 -> ChartView.ChartType.MONTHLY12
            ChartType.MONTHLY24 -> ChartView.ChartType.MONTHLY24
        }

        return ChartInfoViewItem(chartData, chartType, chartData.diff())
    }

    fun createCoinDetailsViewItem(coinMarket: CoinMarketDetails, currency: Currency, coinCode: String): CoinDetailsViewItem {
        return CoinDetailsViewItem(
            currency = currency,
            rateValue = coinMarket.rate,
            marketCap = coinMarket.marketCap,
            circulatingSupply = CoinModule.CoinCodeWithValue(coinCode, coinMarket.circulatingSupply),
            totalSupply = CoinModule.CoinCodeWithValue(coinCode, coinMarket.totalSupply),
            rateHigh24h = coinMarket.rateHigh24h,
            rateLow24h = coinMarket.rateLow24h,
            volume24h = coinMarket.volume24h,
            marketCapDiff24h = coinMarket.marketCapDiff24h,
            coinMeta = coinMarket.meta,
            rateDiffs = coinMarket.rateDiffs
        )
    }

    private fun createChartData(chartInfo: ChartInfo, lastPoint: LastPoint?): ChartData {
        val points = chartInfo.points.map { ChartPoint(it.value.toFloat(), it.volume?.toFloat(), it.timestamp) }.toMutableList()
        val chartInfoLastPoint = chartInfo.points.lastOrNull()
        var endTimestamp = chartInfo.endTimestamp

        if (lastPoint != null && chartInfoLastPoint?.timestamp != null && lastPoint.timestamp > chartInfoLastPoint.timestamp) {
            endTimestamp = max(lastPoint.timestamp, endTimestamp)
            points.add(ChartPoint(lastPoint.rate.toFloat(), null, lastPoint.timestamp))
        }

        return ChartDataFactory.build(points, chartInfo.startTimestamp, endTimestamp, chartInfo.isExpired)
    }
}