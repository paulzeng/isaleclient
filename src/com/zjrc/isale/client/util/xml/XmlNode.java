package com.zjrc.isale.client.util.xml;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 娄露铭
 * 功能描述：XML节点
 */

import java.util.ArrayList;
import java.util.Iterator;

public class XmlNode {
    private String strTag;
    private String strText;
    private ArrayList<AttrNode> attrNodes;
    private ArrayList<XmlNode> childNodes;
    private Object oAttach;
    
    public XmlNode(){
        strTag = null;
        strText = null;
        attrNodes = null;
        childNodes = null;
        oAttach = null;
    }

    public XmlNode(String tag, String text){
        strTag = null;
        strText = null;
        attrNodes = null;
        childNodes = null;
        oAttach = null;
        strText = text;
        strTag = tag;
    }

    public void deinit(){
        oAttach = null;
        if(childNodes != null)
        {
            XmlNode me;
            for(Iterator<XmlNode> iterator = childNodes.iterator(); iterator.hasNext(); me.deinit())
                me = (XmlNode)iterator.next();

            childNodes.clear();
        }
        if(attrNodes != null)
            attrNodes.clear();
    }

    public void setObject(Object obj){
        oAttach = obj;
    }

    public Object getObject(){
        return oAttach;
    }

    public void setText(String text){
        //strText = XmlValueUtil.encodeString(text);
        strText = text;
    }

    public String getText(){
    	//return XmlValueUtil.decodeString(strText);
        return strText;
    }

    public String getTag(){
        return strTag;
    }

    public void setTag(String tag){
        strTag = tag;
    }

    public int getChildCount(){
        if(childNodes != null)
            return childNodes.size();
        else
            return 0;
    }

    public XmlNode getChildNode(int index){
        if(childNodes != null && index >= 0 && index < childNodes.size())
            return (XmlNode)childNodes.get(index);
        else
            return null;
    }

    public XmlNode getChildNode(String tag){
        if(childNodes != null)
        {
            for(Iterator<XmlNode> iterator = childNodes.iterator(); iterator.hasNext();)
            {
                XmlNode child = (XmlNode)iterator.next();
                if(tag.compareTo(child.getTag()) == 0)
                    return child;
            }

        }
        return null;
    }

    public String getChildNodeText(String tag){
        XmlNode child = getChildNode(tag);
        if(child != null)
            return child.getText();
        else
            return null;
    }

    public ArrayList<XmlNode> getChildNodeSet(){
        return childNodes;
    }

    public ArrayList<XmlNode> getChildNodeSet(String tag){
        ArrayList<XmlNode> lsXmlObj = null;
        if(tag != null)
        {
            lsXmlObj = new ArrayList<XmlNode>();
            if(tag != null && childNodes != null)
            {
                for(Iterator<XmlNode> iterator = childNodes.iterator(); iterator.hasNext();)
                {
                    XmlNode child = (XmlNode)iterator.next();
                    if(tag.compareTo(child.getTag()) == 0)
                        lsXmlObj.add(child);
                }

            }
        }
        return lsXmlObj;
    }

    public XmlNode getChildNode(String tag, int index){
        if(childNodes != null)
        {
            int iCount = 0;
            for(Iterator<XmlNode> iterator = childNodes.iterator(); iterator.hasNext();)
            {
                XmlNode child = (XmlNode)iterator.next();
                if(tag.equals(child.getTag()))
                {
                    if(index == iCount)
                        return child;
                    iCount++;
                }
            }

        }
        return null;
    }

    public XmlNode getChildNode(String tag, String attr, String value){
        if(childNodes != null)
        {
            if(attr == null || value == null)
                return getChildNode(tag);
            for(Iterator<XmlNode> iterator = childNodes.iterator(); iterator.hasNext();)
            {
                XmlNode child = (XmlNode)iterator.next();
                if(tag.compareTo(child.getTag()) == 0 && value.compareTo(child.getAttrValue(attr)) == 0)
                    return child;
            }

        }
        return null;
    }

    public XmlNode addChildNode(String tag, String text){
        if(childNodes == null)
            childNodes = new ArrayList<XmlNode>();
        XmlNode me = new XmlNode(tag, text);
        childNodes.add(me);
        return me;
    }

    public void addChildNode(XmlNode child){
        if(childNodes == null)
            childNodes = new ArrayList<XmlNode>();
        childNodes.add(child);
    }

    public void delChildNode(XmlNode child){
        if(childNodes != null)
        {
            child.deinit();
            childNodes.remove(child);
        }
    }

    public int getAttrCount(){
        if(attrNodes != null)
            return attrNodes.size();
        else
            return 0;
    }

    public String getAttrKey(int index){
        if(attrNodes != null && index >= 0 && index < attrNodes.size())
            return ((AttrNode)attrNodes.get(index)).getKey();
        else
            return null;
    }

    public String getAttrValue(int index){
        if(attrNodes != null && index >= 0 && index < attrNodes.size())
            return ((AttrNode)attrNodes.get(index)).getValue();
        else
            return null;
    }

    public String getAttrValue(String key){
        if(attrNodes != null)
        {
            for(Iterator<AttrNode> iterator = attrNodes.iterator(); iterator.hasNext();)
            {
                AttrNode attr = (AttrNode)iterator.next();
                if(key.compareTo(attr.getKey()) == 0)
                    return attr.getValue();
            }

        }
        return null;
    }

    public boolean setAttrValue(String key, String value){
        boolean rs = false;
        if(attrNodes != null)
        {
            for(Iterator<AttrNode> iterator = attrNodes.iterator(); iterator.hasNext();)
            {
                AttrNode attr = (AttrNode)iterator.next();
                if(key.compareTo(attr.getKey()) == 0)
                {
                    attr.setValue(value);
                    rs = true;
                    break;
                }
            }

        }
        return rs;
    }

    public ArrayList<AttrNode> getAttrNodeSet(){
        return attrNodes;
    }

    public ArrayList<AttrNode> getAttrNodeSet(String sTag){
        XmlNode xmlObj = getXmlNode(sTag);
        if(xmlObj != null)
            return xmlObj.attrNodes;
        else
            return null;
    }

    public AttrNode addAttrNode(String key, String value){
        if(attrNodes == null)
            attrNodes = new ArrayList<AttrNode>();
        AttrNode me = new AttrNode(key, value);
        attrNodes.add(me);
        return me;
    }

    public AttrNode addAttrNode(AttrNode me){
        if(attrNodes == null)
            attrNodes = new ArrayList<AttrNode>();
        attrNodes.add(me);
        return me;
    }

    public String createXML(){
        return createXML("1.0", "utf-8");
    }

    public String createXML(String cCharset){
        return createXML("1.0", cCharset);
    }

    public String createXML(String version, String cCharset){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"");
        sb.append(version);
        sb.append("\" encoding=\"");
        sb.append(cCharset);
        sb.append("\"?>");
        createXML(sb);
        return sb.toString();
    }

    private void createXML(StringBuffer sb){
        sb.append('<');
        sb.append(strTag);
        if(attrNodes != null)
        {
            for(int i = 0; i < attrNodes.size(); i++)
            {
                AttrNode attr = (AttrNode)attrNodes.get(i);
                if(attr.getKey() != null && attr.getValue() != null)
                {
                    sb.append(' ');
                    sb.append(attr.getKey());
                    sb.append("=\"");
                    sb.append(attr.getValue());
                    sb.append("\"");
                }
            }

        }
        sb.append('>');
        if(strText != null)
            sb.append(XmlValueUtil.encodeString(strText));
        if(childNodes != null)
        {
            for(int i = 0; i < childNodes.size(); i++)
            {
                XmlNode me = (XmlNode)childNodes.get(i);
                me.createXML(sb);
            }

        }
        sb.append("</");
        sb.append(strTag);
        sb.append('>');
    }

    public XmlNode getXmlNode(String sTags){
        XmlNode curObj = null;
        if(sTags != null)
        {
            String lsTag[] = sTags.split("\\.");
            if(lsTag.length > 1 && lsTag[0].compareTo(strTag) == 0)
            {
                curObj = this;
                boolean rs = true;
                for(int i = 1; rs && i < lsTag.length; i++)
                {
                    rs = false;
                    if(curObj.childNodes == null)
                        break;
                    for(Iterator<XmlNode> iterator = curObj.childNodes.iterator(); iterator.hasNext();)
                    {
                        XmlNode child = (XmlNode)iterator.next();
                        if(lsTag[i].compareTo(child.strTag) == 0)
                        {
                            curObj = child;
                            rs = true;
                            break;
                        }
                    }

                }

                if(!rs)
                    curObj = null;
            }
        } else
        {
            curObj = this;
        }
        return curObj;
    }

    public String getText(String sTags){
        XmlNode obj = getXmlNode(sTags);
        if(obj != null)
            return obj.strText;
        else
            return null;
    }

    public boolean setText(String sTags, String sText){
        XmlNode obj = getXmlNode(sTags);
        if(obj != null)
        {
            obj.strText = sText;
            return true;
        } else
        {
            return false;
        }
    }

    public boolean setAttrValue(String sTags, String sKey, String sValue){
        XmlNode obj = getXmlNode(sTags);
        if(obj != null)
            return obj.setAttrValue(sKey, sValue);
        else
            return false;
    }

    public String getAttrValue(String sTags, String sKey){
        XmlNode obj = getXmlNode(sTags);
        if(obj != null)
            return obj.getAttrValue(sKey);
        else
            return null;
    }

}
