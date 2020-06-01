package moe.youranime.main.auth

import moe.youranime.main.config.Configuration
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class User(
    val active: Boolean,
    val limit: Boolean,
    val username: String,
    val name: String,
    val hex: String,
    val createdAt: Date?,
    val updatedAt: Date?
) {
    class Builder(dateFormat: String = "") {
        private var active: Boolean = false
        private var limit: Boolean = false
        private lateinit var username: String
        private lateinit var name: String
        private lateinit var hex: String
        private var createdAt: Date? = null
        private var updatedAt: Date? = null
        private var dateParser = SimpleDateFormat(dateFormat, Locale.getDefault())

        fun active(active: Boolean) = apply { this.active = active }
        fun limit(limit: Boolean) = apply { this.limit = limit }
        fun username(username: String) = apply { this.username = username }
        fun name(name: String) = apply { this.name = name }
        fun hex(hex: String) = apply { this.hex = hex }
        fun createdAt(createdAt: String?) = apply {
            createdAt?.let {
                this.createdAt(dateParser.parse(it))
            }
        }
        fun createdAt(createdAt: Date?) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: String?) = apply {
            updatedAt?.let {
                this.createdAt(dateParser.parse(it))
            }
        }
        fun updatedAt(updatedAt: Date?) = apply { this.updatedAt = updatedAt }

        fun build(): User {
            return User(
                active = active,
                limit = limit,
                username = username,
                name = name,
                hex = hex,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    companion object {
        fun fromGraphql(fetchedUser: SigninUserMutation.User?): User? {
            if (fetchedUser == null) { return null }

            return Builder(Configuration.dateFormat)
                .active(fetchedUser.active)
                .limit(fetchedUser.limited)
                .username(fetchedUser.username)
                .name(fetchedUser.name)
                .hex(fetchedUser.hex)
                .createdAt(fetchedUser.createdAt as String)
                .updatedAt(fetchedUser.updatedAt as String)
                .build()
        }

        fun fromGraphql(fetchedUser: CheckTokenQuery.CurrentUser?): User? {
            if (fetchedUser == null) { return null }

            return Builder(Configuration.dateFormat)
                .active(fetchedUser.active)
                .limit(fetchedUser.limited)
                .username(fetchedUser.username)
                .name(fetchedUser.name)
                .hex(fetchedUser.hex)
                .createdAt(fetchedUser.createdAt as String)
                .updatedAt(fetchedUser.updatedAt as String)
                .build()
        }
    }
}