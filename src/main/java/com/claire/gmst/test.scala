package com.claire.gmst

import com.claire.gmst.preferenceCal
import com.claire.util.Config

/**
 * Created by Claire on 1/20/2016.
 */
object test {

  def main(args: Array[String]) {
    Config.init
    val preferenceCal : preferenceCal = new preferenceCal
    preferenceCal.init()
  }

}
