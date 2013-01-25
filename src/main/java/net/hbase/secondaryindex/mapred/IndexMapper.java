package net.hbase.secondaryindex.mapred;

import java.io.IOException;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Writable;

import net.hbase.secondaryindex.util.Const;

public class IndexMapper extends TableMapper<ImmutableBytesWritable, Writable> {

	// private byte[] columnFamily = null;
	// private byte[] columnqualifier = null;
	private String column = null;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		// columnFamily = Bytes.toBytes(context.getConfiguration().get(
		// "conf.columnfamily"));
		// columnqualifier = Bytes.toBytes(context.getConfiguration().get(
		// "conf.columnqualifier"));
		column = context.getConfiguration().get("conf.column");
	}

	@Override
	public void map(ImmutableBytesWritable row, Result columns, Context context)
			throws IOException {
		String value = null;
		try {
			for (KeyValue kv : columns.list()) {
				value = Bytes.toStringBinary(kv.getValue());
				long ts = kv.getTimestamp();

				byte[] rowkey = row.get();
				byte[] columnFamily = Const.COLUMN_FAMILY_CF1;
				byte[] qualifier = Const.COLUMN_RK;
				if (null != value && value.length() > 0) {
					Put put = new Put(Bytes.toBytes(column + "_" + value), ts);
					put.add(columnFamily, qualifier, rowkey);
					context.write(row, put);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage() + ", Row: "
					+ Bytes.toString(row.get()) + ", Value: " + value);
		}
	}

}
