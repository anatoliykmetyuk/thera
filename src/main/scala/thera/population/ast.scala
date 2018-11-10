package thera.population

import io.circe.Json

object ast {
  sealed trait Node
  case class Tree    (nodes: List[Node]                          ) extends Node
  case class Text    (value: String                              ) extends Node
  case class Function(args : List[String], vars: Json, body: Node) extends Node
  case class Variable(path : List[String]                        ) extends Node
  case class Call    (path : List[String], args: List[Node]      ) extends Node
}