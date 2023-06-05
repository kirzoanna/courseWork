package ru.altmanea.webapp.rest

import arrow.core.Either
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.onClick
import ru.altmanea.webapp.bibtex.readFromFile
import ru.altmanea.webapp.bibtex.*

import ru.altmanea.webapp.config.Config
import java.io.File

fun Route.filesRoutes() {
    route(Config.filePath) {
        get {
            val bibtex = mongoCollection.find().toList()

            call.respond(bibtex)
        }
        post("file") {
            val multiPartData = call.receiveMultipart()
            var fileName = ""

            multiPartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("config/$fileName").writeBytes(fileBytes)
                    }

                    else -> {}
                }
                part.dispose()
            }
            if (!fileName.endsWith(".bib") && !fileName.endsWith(".txt")) {
                File("config/$fileName").delete()
                return@post call.respondRedirect("/#")
            } else {
                val reader = readFromFile("config/$fileName")
                val database = reader?.let {
                    toBibtex(it)
                }
                reader?.close()
                database?.let {
                    it.entries.forEach { (key, entry) ->
                        when (val res = entryToBibtex(entry, key)) {
                            is Either.Left -> {
                                val text = res.value.joinToString { it.error }
                                return@post call.respondHtml {
                                    body {
                                        div {
                                            +"Обнаружена ошибка в $key. Будьте внимательны!"
                                        }
                                        div {
                                            +text
                                        }
                                        button {
                                            onClick = "history.back(-1)"
                                            +"Назад"

                                        }
                                    }
                                }
                            }

                            is Either.Right -> {
                                mongoCollection.insertOne(res.value)
                            }
                        }
                    }
                }
                return@post call.respondRedirect("/#")
            }
        }
    }
}