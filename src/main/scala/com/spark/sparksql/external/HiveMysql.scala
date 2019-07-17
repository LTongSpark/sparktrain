package com.spark.sparksql.external

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @author LTong
  * @date 2019-06-17 上午 11:09
  *  使用外部数据源综合查询hive和mysql的数据
  */
class HiveMysql {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("HiveMySQLApp").master("local[2]").getOrCreate()
    val hivetable = spark.table("emp")

    val mysqltable = spark.read.format("jdbc").option("url","jdbc:mysql://localhost:3306").
      option("driver","com.mysql.jdbc.Driver").
      option("user","root").option("password",1234).option("dbtable","datacube.DEPT").load()

    //join

    val dataFrame: DataFrame = hivetable.join(mysqltable ,hivetable.col("id")=== mysqltable.col("id"))

    dataFrame.show()

    dataFrame.select(hivetable.col("empno"), hivetable.col("ename"), mysqltable.col("deptno"), mysqltable.col("dname")).show
    spark.stop()
  }

}
