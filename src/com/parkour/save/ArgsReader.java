package com.parkour.save;

import java.util.HashMap;

public class ArgsReader {

	public static ArgsOutput read(String in){
		HashMap<String, String> out = new HashMap<>();
		String key = "";
		String value = "";
		boolean vopen = false;
		for(char c : in.toCharArray()) {
			if(vopen && c == '\'') {
				out.put(key.toLowerCase(), value);	
				value = "";
			}
			if(c == '\'') {
				if(vopen) key = "";
				vopen = !vopen;
				continue;
			}
			if(!vopen && (c == ' ' || c == '=')) continue;
			if(!vopen) key += c; 
			if(vopen) value += c;
		}
		return new ArgsOutput(out);
	}
	
	public static ArgsOutput read(String[] args){
		String in = "";
		for(String s : args) {
			in += s + " ";
		}
		return read(in);
	}
}
