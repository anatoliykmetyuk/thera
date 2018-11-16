package thera.runtime

import io.circe.Json
import cats._, cats.implicits._, cats.data._, cats.effect._

sealed trait Runtime {
  def as[T](name: String)(implicit m: Manifest[T]): T =
    if (m.runtimeClass.equals(this.getClass)) this.asInstanceOf[T]
    else throw new RuntimeException(s"$this is not a $name")

  def asFunc  : Function = as[Function]("function")
  def asText  : Text     = as[Text    ]("text"    )
  def asData  : Data     = as[Data    ]("data"    )

  def evalThunk: Ef[Runtime] = this match {
    case Function(f, true) => f(Nil) >>= (_.evalThunk)
    case x => State.pure(x)
  }
}

case class Text(value: String) extends Runtime
case class Data(value: Json  ) extends Runtime
case class Function(f: Args => Ef[Runtime], zeroArity: Boolean = false) extends Runtime with Function1[Args, Ef[Runtime]] {
  def apply(as: Args): Ef[Runtime] = f(as)
}

object Runtime {
  implicit val monoid: Monoid[Runtime] = new Monoid[Runtime] {
    def combine(x: Runtime, y: Runtime): Runtime = (x, y) match {
      case (Text(s1), Text(s2)) => Text(s1 + s2)
      case _ => throw new RuntimeException(s"Attempt to combine $x and $y failed. It is only possible to combine strings at the moment.")
    }

    def empty: Runtime = Text("")
  }
}
