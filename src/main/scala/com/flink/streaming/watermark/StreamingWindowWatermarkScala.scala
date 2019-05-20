package com.flink.streaming.watermark

import java.text.SimpleDateFormat

import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import scala.collection.mutable.ArrayBuffer
import scala.util.Sorting

/**
  * Watermark 案例
  *
  */
object StreamingWindowWatermarkScala {

  def main(args: Array[String]): Unit = {
      //定义socket的端口号
    val port = 9000
      //获取运行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    import org.apache.flink.api.scala._

      //设置使用eventtime，默认是使用processtime
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
      //设置并行度为1,默认并行度是当前机器的cpu数量
    env.setParallelism(1)
      //连接socket获取输入的数据
    val text = env.socketTextStream("hadoop100",port,'\n')
      //解析输入的数据
    val inputMap = text.map(line=>{
      val arr = line.split(",")
      (arr(0),arr(1).toLong)
    })
      //抽取timestamp和生成watermark
    val waterMarkStream = inputMap.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks[(String, Long)] {
      var currentMaxTimestamp:Long = 0L
      var maxOutOfOrderness:Long = 10000L// 最大允许的乱序时间是10s

      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        /**
          * 定义生成watermark的逻辑
          * 默认100ms被调用一次
          */
      override def getCurrentWatermark = new Watermark(currentMaxTimestamp - maxOutOfOrderness)

        //定义如何提取timestamp
      override def extractTimestamp(element: (String, Long), previousElementTimestamp: Long) = {
        val timestamp = element._2
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp)
//        val id = Thread.currentThread().getId
//        println("currentThreadId:"+id+",key:"+element._1+",eventtime:["+element._2+"|"+
//            sdf.format(element._2)+"],currentMaxTimestamp:["+currentMaxTimestamp+"|"+
//            sdf.format(currentMaxTimestamp)+"],watermark:["+getCurrentWatermark().getTimestamp+"|"+
//            sdf.format(getCurrentWatermark().getTimestamp)+"]")
        timestamp
      }
    })

      //对window内的数据进行排序，保证数据的顺序
    val window = waterMarkStream.keyBy(0)
      .window(TumblingEventTimeWindows.of(Time.seconds(3))) //按照消息的EventTime分配窗口，和调用TimeWindow效果一样
        //全量数据聚合
      .apply(new WindowFunction[Tuple2[String, Long], String, Tuple, TimeWindow] {
      override def apply(key: Tuple, window: TimeWindow, input: Iterable[(String, Long)], out: Collector[String]) = {
        val keyStr = key.toString
        val arrBuf = ArrayBuffer[Long]()
        val ite = input.iterator
        while (ite.hasNext){
          val tup2 = ite.next()
          arrBuf.append(tup2._2)
        }

        val arr = arrBuf.toArray
        Sorting.quickSort(arr)

        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        val result = keyStr + "," + arr.length + "," + sdf.format(arr.head) + "," +
            sdf.format(arr.last)+ "," + sdf.format(window.getStart) + "," + sdf.format(window.getEnd)
        out.collect(result)
      }
    })

    window.print()
      //注意：因为flink是懒加载的，所以必须调用execute方法，上面的代码才会执行
    env.execute("StreamingWindowWatermarkScala")

  }



}
