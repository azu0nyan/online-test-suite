package controller

object Validator {

  def validateAnswer(alias:String, seed:Int, answer:String) = {
    val pt = TemplatesRegistry.getProblemTemplate(alias)
    pt.map{ pt => pt.validateAnswer(seed, answer)}
  }

}
