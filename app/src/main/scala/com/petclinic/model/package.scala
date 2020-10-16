package com.petclinic

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string.Trimmed

package object model {

  private type NonBlank = NonEmpty And Trimmed

  type NonBlankString = String Refined NonBlank

}
