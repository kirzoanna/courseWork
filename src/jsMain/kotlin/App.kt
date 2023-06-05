import component.CReader
import component.View
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.body
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.style
import react.dom.html.ReactHTML.ul
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import tanstack.query.core.QueryClient
import tanstack.react.query.QueryClientProvider
import web.dom.document

fun main() {
    val container = document.getElementById("root")!!
    createRoot(container).render(app.create())
}

val app = FC<Props>("App") {
    HashRouter {
        QueryClientProvider {
            client = QueryClient()
            body {
                style {
                    +"""table {
        border-collapse: collapse;
        width: 100%;
      }
      tr {
        background-color: #f5f5f5;
      }
      th,
      td {
        padding: 15px;
        text-align: left;
        border-bottom: 1px solid #ccc;
      }
      tr:hover {
        background-color: #cdcdcd;
      }
      li {
          padding-bottom: 10px;
      }
      
      """
                }
            }
            Routes {
                Route {
                    path = "/"
                    element = CMain.create()
                }
                Route {
                    path = "/reader"
                    element = CReader.create()
                }
                Route {
                    path = "/view"
                    element = View.create()
                }
            }
        }
    }
}

val CMain = FC<Props> {
    h3{
        +"Работа с Bibtex"
    }
    div {
        ul {
            li {
                ReactHTML.style {
                    + """"""
                }
                Link {
                    +"Загрузка файла"
                    to = "reader/"
                }
            }
            li {
                Link {
                    +"Просмотр загрузок"
                    to = "view/"
                }
            }
        }
    }
}


