package com.sharavara.mq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

public class Environment implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, Properties> environments;

	public Environment() {
		environments = new HashMap<String, Properties>();
	}

	public void setEnv(String key, Properties env) {
		environments.put(key, env);
	}

	public Properties getEnv(String key) {
		return environments.get(key);

	}

	public boolean delEnv(String key) {
		if (!environments.containsKey(key)) {
			return false;
		}
		environments.remove(key);
		return true;
	}

	public String[] getKeys() {
		String[] keys = null;
		keys = (String[]) environments.keySet().toArray(new String[0]);
		return keys;
	}

}
