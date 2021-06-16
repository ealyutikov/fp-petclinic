package com.petclinic.model

import com.petclinic.model.BuildInfoResponse._
import io.circe.generic.JsonCodec

@JsonCodec
final case class BuildInfoResponse(name: String, version: AppVersion, build: BuildVersion)

object BuildInfoResponse {
  @JsonCodec
  final case class AppVersion(value: String)

  @JsonCodec
  final case class BuildVersion(number: Int, branch: String, commit: String, time: String)
}
