package com.spark.sparksql.log

import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}


/**
  * @author LTong
  * @date 2019-06-17 上午 11:36
  */
object AccessConvertUtil {

  //定义输出的字段 scheam信息

  val struct = StructType(Array(
    StructField("url", StringType),
      StructField("cmsType", StringType),
      StructField("cmsId", LongType),
      StructField("traffic", LongType),
      StructField("ip", StringType),
      StructField("city", StringType),
      StructField("time", StringType),
      StructField("day", StringType)
  ))

  /**
    * 解析字段
    */

  def parse(json:String) ={
    try {

      val splits = json.split("\t")
      val url = splits(1)

      val traffic = splits(2).toLong
      val ip = splits(3)

      val cms = url.substring(url.indexOf("http://www.imooc.com/") + "http://www.imooc.com/".length)

      val cmsTypeid = cms.split("/")

      var cmsType = ""
      var cmsID = 0L
      if(!cmsTypeid.isEmpty && cmsTypeid.length >1){
        cmsType = cmsTypeid(0)
        cmsID = cmsTypeid(1).toLong
      }

      //val city = IpHelper.findRegionByIp(ip)
      val city = ""
      val time = splits(0)
      val day = time.substring(0,10).replaceAll("-" ,"")
      Row(url ,cmsType ,cmsID,traffic ,ip ,city,time ,day)
    }catch {
      case e:Exception =>{
        Row(0)
      }
    }
  }




}
