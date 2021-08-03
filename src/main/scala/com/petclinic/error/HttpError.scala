package com.petclinic.error

sealed trait HttpError extends Product with Serializable

object HttpError {
  final case class NotFound(what: String)          extends HttpError
  final case class Internal(what: String)          extends HttpError
  final case class Unauthorized(realm: String)     extends HttpError
  final case class Unknown(code: Int, msg: String) extends HttpError
  final case object NoContent                      extends HttpError
}
