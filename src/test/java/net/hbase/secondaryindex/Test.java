package net.hbase.secondaryindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.hbase.secondaryindex.util.Const;

public class Test {
	public static void main(String args[]) throws Exception {
		String str = "mail";
		System.out.println(str.indexOf(":"));

		List<String> list = new ArrayList<String>();
		List<String> list1 = new ArrayList<String>();
		list1.add("@@");

		for (String s : list) {
			for (String s1 : list1) {
				System.out.println(s + ":" + s1);
			}
		}
		
		String[] arr = new String[]{"1","2","3"};
		List<String> arrList = Arrays.asList(arr);
//		arrList.remove(0);
		System.out.println(arrList);
		
		list = new ArrayList<String>();
		for (String s : arr) {
		    list.add(s);
		}
		list.remove("2");
		System.out.println(list);
		str = "[test start]";
		if(str.startsWith(Const.JSON_ARRAY_START)){
			System.out.println(str);
		}
	}
}
