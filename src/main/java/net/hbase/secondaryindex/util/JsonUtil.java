package net.hbase.secondaryindex.util;

import net.sf.json.JSONObject;

public class JsonUtil {

	// {'mtype':'iPad2,5','mos':'6.0.2','itime':'2013-01-14 21:30:51'}
	public static String parseItime(String json) {
		String itime = null;
		if (null != json && json.trim().length() > 0) {
			JSONObject jo = JSONObject.fromObject(json);
			itime = (String) jo.get("itime");
		}
		return itime;
	}

	public static void main(String[] args) {
		String json = "{'mtype':'iPad2,5','mos':'6.0.2','itime':'2013-01-14 21:30:51'}";
		String itime = parseItime(json);
		System.out.println(itime);

	}
}
