package ovo.sypw.bsp.utils

object StringUtils {
    fun String.Companion.format(format: String, percentage: Float): String {
        val rounded = kotlin.math.round(percentage * 10) / 10
        val strValue = if (rounded % 1.0 == 0.0) "${rounded.toInt()}.0" else rounded.toString()
        return format.replace("%.1f", strValue)
    }

}