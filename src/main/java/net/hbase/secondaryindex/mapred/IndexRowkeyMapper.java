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

public class IndexRowkeyMapper extends
		TableMapper<ImmutableBytesWritable, Writable> {

	// private byte[] columnFamily;
	// private byte[] columnQualifier;
	//
	private String column;
	private String rowkeyFields;

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		column = context.getConfiguration().get(Const.HBASE_CONF_COLUMN_NAME);
		rowkeyFields = context.getConfiguration().get(
				Const.HBASE_CONF_ROWKEY_NAME);
	}

	@Override
	public void map(ImmutableBytesWritable row, Result columns, Context context)
			throws IOException {
		try {
			byte[] rowkey = row.get();
			byte[] cf = Const.COLUMN_FAMILY_CF1;
			byte[] qualifier = null;

			// Get timestamp of validate column
			long ts = System.currentTimeMillis();
			String[] arr = column.split(",", -1);
			if (arr.length == 2) {
				for (String col : arr) {
					for (KeyValue kv : columns.list()) {
						String columnName = Bytes.toString(kv.getFamily())
								+ Const.FAMILY_COLUMN_SEPARATOR
								+ Bytes.toString(kv.getQualifier());
						if (!col.equals(Const.MAPPER_TYPE_ROWKEY)
								&& columnName.equals(col)) {
							ts = kv.getTimestamp();
							break;
						}
					}
				}
			}

			String rowkeyStr = Bytes.toStringBinary(rowkey);

			String[] fieldArr = rowkeyFields.split(",", -1);
			int rorder = -1;
			for (String field : fieldArr) {
				if (field.indexOf(Const.PARAMETER_ISROWKEY) >= 0) {
					String[] farr = field.split(Const.FAMILY_COLUMN_SEPARATOR,
							-1);
					if (farr.length == 2) {
						rorder = Integer.parseInt(farr[1]) - 1;
					}
				}
			}
			int corder = -1;
			for (String field : fieldArr) {
				if (field.indexOf(Const.PARAMETER_ISROWKEY) < 0) {
					String[] farr = field.split(Const.FAMILY_COLUMN_SEPARATOR,
							-1);
					if (farr.length == 2) {
						corder = Integer.parseInt(farr[1]) - 1;
						if (corder != rorder)
							qualifier = Bytes.toBytes(farr[0]);
					}
				}
			}

			if (rorder < 0 || corder < 0)
				return;

			String[] rowkeyArr = rowkeyStr.split(
					Const.ROWKEY_DEFAULT_SEPARATOR, -1);
			if (rowkeyArr.length > rorder && rowkeyArr.length > corder) {
				Put put = new Put(Bytes.toBytes(rowkeyArr[rorder]), ts);
				put.add(cf, qualifier, Bytes.toBytes(rowkeyArr[corder]));
				context.write(row, put);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error: " + e.getMessage() + ", Row: "
					+ Bytes.toString(row.get()));
		}
	}

}
