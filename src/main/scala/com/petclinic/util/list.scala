package com.petclinic.util

object list {
  final implicit class ToListOps[A, B](private val list: List[(A, B)]) extends AnyVal {
    @inline def toMapList: Map[A, List[B]] =
      list.groupBy(_._1).view.mapValues(_.map(_._2)).toMap
  }
}
