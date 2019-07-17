package com.spark.sparksql.log.dao

import java.sql.{Connection, PreparedStatement}

import com.spark.sparksql.log.MySQLUtils
import com.spark.sparksql.log.entity.{DayCityVideoAccessStat, DayVideoAccessStat, DayVideoTrafficsStat}

import scala.collection.mutable.ListBuffer

/**
  * @author LTong
  * @date 2019-06-17 下午 2:24
  */
object StatDao {

  /**
    * 批量保存数据到数据库中
    */

  def insertDayVideoAccessTopN(list:ListBuffer[DayVideoAccessStat]) ={
     var conn:Connection = null
     var ppst:PreparedStatement = null

    try{
      conn = MySQLUtils.getConnection()
          //设置手动提交
      conn.setAutoCommit(false)
      val sql = "insert into day_video_access_topn_stat(day,cms_id,times) values (?,?,?)"
      ppst = conn.prepareStatement(sql)

      for(e <- list){
        ppst.setString(1,e.day)
        ppst.setLong(2,e.cmsId)
        ppst.setLong(3,e.times)
        ppst.addBatch()
      }
      //批量执行处理
      ppst.executeBatch()
      //手工提交
      conn.commit()
    }catch {
      case e:Exception =>e.printStackTrace()
    }finally {
      MySQLUtils.release(conn, ppst)
    }

  }

  /**
    * 按照地市进行统计TopN课程
    *
    * @param list
    */
  def insertDayCityVideoAccessTopN(list: ListBuffer[DayCityVideoAccessStat]): Unit = {
    var connection: Connection = null
    var pstmt: PreparedStatement = null

    try {
      connection = MySQLUtils.getConnection()
      //设置手动提交
      connection.setAutoCommit(false)
      val sql = "insert into day_video_city_access_topn_stat(day,cms_id,city,times,times_rank) values (?,?,?,?,?)"
      pstmt = connection.prepareStatement(sql)

      for (ele <- list) {
        pstmt.setString(1, ele.day)
        pstmt.setLong(2, ele.cmsId)
        pstmt.setString(3, ele.city)
        pstmt.setLong(4, ele.times)
        pstmt.setInt(5, ele.timesRank)

        pstmt.addBatch()
      }

      //执行批量处理
      pstmt.executeBatch()
      //手动提交
      connection.commit()
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtils.release(connection, pstmt)
    }
  }

  /**
    * 批量保存DayVideoTrafficsStat到数据库
    *
    * @param list
    */
  def insertDayVideoTrafficsAccessTopN(list: ListBuffer[DayVideoTrafficsStat]): Unit = {
    var connection: Connection = null
    var pstmt: PreparedStatement = null
    try {
      connection = MySQLUtils.getConnection()
      //设置手动提交
      connection.setAutoCommit(false)

      val sql = "insert into day_video_traffics_access_topn_stat (day,cms_id,traffics) values (?,?,?)"
      pstmt = connection.prepareStatement(sql)
      for (ele <- list) {
        pstmt.setString(1, ele.day)
        pstmt.setLong(2, ele.cmsId)
        pstmt.setLong(3, ele.traffics)
        pstmt.addBatch()
      }
      //执行批量处理
      pstmt.executeBatch()
      //手工提交
      connection.commit()

    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtils.release(connection, pstmt)
    }
  }

  /**
    * 删除指定日期的数据
    *
    * @param day
    */
  def deleteData(day: String): Unit = {
    val tables = Array("day_video_access_topn_stat",
      "day_video_city_access_topn_stat",
      "day_video_traffics_access_topn_stat")
    var connection: Connection = null
    var pstmt: PreparedStatement = null

    try {
      connection = MySQLUtils.getConnection()
      for (table <- tables) {
        val deleteSql = s"delete from $table where day = ?"
        pstmt = connection.prepareStatement(deleteSql)
        pstmt.setString(1, day)
        pstmt.executeUpdate()
      }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      MySQLUtils.release(connection, pstmt)
    }
  }


}
