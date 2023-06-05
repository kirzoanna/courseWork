package ru.altmanea.webapp.bibtex

import arrow.core.Either
import arrow.core.EitherNel
import org.jbibtex.BibTeXDatabase
import org.jbibtex.CharacterFilterReader
import org.jbibtex.BibTeXParser
import org.jbibtex.BibTeXEntry
import java.io.File
import java.io.FileReader
import java.io.Reader
import com.mongodb.client.MongoDatabase
import org.jbibtex.Key
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import ru.altmanea.webapp.data.*
import ru.altmanea.webapp.type.TypeError
import java.lang.Exception

val mdbClient = KMongo.createClient("mongodb://mongoadmin:mongoadmin@127.0.0.1:27017")
val mongoDatabase: MongoDatabase = mdbClient.getDatabase("ANNA")
val mongoCollection = mongoDatabase.getCollection<BibtexFormat>().apply { }

//Прочитать файл, если существует путь к имени файла
fun readFromFile(fileNamePath: String): Reader? {
    var result: Reader? = null
    try {
        result = FileReader(File(fileNamePath))
    } catch (anyException: Exception) {
        // [Ошибка] Не удалось найти/прочитать
        println("[ERROR] Unable to find/read '$fileNamePath'")
    }
    return result
}

// Преобразование Reader в базу данных BibTeX с помощью инструментов bibtex
fun toBibtex(reader: Reader): BibTeXDatabase? {
    var database: BibTeXDatabase? = null
    val filterReader = CharacterFilterReader(reader)
//фильтрует все неизвестные/нечитаемые символы
    try {
        database = BibTeXParser().parse(filterReader)
//Выдает exception, если в reader есть записи со специальными ключевыми словами, такими как "for"
    } catch (e: Exception) {
        println(
            "[ERROR] Cannot parse document: publication ID contains values/strings that marked as key words in Kotlin/Java;\n" +
                    "Try: putting '_' or '' over 'for', 'is', 'in', 'as' in publication ID"
        )
        //[[ОШИБКА] Не удается проанализировать документ: ID публикации содержит значения/строки, помеченные как ключевые слова в Kotlin/Java;\n" +
        //"Попробуйте: поместите '_' или ' over 'for', 'is', 'in', 'as' в ID публикации
    }
    return database
}

fun entryToBibtex(entry: BibTeXEntry, key: Key): EitherNel<TypeError, BibtexFormat> {
    val bibtex = BibtexFormat.invoke(
        type = entry.type.value,
        ID = key.value,
        journal = Journal.invoke(entry.getField(BibTeXEntry.KEY_JOURNAL)?.toUserString() ?: ""),
        title = Title.invoke(entry.getField(BibTeXEntry.KEY_TITLE).toUserString()),
        DOI = entry.getField(BibTeXEntry.KEY_DOI)?.toUserString(),
        author = Author.invoke(entry.getField(BibTeXEntry.KEY_AUTHOR)?.toUserString() ?: ""),
        year = Year.invoke(entry.getField(BibTeXEntry.KEY_YEAR)?.toUserString() ?: "0"),
        publisher = Publisher.invoke(entry.getField(BibTeXEntry.KEY_PUBLISHER)?.toUserString() ?: "")

    )

    return bibtex
}



