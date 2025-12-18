package domain.config

interface LocalConfigDataSource {
    fun getLocalConfig(): LocalConfig
}