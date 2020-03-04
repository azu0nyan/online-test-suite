package frontend.css

object TopMenuCss {

  def getCss(outerClass:String, activeClassName:String = "active"):String =
    s"""
       |
       |.$outerClass ul {
       |  list-style-type: none;
       |  margin: 0;
       |  padding: 0;
       |  overflow: hidden;
       |  background-color: ${Styles.topRowColor};
       |  display: flex;
       |  flex-direction: row;
       |  flex-wrap: nowrap;
       |  justify-content: left;
       |  height: 100%;
       |}
       |
       |
       |.$outerClass li {
       |  float: left;
       |  display: flex;
       |  flex-direction: column;
       |  justify-content: flex-end;
       |  color: white;
       |  text-align: center;
       |  padding: ${Styles.defaultPadding};
       |  text-decoration: none;
       |}
       |
       |.$outerClass li:hover:not(.$activeClassName) {
       |  background-color: ${Styles.topMenuHoverColor};
       |}
       |
       |.$outerClass .$activeClassName {
       |  background-color: ${Styles.topRowActiveColor};
       |  border-bottom: solid;
       |  border-bottom-color: ${Styles.contentBgColor};
       |  border-bottom-width: 10px;
       |}
       |""".stripMargin

}
