package com.parkour.save;

import java.util.HashMap;

public class ArgsOutput{
	private HashMap<String, String> keysValues;
	
	ArgsOutput(HashMap<String, String> keysValues){
		this.keysValues = keysValues;
	}
	
	public String get(String key) {
		String out = keysValues.get(key);
		return out == null ? "null" : out;
	}
	
	public String get(String key, String outDefault) {
		String out = keysValues.get(key);
		return out == null ? outDefault : out;
	}
	
	public boolean has(String key) {
		return !get(key).equals("null");
	}
	
	public boolean hasAll(String... key) {
		for(String k : key) {
			if(get(k).equals("null")) return false;
		}
		return true;
	}
	
}
