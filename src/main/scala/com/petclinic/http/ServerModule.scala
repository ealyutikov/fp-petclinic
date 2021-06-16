package com.petclinic.http

import distage.{ModuleDef, TagK}

final class ServerModule[I[_] : TagK] extends ModuleDef {
  make[HttpServer[fs2.Stream[I, *]]].fromResource[HttpServer.Maker[I]]
}
