package app

import controller.TemplatesRegistry
import impl.BinaryCountingOfAncientRussians

object App {
  def main(args: Array[String]): Unit = {

  }

  def initAliaces(): Unit ={
    TemplatesRegistry.registerProblemListTemplate(BinaryCountingOfAncientRussians.template)
  }

}
