package com.spark.sparksql.log

/**
  * @author LTong
  * @date 2019-06-17 上午 11:52
  */
import java.util.{Date, Locale}

import org.apache.commons.lang3.time.FastDateFormat


/**
  * 日期时间解析工具类
  * 注意：SimpleDateFormat是线程不安全
  * Created by kilo on 2018/3/16.
  */
object DateUtils {
  //输入文件日期时间格式
  //10/Nov/2016:00:01:02 +0800
  val YYYYMMDDHHMM_TIME_FORMAT = FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH)

  //目标日期格式
  val TARGET_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")

  /**
    * 获取时间：yyyy-MM-dd HH:mm:ss
    *
    * @param time
    * @return
    */
  def parse(time: String) = {
    TARGET_TIME_FORMAT.format(new Date(getTime(time)))
  }

  /**
    * 获取输入日志时间：
    * 返回long类型
    *
    * @param time
    * @return
    */
  def getTime(time: String) = {
    try {
      YYYYMMDDHHMM_TIME_FORMAT.parse(time.substring(time.indexOf("[") + 1, time.lastIndexOf("]"))).getTime
    } catch {
      case e: Exception => {
        0l
      }
    }
  }


  def main(args: Array[String]): Unit = {
    println(parse("[10/Nov/2016:00:01:02 +0800])"))
  }
}
