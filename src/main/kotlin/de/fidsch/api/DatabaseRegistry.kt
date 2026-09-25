package de.fidsch.api

import com.zaxxer.hikari.HikariDataSource
import jakarta.annotation.PreDestroy
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

@Component
class DatabaseRegistry(
    private val jsonMapper: JsonMapper
) {
    private val credentialsPath = Path.of("/work/credentials.json")

    private val dataSources = ConcurrentHashMap<String, HikariDataSource>()

    private val credentials: JsonNode by lazy {
        if (!Files.isRegularFile(credentialsPath)) {
            error("Credentials-Datei nicht gefunden: $credentialsPath")
        }

        jsonMapper.readTree(Files.readString(credentialsPath))
    }

    fun jdbc(configName: String): JdbcTemplate {
        val dataSource = dataSources.computeIfAbsent(configName) {
            createDataSource(configName)
        }

        return JdbcTemplate(dataSource)
    }

    private fun createDataSource(configName: String): HikariDataSource {
        val config = credentials.get(configName)
            ?: error("DB-Konfiguration '$configName' fehlt in $credentialsPath")

        val host = config.requiredText("host")
        val user = config.requiredText("user")
        val password = config.requiredText("password")
        val database = config.requiredText("database")

        return HikariDataSource().apply {
            driverClassName = "org.mariadb.jdbc.Driver"
            jdbcUrl = "jdbc:mariadb://$host/$database"
            username = user
            this.password = password

            poolName = "fidsch-$configName"
            maximumPoolSize = 5
            minimumIdle = 0
        }
    }

    private fun JsonNode.requiredText(name: String): String {
        val node = get(name)

        if (node == null || node.isNull || node.asText().isBlank()) {
            error("Pflichtfeld '$name' fehlt in DB-Konfiguration")
        }

        return node.asText()
    }

    @PreDestroy
    fun close() {
        dataSources.values.forEach { it.close() }
    }
}
