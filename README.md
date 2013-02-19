#More details, see wiki page(Chinese wiki 中文使用手册): 
https://github.com/mayanhui/hbase-secondary-index/wiki

###################################################
## Methods of building secondary index ############
###################################################

##0.Environment

* hadoop: 1.0.4
* hbase: 0.94.0
* zookeeper: 3.4.3
* hive: 0.9.0
* thrift: 0.9.0


##1.Many ways to build index

###1.1 MapReduce

Using integration mapreduce to build hbase index for main table. The main structure is:

(1) scan input table by TableMapper<ImmutableBytesWritable, Writable>

(2) get the rowkey and special colum name and value

(3) create instance of Put with value=rowkey, and rowkey=columnName + "_" +columnValue

(4) use IdentityTableReducer to put data into index table

Index type support:

1. build single column index

2. build multi single-column index together

3. build combined-column index

4. build json column index. single-field, combined-field index

5. build rowkey only index


Command to build index:

* 1. build single column index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid -s 20130101 -e 20130120 -v 1



* 2. build multi single-column index together

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -s 20130101 -e 20130120 -v 3



* 3. build combined-column index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -si false

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -si false -s 20130101 -e 20130120 -v 1


* 4. build json column index. single-field, combined-field index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category 

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -s 20130101 -e 20130120 -v 1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -si false

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -si false -s 20130101 -e 20130120 -v 1


* 5. build rowkey only index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey -r uid:1,mid:2,isrowkey:1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1 -s 20130101 -e 20130120

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1 -s 20130101 -e 20130120 -v 1



###1.2 ITHBase

$HBASE_HOME/conf/hbase-site.xml：

* hbase.hlog.splitter.impl

org.apache.hadoop.hbase.regionserver.transactional.THLogSplitter

* hbase.regionserver.class

org.apache.hadoop.hbase.ipc.IndexedRegionInterface

* hbase.regionserver.impl

org.apache.hadoop.hbase.regionserver.tableindexed.IndexedRegionServer

* hbase.hregion.impl

org.apache.hadoop.hbase.regionserver.tableindexed.IndexedRegion




###1.3 IHBase



###1.4 Coprocessor




#####################
## MapReduce ########
#####################

##2 MapReduce Usage

###2.1 Build from source code
Download the source code first and then use maven to build jar.
go into the project and do:

mvn install

Note: You need to install maven >= 2.2.1

###2.2 use jar
You can see the jar file in root directory of project:
hbase-secondary-index-0.1.jar
You can use it directly!

###2.3 Build index
Use the example of buildindex.sh in directory 'src/main/resources'
Such as:

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i user_behavior_attribute_noregistered -o user_behavior_attribute_noregistered_index -c bhvr:vvmid -s 20130101 -e 20130120 -v 3

usage: Build-Secondary-Index  -c <family:qualifier> [-d] [-e <end-date>]
       -i <input-table-name> [-j <json fields>] -o <output-table-name> [-r
       <rowkey fields>] [-s <start-date>] [-si <single-index>] [-v
       <versions>]

 -c,--column <family:qualifier>    column to store row data into (must
                                   exist). Such as:
                                   cf1:age,cf2:tag,cf2:msg  or rowkey or
                                   rowkey,cf1:age. The last two usage are
                                   for 'rowkey' index building.

 -d,--debug                        switch on DEBUG log level

 -e,--edate <end-date>             the end date of data to build
                                   index(default is today), such as:
                                   20130120

 -i,--input <input-table-name>     the directory or file to read from
                                   (must exist)

 -j,--json <json fields>           json fields to build index. The max
                                   number of fields is 3! This kind of
                                   data uses IndexJsonMapper.class.

 -o,--output <output-table-name>   table to import into (must exist)

 -r,--rowkey <rowkey fields>       rowkey fields to build index. The max
                                   number of fields is 2! This kind of
                                   data uses IndexRowkeyMapper.class. The
                                   format is: uid:1,msgid:2,isrowkey:1
                                   uid and msgid are the field name, 1 and
                                   2 is the order in the rowkey(like:
                                   uid_msgid_ts). isrowkey is the label to
                                   define which field is the new rowkey.
                                   The separator in rowkey is _ . You can
                                   use validate column to build
                                   incremental index. If use validate
                                   column, you need to add a column to -c
                                   parameter, the -c should be
                                   'rowkey,cf1:age'

 -s,--sdate <start-date>           the start date of data to build
                                   index(default is 19700101), such as:
                                   20130101

 -si,--sindex <single-index>       if use single index. true means 'single
                                   index', false means 'combined
                                   index'(default is true). If build
                                   combined index, the max number of
                                   columns is 3.

 -v,--versions <versions>          the versions of each cell to build
                                   index(default is Integer.MAX_VALUE)

