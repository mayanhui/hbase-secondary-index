package net.hbase.secondaryindex;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;


public class TestSyslog {
	public static void main(String args[]) throws Exception {
//		DataInputStream din = new DataInputStream(new BufferedInputStream(System.in));
//		String line = null;
//		List<String> list = new ArrayList<String>();
//		while (null != (line = din.readLine())) {
//			list.add(line);
//			System.out.println("list.size(): " + list.size());
//			if(list.size() >= 20){
//				System.out.println(list.get(0));
//				list =  new ArrayList<String>();
//			}
//		}
		
//		long i = 1123304L;
////		Bytes.toBytes(i);
//		int count = Bytes.toInt(Bytes.toBytes(i));
//		long count = System.currentTimeMillis();
//		System.out.println(count);
//		long l = 3600 * 1000 * 24 * 150L;
//		count = System.currentTimeMillis() - l;
//		
//		System.out.println(count);
//		
//		count = 1;
//		int i = 1;
//		System.out.println(count+1);
//		System.out.println(++i);
//		
//		System.out.println(DateFormatUtil.parseToStringDate(DateFormatUtil.formatStringTimeToLong("2013-01-20 09:56:01 p.m.asdfasdf")));
//		
//		
//		String t = "FALSE";
//		System.out.println(Boolean.parseBoolean(t));

		String str = "bhvr:vvmid";
		str = "bhvr:vvmid,bhvr:movt,bhvr:movt";
		if(str.matches(".*?:{1}.*?")){
			System.out.println("Hit");
		}else
			System.out.println("No");
		
	}
}
