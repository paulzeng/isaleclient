package com.zjrc.isale.client.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TerminalType implements Serializable {
	private String id;
	private String name;
	
	public TerminalType(){
		
	}
	
	public TerminalType(String id,String name){
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
