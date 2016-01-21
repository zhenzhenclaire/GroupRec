package com.claire.gmst

import com.claire.util.Config

/**
 * Created by Claire on 1/20/2016.
 */
object test {

  def main(args: Array[String]) {
    Config.init
    val weightcal : weightCal = new weightCal
    weightcal.init()
  }

}
