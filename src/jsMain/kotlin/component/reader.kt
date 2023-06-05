package component

import react.FC
import react.Props
import react.dom.html.ButtonType
import react.dom.html.FormMethod
import react.dom.html.ReactHTML
import ru.altmanea.webapp.config.Config
import web.html.InputType

val CReader = FC<Props> {
    ReactHTML.div {
        ReactHTML.h3 {
            +"Выберете файл в формате .txt или .bib"
        }

        ReactHTML.form {
            name = "file"
            encType = "multipart/form-data"
            method = FormMethod.post
            action = Config.filePath + "file"
            ReactHTML.div {
                ReactHTML.label {
                    form = "file"
                    +"Выберете файл"
                }
                ReactHTML.input {
                    name = "file"
                    type = InputType.file
                    accept = ".bib, .txt"
                }
                ReactHTML.button {
                    type = ButtonType.submit
                    +"Принять файл"
                }
            }
        }
    }
}