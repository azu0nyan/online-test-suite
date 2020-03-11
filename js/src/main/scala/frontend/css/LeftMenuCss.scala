package frontend.css

object LeftMenuCss {

  val scrollbarWidth = "20px"
  def getCss(outerClass: String, activeClassName: String = "active"): String =
    s"""
       |/*scroll bar*/
       |.$outerClass  ul::-webkit-scrollbar {
       |  width: $scrollbarWidth;
       |}
       |
       |.$outerClass  ul::-webkit-scrollbar-thumb {
       |    height: 6px;
       |  /*  border: 4px solid rgba(0, 0, 0, 0);*/
       |   /* background-clip: padding-box;*/
       |   /* -webkit-border-radius: 7px;*/
       |    background-color: ${Styles.leftMenuScrollbarColor};
       |   /* -webkit-box-shadow: inset -1px -1px 0px rgba(0, 0, 0, 0.05), inset 1px 1px 0px rgba(0, 0, 0, 0.05);*/
       |}
       |.$outerClass  ul::-webkit-scrollbar-button {
       |    width: 0;
       |    height: 0;
       |    display: none;
       |}
       |.$outerClass  ul::-webkit-scrollbar-corner {
       |    background-color: transparent;
       |}
       |
       |
       |.$outerClass  ul {
       |  scrollbar-width: $scrollbarWidth;
       |  scrollbar-color: ${Styles.leftMenuScrollbarColor} rgba(0,0,0,0);
       |}
       |/*end scrollbar*/
       |
       |
       |.$outerClass ul {
       |    display: flex;
       |    flex-direction: column;
       |    flex-wrap: nowrap;
       |    justify-content: flex-start;
       |    height: 100%;
       |    margin: 0;
       |    padding: 0;
       |    list-style-type: none;
       |    overflow: hidden;
       |    overflow-y:scroll;
       |    direction: rtl;/*for scrollbar*/       |
       |    background-color: ${Styles.leftMenuBGColor};
       |
       |
       |}
       |.$outerClass li{
       |    float: left;
       |    display: flex;
       |    flex-direction: row;
       |    justify-content: flex-start;
       |    text-align: center;
       |    color: white;
       |    padding: ${Styles.defaultPadding};
       |    text-decoration: none;
       |    direction: ltr;/*cancel for scrollbar*/
       |    background-color: ${Styles.leftMenuBGColor};
       |}
       |
       |.$outerClass li:hover:not(.$activeClassName) {
       |  background-color: ${Styles.topMenuHoverColor};
       |}
       |
       |.$outerClass .$activeClassName {
       |  background-color: ${Styles.leftMenuActiveColor};
       |  border-right: solid;
       |  border-right-color: ${Styles.contentBgColor};
       |  border-right-width: 20px;
       |}
       """.stripMargin

}
