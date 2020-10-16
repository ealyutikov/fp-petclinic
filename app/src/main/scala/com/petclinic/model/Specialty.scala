package com.petclinic.model

import eu.timepit.refined.types.numeric.PosLong

final case class Specialty(id: PosLong, name: NonBlankString)