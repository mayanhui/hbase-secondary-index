package net.hbase.secondaryindex.util;

import org.apache.hadoop.hbase.util.Bytes;

public class Const {
	public static final String HBASE_TABLE_NAME = "hbase.tablename";
	public static final int MAX_INPUT_SPLIT_SIZE = 50 * 1000 * 1000;// MB
	public static final int MIN_INPUT_SPLIT_SIZE = 512 * 1000 * 1000;// MB

	/* Column Family */
	public static final byte[] COLUMN_FAMILY_CF1 = Bytes.toBytes("cf1");

	/* Column */
	public static final byte[] COLUMN_RK = Bytes.toBytes("rk");

	/* hbase conf */
	public static final String HBASE_CONF_COLUMN_NAME = "conf.column";
	public static final String HBASE_CONF_ISBUILDSINGLEINDEX_NAME = "conf.isbuildsingleindex";

	public static final String ROWKEY_DEFAULT_SEPARATOR = "_";
	public static final String FAMILY_COLUMN_SEPARATOR = ":";
    
	
}
