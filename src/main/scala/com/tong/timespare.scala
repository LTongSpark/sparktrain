package com.tong

import java.util.{Date, Locale}
import org.apache.commons.lang3.time.FastDateFormat

/**
  * @author LTong
  * @date 2019-06-16 下午 3:23
  */
object timespare {

  //原始固定时期
  val yyyyMMddHHmmss_format = FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z",Locale.ENGLISH)
  //解析后的时间

  val yyyyMMddHHmmss = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

  def parseTime(time:String) ={
    yyyyMMddHHmmss.format(new Date(getTime(time)))
  }

  def getTime(time:String) :Long ={
    try {
      yyyyMMddHHmmss_format.parse(time.substring(time.indexOf("[") + 1 ,time.lastIndexOf("]"))).getTime
      yyyyMMddHHmmss_format.parse(time.substring(time.indexOf("[") + 1 ,time.lastIndexOf("]"))).getDate
    }catch {
      case e:Exception =>{
        0l
      }
    }
  }
  def main(args: Array[String]): Unit = {
    //print(parseTime("[10/Nov/2018:00:00:03 +0800]"))
    print(getTime("[10/Nov/2018:00:00:03 +0800]"))
  }
}
