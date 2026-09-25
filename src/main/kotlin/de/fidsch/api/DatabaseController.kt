package de.fidsch.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/db")
class DatabaseController(
    private val databases: DatabaseRegistry
) {
    @GetMapping("/ping")
    fun ping(): Map<String, Any?> =
        databases.jdbc("fitphp").queryForMap(
            "SELECT 1 AS ok, DATABASE() AS db, CURRENT_USER() AS user"
        )
}
