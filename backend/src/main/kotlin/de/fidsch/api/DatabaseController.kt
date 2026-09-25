package de.fidsch.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/db")
class DatabaseController(private val databases: DatabaseRegistry) {

    @GetMapping("/ping-all")
    fun pingAll(): Map<String, Map<String, Any?>> {
        val configs = listOf(
            "fitphp", "bizphp", "sciphp", "checkphp",
            "loginphp", "phanphp", "blogphp", "ilcophp"
        )

        return configs.associateWith { name ->
            databases.jdbc(name).queryForMap(
                "SELECT 1 AS ok, DATABASE() AS db, CURRENT_USER() AS user"
            )
        }
    }
}
