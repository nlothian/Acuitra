package com.acuitra.sparql;

import java.util.HashMap;

public class JsonTable {
	private String[] columnNames;
	private HashMap<String,String>[] results;
	
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	public HashMap<String, String>[] getResults() {
		return results;
	}
	public void setResults(HashMap<String, String>[] results) {
		this.results = results;
	}
	
	
	
}
