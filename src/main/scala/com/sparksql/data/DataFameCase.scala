package com.sparksql.data

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @author LTong
  * @date 2019-06-17 上午 10:37
  */
object DataFameCase {
  case class Student(age:Int,name:String)
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession.builder().appName(this.getClass.getSimpleName).master("local[*]").getOrCreate()

    val rdd = spark.sparkContext.textFile("")

    import spark.implicits._
    val student:DataFrame = rdd.map(_.split("\t")).map(line =>Student(line(0).toInt ,line(1))).toDF()
 //转换成df模式
      student.filter("name='' or name ='NULL'").show()
        //    studentDF.filter("SUBSTR(name,0,1)='M'").show()
        //    studentDF.sort("name","id").show()
        //    studentDF.sort(studentDF.col("name").asc,studentDF("id").desc).show()
        //    studentDF.select(studentDF("name").as("student_name")).show()
        //
        //    val studentDF2 = rdd.map(_.split("\\|")).map(line => Student(line(0).toInt, line(1), line(2), line(3))).toDF()
        //
        //    studentDF.join(studentDF2,studentDF.col("id")===studentDF2("id")).show()
        //
        //    studentDF.coalesce(1).write.format("json").mode(SaveMode.Overwrite).save("G:/data/output")
    spark.stop()
  }

}
