package net.hbase.secondaryindex.util;

import org.apache.hadoop.hbase.util.Bytes;

public class Const {
	public static final String HBASE_TABLE_NAME = "hbase.tablename";
	public static final int MAX_INPUT_SPLIT_SIZE = 50 * 1000 * 1000;// MB
	public static final int MIN_INPUT_SPLIT_SIZE = 512 * 1000 * 1000;// MB
	
	
	/*Column Family*/
	public static final byte[] COLUMN_FAMILY_CF1 = Bytes.toBytes("cf1");
	
	/*Column*/
	public static final byte[] COLUMN_RK = Bytes.toBytes("rk");

}
