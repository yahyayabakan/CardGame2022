// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/Karthik/Downloads/ITSD-DT2022-Template/conf/routes
// @DATE:Fri Feb 04 17:51:30 GMT 2022


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
