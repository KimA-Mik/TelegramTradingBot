package data.config

import domain.config.LocalConfig
import domain.config.LocalConfigDataSource
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File

class LocalConfigDataSourceImpl(
    json: Json,
    configFilePath: String?
) : LocalConfigDataSource {
    private val logger = LoggerFactory.getLogger(LocalConfigDataSourceImpl::class.java)
    private val localConfig: LocalConfig = run {
        if (configFilePath.isNullOrBlank()) return@run LocalConfig()
        try {
            val file = File(configFilePath)
            if (!file.exists()) return@run LocalConfig()
            val content = file.readText().trim()
            if (content.isEmpty()) return@run LocalConfig()
            json.decodeFromString(LocalConfig.serializer(), content)
        } catch (e: Exception) {
            logger.warn("Local config could not be deserialized because: ${e.message}")
            LocalConfig()
        }
    }

    override fun getLocalConfig(): LocalConfig = localConfig
}