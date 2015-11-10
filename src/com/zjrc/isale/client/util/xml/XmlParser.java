package com.zjrc.isale.client.util.xml;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 娄露铭
 * 功能描述：XML解析
 */

import android.util.Xml;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;

public class XmlParser {
    public XmlParser(){
    }

    public static XmlNode parserXML(XmlPullParser xmlParser){
        Stack<XmlNode> stackObj = new Stack<XmlNode>();
        XmlNode me = null;
        XmlNode head = null;
        try
        {
            for(int evtType = xmlParser.getEventType(); evtType != 1; evtType = xmlParser.next())
                switch(evtType)
                {
                default:
                    break;

                case 2: // '\002'
                    me = new XmlNode();
                    me.setTag(xmlParser.getName());
                    int size = xmlParser.getAttributeCount();
                    if(size > 0)
                    {
                        for(int i = 0; i < size; i++)
                            me.addAttrNode(xmlParser.getAttributeName(i), xmlParser.getAttributeValue(i));

                    }
                    if(stackObj.size() > 0)
                    {
                        XmlNode father = (XmlNode)stackObj.peek();
                        father.addChildNode(me);
                    } else {
                        head = me;
                    }
                    stackObj.push(me);
                    break;

                case 4: // '\004'
                    if(stackObj.size() > 0)
                    {
                        me = (XmlNode)stackObj.peek();
                        me.setText(xmlParser.getText());
                    }
                    break;

                case 3: // '\003'
                    if(stackObj.size() > 0)
                        stackObj.pop();
                    break;
                }

        }
        catch(Exception e)
        {
            if(head != null)
            {
                head.deinit();
                head = null;
            }
        }
        stackObj.clear();
        return head;
    }

    public static XmlNode parserXML(String xml, String code){
        if(xml != null && code != null)
        {
            XmlPullParser xmlParser = Xml.newPullParser();
            try
            {
                InputStream stream = new ByteArrayInputStream(xml.getBytes(code));
                xmlParser.setInput(stream, code);
            }
            catch(Exception e)
            {
                return null;
            }
            return parserXML(xmlParser);
        } else
        {
            return null;
        }
    }

    public static XmlNode parserXML(byte xml[], String code){
        if(xml != null)
        {
            XmlPullParser xmlParser = Xml.newPullParser();
            InputStream stream = new ByteArrayInputStream(xml);
            try
            {
                xmlParser.setInput(stream, code);
            }
            catch(Exception e)
            {
                return null;
            }
            return parserXML(xmlParser);
        } else
        {
            return null;
        }
    }
}
