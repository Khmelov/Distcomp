package com.example.eger.dc.lab2.bean

import com.example.eger.dc.lab2.dto.response.AuthorResponseTo
import kotlinx.serialization.Serializable

@Serializable
data class Author(
	val id: Long?, val login: String, val password: String, val firstname: String, val lastname: String
) {
	fun toResponse(): AuthorResponseTo = AuthorResponseTo(id, login, password, firstname, lastname)
}