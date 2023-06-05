package ru.altmanea.webapp.type

sealed interface TypeError  {
    val error: String
    class WrongName(override val error: String) : TypeError
}