package component

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.BibtexFormat
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import tools.QueryError
import tools.fetchText

val View = FC<Props> {
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = arrayOf("files").unsafeCast<QueryKey>(),
        queryFn = {
            fetchText(
                Config.filePath
            )
        }
    )

    if (query.isSuccess) {
        val data = Json.decodeFromString<List<BibtexFormat>>(query.data ?: "")

        ReactHTML.ol {
            data.map {
                li {
                    CBibtex {
                        bibtex = it
                    }
                }
            }
        }
    }
}

external interface BibtexProps : Props {
    var bibtex: BibtexFormat
}

val CBibtex = FC<BibtexProps> { props ->
    div {


        table {

            thead {
                tr {
                    th {
                        +"Название"
                    }
                    th {
                        +"Значение"
                    }
                }
            }
            tbody {
                tr {
                    td {
                        +"Type"
                    }
                    td {
                        +props.bibtex.type
                    }
                }
                tr {
                    td {
                        +"Id"
                    }
                    td {
                        +props.bibtex.ID
                    }
                }
                tr {
                    td {
                        +"Journal"
                    }
                    td {
                        props.bibtex.journal?.let {
                            +it.journal
                        }
                    }
                }
                tr {
                    td {
                        +"Title"
                    }
                    td {
                        +props.bibtex.title.title
                    }
                }
                tr {
                    td {
                        +"DOI"
                    }
                    td {
                        props.bibtex.DOI?.let{
                            +it
                        }
                    }
                }
                tr {
                    td {
                        +"Author"
                    }
                    td {
                        props.bibtex.author?.let{
                            +it.fullName
                        }
                    }
                }
                tr {
                    td {
                        +"Year"
                    }
                    td {
                        props.bibtex.year?.let{
                            +it.year
                        }
                    }
                }
                tr {
                    td {
                        +"Publisher"
                    }
                    td {
                        props.bibtex.publisher.let{
                            +it.name
                        }
                    }
                }
            }

        }
    }
}