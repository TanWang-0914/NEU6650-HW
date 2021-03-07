import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import org.knowm.xchart.*;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class PlotChart {
    public static void main(String[] args) {

//        double[] throughput = {32, 64, 128, 256,512,1024};
//        double[] meanResponseTime = {3, 4, 7, 10,20,93};
//        // Create Chart
//        XYChart chart = new XYChart(500, 400);
//        chart.setTitle("Thread vs meanResponseTime");
//        chart.setXAxisTitle("Thread");
//        chart.setYAxisTitle("meanResponseTime(millisec)");
//        XYSeries series = chart.addSeries("y(x)", throughput, meanResponseTime);
//        series.setMarker(SeriesMarkers.CIRCLE);
//        new SwingWrapper(chart);
//        try {
//            BitmapEncoder.saveBitmap(chart, "./Part2" + "chart", BitmapEncoder.BitmapFormat.PNG);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        double[] storeNum = {32, 64, 128, 256,512,1024, 2048};
//        double[] wallTime = {16.54, 26.78, 46.96, 80.46,130.399,239.45,536.08};
//        // Create Chart
//        XYChart chart = new XYChart(500, 400);
//        chart.setTitle("Thread vs wallTime");
//        chart.setXAxisTitle("Thread number");
//        chart.setYAxisTitle("WallTime(sec)");
//        XYSeries series = chart.addSeries("y(x)", storeNum, wallTime);
//        series.setMarker(SeriesMarkers.CIRCLE);
//        new SwingWrapper(chart);
//        try {
//            BitmapEncoder.saveBitmap(chart, "./Part1" + "chart", BitmapEncoder.BitmapFormat.PNG);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        long totalReqTime = 0;
        Long startTime;
        List<Integer> responseStartTime = new ArrayList<>();
        List<Integer> responseTimeList = new ArrayList<>();
        int index = 1;
        Map<Integer,Integer> map = new HashMap<>();
        Map<Integer,Integer> count = new HashMap<>();
        int[] storeNum = {32, 64, 128, 256, 512};
        int[] throughput = {2240, 3342, 3808, 3840, 3265 };

//        for (int snum: storeNum){
//            try (CSVReader reader = new CSVReader(new FileReader("./"+snum + "part2.csv"))){
//                String[] line;
//                line = reader.readNext();
//                line = reader.readNext();
//                startTime = Long.valueOf(line[0]);
//
//                while ((line = reader.readNext()) != null){
//                    if (line.length == 4 && line[3].equals("201")){
//                        int key = (int)(Long.valueOf(line[0])-startTime)/1000;
//                        int resTime = Integer.parseInt(line[2]);
//                        map.put(key, map.getOrDefault(key, 0)+resTime);
//                        count.put(key, count.getOrDefault(key, 0)+1);
//                    }
//                }
//                List<Integer> keyList = new ArrayList<>(map.keySet());
//                Collections.sort(keyList);
//                // List<Integer> averageTimeList = new ArrayList<>();
//                List<Integer> numberTimeList = new ArrayList<>();
//                for (int k: keyList){
//                    // System.out.println(map.get(k)+"/" +count.get(k));
//                    // int averageTime = map.get(k)/count.get(k);
//                    // averageTimeList.add(averageTime);
//                    numberTimeList.add(count.get(k));
//                }
//
////                for (int i = 0; i < keyList.size(); i++){
////                    System.out.println(keyList.get(i) + ": " + averageTimeList.get(i));
////                }
////                XYChart chart = new XYChartBuilder().xAxisTitle("RunTime").yAxisTitle("AverageResponseTime").width(1000).height(300).build();
////                XYSeries series = chart.addSeries("Run Time-Average Response Time",keyList,averageTimeList);
////                series.setLineStyle(SeriesLines.SOLID);
//                XYChart chart = new XYChartBuilder().xAxisTitle("RunTime").yAxisTitle("NumbersOfResponseTime").width(1000).height(300).build();
//                XYSeries series = chart.addSeries("Run Time-Numbers Of ResponseTime",keyList,numberTimeList);
//                series.setLineStyle(SeriesLines.SOLID);
//                new SwingWrapper(chart);
//                BitmapEncoder.saveBitmap(chart,"./"+snum + "chart", BitmapEncoder.BitmapFormat.PNG);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        XYChart chart = new XYChartBuilder().xAxisTitle("Store Number").yAxisTitle("Mean Response Time").width(1000).height(300).build();
        XYSeries series = chart.addSeries("Throughput against Threads",storeNum,throughput);
        series.setLineStyle(SeriesLines.SOLID);
        new SwingWrapper(chart);
        try{
            BitmapEncoder.saveBitmap(chart,"./"+"ThroughputLoadBalancer", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
