package controller


import extensionsInterface.{ProblemListTemplate, ProblemTemplate}

object TemplatesRegistry {

  def getProblemTemplate(alias:String):Option[ProblemTemplate] = None

  def getProblemListTemplate(alias:String):Option[ProblemListTemplate] = None


}
