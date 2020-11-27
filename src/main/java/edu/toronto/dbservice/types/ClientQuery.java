package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ClientQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer clientNum;
	private String item;
	
	public ClientQuery(Integer pQueryID, Integer pClientNum, String pItem) {
		id = pQueryID;
		clientNum = pClientNum;
		item = pItem;
	}
	
	public Integer getQueryID() {
		return id;
	}
	
	public Integer getClientNum() {
		return clientNum;
	}
	
	public String getItem() {
		return item;
	}

}