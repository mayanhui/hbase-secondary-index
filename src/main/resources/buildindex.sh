#!/bin/bash
hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i user_behavior_attribute_noregistered -o user_behavior_attribute_noregistered_index -c bhvr:vvmid
