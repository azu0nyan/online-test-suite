package controller


import extensionsInterface.{ProblemSetTemplate, ProblemTemplate}

object TemplatesRegistry {

  def getProblemTemplate(alias:String):Option[ProblemTemplate] = None

  def getProblemSetTemplate(alias:String):Option[ProblemSetTemplate] = None


}
