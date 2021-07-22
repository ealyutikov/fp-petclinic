package com.petclinic.error

sealed trait HttpError extends Product with Serializable

object HttpError {
  case class NotFound(what: String)          extends HttpError
  case class Unauthorized(realm: String)     extends HttpError
  case class Unknown(code: Int, msg: String) extends HttpError
  case object NoContent                      extends HttpError
}
