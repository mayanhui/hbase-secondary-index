#More details, see wiki page(Chinese wiki 中文使用手册): 
https://github.com/mayanhui/hbase-secondary-index/wiki

###################################################
## Methods of building secondary index ############
###################################################

1.Many ways to build index

1.1 MapReduce

Using integration mapreduce to build hbase index for main table. The main structure is:

(1) scan input table by TableMapper<ImmutableBytesWritable, Writable>

(2) get the rowkey and special colum name and value

(3) create instance of Put with value=rowkey, and rowkey=columnName + "_" +columnValue

(4) use IdentityTableReducer to put data into index table



1.2  

#####################
## MapReduce ########
#####################

2 MapReduce Usage

2.1 Build from source code
Download the source code first and then use maven to build jar.
go into the project and do:

mvn install

Note: You need to install maven >= 2.2.1

2.2 Build index
Use the example of buildindex.sh in directory 'src/main/resources'
Such as:

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i user_behavior_attribute_noregistered -o user_behavior_attribute_noregistered_index -c bhvr:vvmid -s 20130101 -e 20130120 -v 3

The meaning of parameters:

 usage: Build-Secondary-Index  -c <family:qualifier> [-d] [-e <end-date>]
       -i <input-table-name> -o <output-table-name> [-s <start-date>] [-si
       <single-index>] [-v <versions>]

-c,--column <family:qualifier>    column to store row data into (must
                                   exist)

-d,--debug                        switch on DEBUG log level

-e,--edate <end-date>             the end date of data to build
                                   index(default is today), such as:
                                   20130120

-i,--input <input-table-name>     the directory or file to read from
                                   (must exist)

-o,--output <output-table-name>   table to import into (must exist)

-s,--sdate <start-date>           the start date of data to build
                                   index(default is 19700101), such as:
                                   20130101

-si,--sindex <single-index>       if use single index. true means 'single
                                   index', false means 'combined
                                   index'(default is true)

-v,--versions <versions>          the versions of each cell to build
                                   index(default is Integer.MAX_VALUE)
