package com.petclinic.model

import com.petclinic.util.aliases.NonBlankString
import eu.timepit.refined.types.numeric.PosLong

final case class Specialty(id: PosLong, name: NonBlankString)
