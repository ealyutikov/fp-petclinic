package com.petclinic.model

import eu.timepit.refined.types.numeric.PosLong

final case class Vet(id: PosLong, firstName: NonBlankString, lastName: NonBlankString, specialties: List[Specialty])

