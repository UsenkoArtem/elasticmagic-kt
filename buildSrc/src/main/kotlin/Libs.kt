object Versions {
    const val kotlin = "1.4.30"
    const val kotlinxSerialization = "1.1.0"
    const val ktor = "1.5.2"

    const val kotest = "4.4.1"
}

object Libs {
    fun kotest(flavor: String): String {
        return "io.kotest:kotest-$flavor:${Versions.kotest}"
    }

    fun kotlinSerialization(flavor: String): String {
        return "org.jetbrains.kotlinx:kotlinx-serialization-$flavor:${Versions.kotlinxSerialization}"
    }

    fun ktorClient(flavor: String): String {
        return "io.ktor:ktor-client-$flavor:${Versions.ktor}"
    }
}
