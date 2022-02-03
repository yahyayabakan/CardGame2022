// @GENERATOR:play-routes-compiler
// @SOURCE:D:/UNIVERSITY OF GLASGOW/TEAM PROJECT/ITSD-DT2022-Template/conf/routes
// @DATE:Thu Feb 03 13:13:59 GMT 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
