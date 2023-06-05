package ru.altmanea.webapp.data

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate

import kotlinx.serialization.Serializable
import ru.altmanea.webapp.type.TypeError
import kotlin.jvm.JvmInline

@Serializable
class BibtexFormat private constructor(
    val type: String, //@*type*{ ... ----book, article
    val ID: String, // @*type*{ **bookID**, ...
    val journal: Journal,
    val title: Title, //title = { ... }
    val DOI: String? = null, //DOI = { ... } / DOI == digital object identifier
    val author: Author, //author = { ... } ----may contain multiple authors && keys ""and"", "",""
    val year: Year? = null, // year = { ... }
    val publisher: Publisher // publisher = { ... }
) {
    companion object {
        operator fun invoke(
            type: String, //@*type*{ ... ----book, article
            ID: String, // @*type*{ **bookID**, ...
            journal: Either<TypeError, Journal>,
            title: Either<TypeError, Title>, //title = { ... }
            DOI: String? = null, //DOI = { ... } / DOI == digital object identifier
            author: Either<TypeError, Author>, //author = { ... } ----may contain multiple authors && keys ""and"", "",""
            year: Either<TypeError, Year>, // year = { ... }
            publisher: Either<TypeError, Publisher> // publisher = { ... }
        ): EitherNel<TypeError, BibtexFormat> = either {
            zipOrAccumulate({
                title.bind()
            }, {
                year.bind()
            }, {
                author.bind()
            }, {
                publisher.bind()
            }, {
                journal.bind()
            }) { tit, y, auth, pub, jor ->
                BibtexFormat(type, ID, jor, tit, DOI, auth, y, pub)
            }
        }
    }
}

@Serializable
@JvmInline
value class Title private constructor(
    val title: String
) {
    companion object {
        private val rightRegister = Regex("\\p{Lu}\\p{Ll}+")

        operator fun invoke(title: String): Either<TypeError, Title> = either {
            println(title)
            ensure(rightRegister.matches(title)) { TypeError.WrongName("Название статьи должно быть с большой буквы") }
            Title(title)
        }
    }
}

@Serializable
@JvmInline
value class Year private constructor(
    val year: String
) {
    companion object {
        private val rightRegister = Regex("\\d+")

        operator fun invoke(year: String): Either<TypeError, Year> = either {
            println(year)
            ensure(rightRegister.matches(year)) { TypeError.WrongName("Год может быть только числом") }
            Year(year)
        }
    }
}

@Serializable
@JvmInline
value class Author private constructor(
    val fullName: String
) {
    companion object {
        private val rightRegister = Regex("\\D+")

        operator fun invoke(fullName: String): Either<TypeError, Author> = either {
            if (fullName != "") {
                ensure(rightRegister.matches(fullName)) { TypeError.WrongName("В именах авторов не может быть цифр") }
            }
            Author(fullName)
        }
    }
}

@Serializable
@JvmInline
value class Publisher private constructor(
    val name: String
) {
    companion object {
        private val rightRegister = Regex("(\\p{Lu}\\p{Ll}+ *)(\\p{Ll}+)")

        operator fun invoke(name: String): Either<TypeError, Publisher> = either {
            if (name != "")
                ensure(rightRegister.matches(name)) { TypeError.WrongName("Название с большой буквы у того, кто выпускает") }
            Publisher(name)
        }
    }
}

@Serializable
@JvmInline
value class Journal private constructor(
    val journal: String
) {
    companion object {
        private val rightRegister = Regex("(\\p{Lu}\\p{Ll}+ *)(\\p{Ll}+)")

        operator fun invoke(journal: String): Either<TypeError, Journal> = either {
            if (journal != "")
                ensure(rightRegister.matches(journal)) { TypeError.WrongName("Название журнала с большой буквы") }
            Journal(journal)
        }
    }
}