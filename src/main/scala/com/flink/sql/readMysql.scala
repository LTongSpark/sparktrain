package com.flink.sql

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, _}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}
import org.apache.flink.types.Row

/**
  * Project: sparktrain
  * ClassName: com.flink.sql.readMysql
  * Version: V1.0
  *
  * Author: LTong
  * Date: 2019-07-02 下午 1:37
  */
object readMysql {
  def main(args: Array[String]): Unit = {
    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
   val env1 =  StreamExecutionEnvironment.getExecutionEnvironment()

    //读取mysql数据
    val data : DataSet[Row] = env.createInput(
      JDBCInputFormat.buildJDBCInputFormat()
        .setDrivername("com.mysql.jdbc.Driver")
        .setUsername("root")
        .setPassword("root")
        .setDBUrl("jdbc:mysql://localhost:3306/spark_home")
        .setQuery("select * from wordcount")
        .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO ,BasicTypeInfo.INT_TYPE_INFO))
        .finish()
    )



  }
}
