package templates.css

object HorizontalMenu {

  def getCss(activeClassName:String = "active"):String =
    s"""
       |ul {
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
       | li {
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
       | li:hover:not(.$activeClassName) {
       |  background-color: ${Styles.topRowHoverColor};
       |}
       |
       |.$activeClassName {
       |  background-color: ${Styles.topRowActiveColor};
       |  border-bottom: solid;
       |  border-bottom-color: ${Styles.contentBgColor};
       |  border-bottom-width: 10px;
       |}
       |""".stripMargin

}
