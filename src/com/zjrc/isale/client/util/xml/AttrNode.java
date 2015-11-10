package com.zjrc.isale.client.util.xml;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 娄露铭
 * 功能描述：XML节点属性
 */

public class AttrNode {
    private String key;
    private String value;
    
    public AttrNode(){
    	key = null;
        value = null;
    }

    public AttrNode(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }
    
    public void setKey(String key){
        this.key = key;
    }    

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }    
}
