package flink_demo

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * Author:   LTong
  * Date:     2019-05-09 14:31
  */
object socketToWordcount {
    def main(args: Array[String]): Unit = {
        //获取端口号
       val port:Int =try {
           ParameterTool.fromArgs(args).getInt("port")
       }catch {
           case e:Exception =>{
               print("host not find")
           }
               9000
       }
        import org.apache.flink.api.scala._
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

        //链接socket获取数据

        val text: DataStream[String] = env.socketTextStream("hadoop121", port)

        //解析数据
        text.flatMap(line =>line.split("\\s")).map((_,1))
            .keyBy(0).timeWindow(Time.seconds(2), Time.seconds(1))
            .sum(1)
    }

}
