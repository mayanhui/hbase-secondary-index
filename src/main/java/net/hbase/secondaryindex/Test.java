package net.hbase.secondaryindex;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class Test {
	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws Exception {
		DataInputStream din = new DataInputStream(new BufferedInputStream(
				System.in));
		String line = null;
		List<String> list = new ArrayList<String>();
		while (null != (line = din.readLine())) {
			list.add(line);
			System.out.println("list.size(): " + list.size());
			if (list.size() >= 20) {
				System.out.println(list.get(0));
				list = new ArrayList<String>();
			}
		}

		String str = "mail";
		System.out.println(str.indexOf(":"));

	}
}
