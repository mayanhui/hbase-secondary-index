package net.hbase.secondaryindex.mapred;

import net.hbase.secondaryindex.util.ConfigFactory;
import net.hbase.secondaryindex.util.ConfigProperties;
import net.hbase.secondaryindex.util.Const;
import net.hbase.secondaryindex.util.DateFormatUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.IdentityTableReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

public class Main {
	static final Log LOG = LogFactory.getLog(Main.class);

	public static final String NAME = "Build-Secondary-Index";

	static ConfigProperties config = ConfigFactory.getInstance()
			.getConfigProperties(ConfigFactory.BULK_IMPORT_CONFIG_PATH);

	public static void main(String[] args) throws Exception {
		Configuration conf = HBaseConfiguration.create();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		CommandLine cmd = parseArgs(otherArgs);
		String outputTable = cmd.getOptionValue("o");
		String inputTable = cmd.getOptionValue("i");
		String column = cmd.getOptionValue("c");
		String[] arr = column.split(",", -1);
		String mapperType = null;
		conf.set(Const.HBASE_CONF_COLUMN_NAME, column);
		if (column.indexOf(":") < 0 && column.indexOf(",") < 0)
			throw new Exception(
					"Column is not invalid! such as: family1:qualifier1,family2:qualifier2");

		String startDateStr = cmd.getOptionValue("s");
		long startDate = -1L;
		if (null != startDateStr && startDateStr.length() > 0) {
			if (startDateStr.length() == 8) {
				startDate = DateFormatUtil
						.formatStringTimeToLong2(startDateStr);
			} else
				throw new Exception(
						"start-date format is invalid. must be 20130102");
		}

		String endDateStr = cmd.getOptionValue("e");
		long endDate = System.currentTimeMillis();
		if (null != endDateStr && endDateStr.length() > 0) {
			if (endDateStr.length() == 8) {
				endDate = DateFormatUtil.formatStringTimeToLong2(endDateStr);
			} else
				throw new Exception(
						"end-date format is invalid. must be 20130102");
		}

		String versionStr = cmd.getOptionValue("v");
		int versions = Integer.MAX_VALUE;
		if (null != versionStr && versionStr.length() > 0) {
			versions = Integer.parseInt(versionStr);
		}

		String singleIndex = cmd.getOptionValue("si");
		boolean isBuildSingleIndex = true;
		if (null != singleIndex && singleIndex.length() > 0) {
			isBuildSingleIndex = Boolean.parseBoolean(singleIndex);
		}

		/* Max columns is 3 to build combined index! */
		if (!isBuildSingleIndex) {
			if (arr.length > 3 || arr.length < 2) {
				throw new Exception(
						"The max number of column for building 'combined index' is 3 and the min number is 2! [2,3]");
			}
		}

		conf.setBoolean(Const.HBASE_CONF_ISBUILDSINGLEINDEX_NAME,
				isBuildSingleIndex);

		String json = cmd.getOptionValue("j");
		if (null != json && json.length() > 0) {
			if (arr.length > 1) {
				throw new Exception(
						"You are using the '-j' or '--json' option for building index for json field, so the '-c' or '--column' must contain only 1 column(json column)!");
			}
			String[] jarr = json.split(",", -1);
			if (jarr.length < 1 || jarr.length > 3) {
				throw new Exception("The input json field is between [1,3].");
			}

			mapperType = Const.MAPPER_TYPE_JSON;
			conf.set(Const.HBASE_CONF_JSON_NAME, json);
		}

		Scan scan = new Scan();
		if (column != null) {
			if (null != arr && arr.length > 0) {
				for (String col : arr) {
					byte[][] colkey = KeyValue.parseColumn(Bytes.toBytes(col));
					if (colkey.length > 1) {
						scan.addColumn(colkey[0], colkey[1]);
					} else {
						scan.addFamily(colkey[0]);
					}
				}
			}
			scan.setTimeRange(startDate, endDate);
			scan.setMaxVersions(versions);
		}

		LOG.info("Build hbase secondary index. From table{" + inputTable
				+ "} to table{" + outputTable + "} with condition: \ncolumns="
				+ column + "\nstartdate=" + startDate + "\nendate=" + endDate
				+ "\nversions=" + versions + "\ninBuildSingleIndex="
				+ isBuildSingleIndex + "\njson=" + json);

		// hbase master
		conf.set(ConfigProperties.CONFIG_NAME_HBASE_MASTER,
				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_MASTER));
		// zookeeper quorum
		conf.set(
				ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM,
				config.getProperty(ConfigProperties.CONFIG_NAME_HBASE_ZOOKEEPER_QUORUM));

		Job job = new Job(conf, "Build hbase secodary index in " + inputTable
				+ ", write to " + outputTable);
		job.setJarByClass(Main.class);
		TableMapReduceUtil.initTableMapperJob(inputTable, scan,
				MapperWrapper.wrap(mapperType), ImmutableBytesWritable.class,
				Put.class, job);
		TableMapReduceUtil.initTableReducerJob(outputTable,
				IdentityTableReducer.class, job);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	private static CommandLine parseArgs(String[] args) throws ParseException {
		Options options = new Options();
		Option o = new Option("i", "input", true,
				"the directory or file to read from (must exist)");
		o.setArgName("input-table-name");
		o.setRequired(true);
		options.addOption(o);

		o = new Option("o", "output", true, "table to import into (must exist)");
		o.setArgName("output-table-name");
		o.setRequired(true);
		options.addOption(o);

		o = new Option("c", "column", true,
				"column to store row data into (must exist)");
		o.setArgName("family:qualifier");
		o.setRequired(true);
		options.addOption(o);

		o = new Option("s", "sdate", true,
				"the start date of data to build index(default is 19700101), such as: 20130101");
		o.setArgName("start-date");
		o.setRequired(false);
		options.addOption(o);

		o = new Option("e", "edate", true,
				"the end date of data to build index(default is today), such as: 20130120");
		o.setArgName("end-date");
		o.setRequired(false);
		options.addOption(o);

		o = new Option("v", "versions", true,
				"the versions of each cell to build index(default is Integer.MAX_VALUE)");
		o.setArgName("versions");
		o.setRequired(false);
		options.addOption(o);

		o = new Option(
				"si",
				"sindex",
				true,
				"if use single index. true means 'single index', false means 'combined index'(default is true). If build combined index, the max number of columns is 3.");
		o.setArgName("single-index");
		o.setRequired(false);
		options.addOption(o);

		o = new Option(
				"j",
				"json",
				true,
				"json fields to build index. The max number of fields is 3! This kind of data uses IndexJsonMapper.class.");
		o.setArgName("json fields");
		o.setRequired(false);
		options.addOption(o);

		options.addOption("d", "debug", false, "switch on DEBUG log level");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage() + "\n");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(NAME + " ", options, true);
			System.exit(-1);
		}
		return cmd;
	}

}
