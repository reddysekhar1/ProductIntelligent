package com.intelliservice.agentanalytics.model;

public class Documents {
 private String id;
 private Source source;
 public String getId() {
	return id;
}
public Documents setId(String id) {
	this.id = id;
	return this;
}
public Source getSource() {
	return source;
}
public Documents setSource(Source source) {
	this.source = source;
	return this;
}

}
