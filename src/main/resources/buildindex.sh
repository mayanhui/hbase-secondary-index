#!/bin/bash
hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i user_behavior_attribute_noregistered -o user_behavior_attribute_noregistered_index -c bhvr:vvmid -s 20130101 -e 20130120 -v 3


