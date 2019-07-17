package com.spark.sparksql.log

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
  * @author LTong
  * @date 2019-06-17 下午 12:30
  */
object MySQLUtils {

  /**
    * 获取数据库连接
    */

  def getConnection(): Connection ={
    DriverManager.getConnection("jdbc:mysql://192.168.1.112:3306/sparksqlproject",
    "root",
    "1234")
  }

  /**
    * 数据库连接资源
    */

  def release(conn:Connection,ppst:PreparedStatement) ={
    try{
      if(ppst != null) ppst.close()
    }catch {
      case e:Exception => e.printStackTrace()
      }finally {
      if(conn!=null) conn.close()
    }
  }

}
