package io.xeros.util

import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import java.time.temporal.ChronoUnit

object ProgressbarUtils {

    fun progress(task: String, amt: Long): ProgressBar = ProgressBarBuilder().
        setTaskName(task).
        setStyle(ProgressBarStyle.ASCII).
        setInitialMax(amt).
        setUpdateIntervalMillis(1).
        showSpeed().
        setSpeedUnit(ChronoUnit.SECONDS).
    build()

    fun progress(task: String, amt: Int): ProgressBar = ProgressBarBuilder().
        setTaskName(task).
        setStyle(ProgressBarStyle.ASCII).
        setInitialMax(amt.toLong()).
        setUpdateIntervalMillis(1).
        showSpeed().
        setSpeedUnit(ChronoUnit.SECONDS).
    build()

}