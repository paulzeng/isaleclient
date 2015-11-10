package com.zjrc.isale.client.ui.adapter;

/**
 * 项目名称：销售管家客户端
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：网点选择适配器
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zjrc.isale.client.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TerminalSelectAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<TerminalItem> list;

    public TerminalSelectAdapter(LayoutInflater inflater) {
        super();
        this.inflater = inflater;
        this.list = new ArrayList<TerminalItem>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < list.size()) {
            return list.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TerminalItem item = list.get(position);
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.terminalselect_list_item, parent, false);
        }
        ItemViewHolder holder = (ItemViewHolder) view.getTag();
        if (holder == null) {
            holder = new ItemViewHolder();
            holder.tv_terminalname = (TextView) view.findViewById(R.id.tv_terminalname);
            holder.tv_terminaladdress = (TextView) view.findViewById(R.id.tv_terminaladdress);
            view.setTag(holder);
        }
        if (item != null) {
            holder.tv_terminalname.setText(item.getTerminalname());
            holder.tv_terminaladdress.setText(item.getTerminaladdress());
        }
        return view;
    }

    public void addItem(String terminalid, String terminalname, String terminaladdress,
                        String terminalman, String terminalphone) {
        TerminalItem item = new TerminalItem();
        item.setTerminalid(terminalid);
        item.setTerminalname(terminalname);
        item.setTerminaladdress(terminaladdress);
        item.setTerminalman(terminalman);
        item.setTerminalphone(terminalphone);
        list.add(item);
    }

    public void clearItem() {
        list.clear();
    }

    public List<TerminalItem> getList() {
        return list;
    }

    protected class ItemViewHolder {
        public TextView tv_terminalname;
        public TextView tv_terminaladdress;
    }

    @SuppressWarnings("serial")
    public class TerminalItem implements Serializable {
        private String terminalid;
        private String terminalname;
        private String terminaladdress;
        private String terminalman;
        private String terminalphone;

        public String getTerminalid() {
            return terminalid;
        }

        public void setTerminalid(String terminalid) {
            this.terminalid = terminalid;
        }

        public String getTerminalname() {
            return terminalname;
        }

        public void setTerminalname(String terminalname) {
            this.terminalname = terminalname;
        }

        public String getTerminaladdress() {
            return terminaladdress;
        }

        public void setTerminaladdress(String terminaladdress) {
            this.terminaladdress = terminaladdress;
        }

        public String getTerminalman() {
            return terminalman;
        }

        public void setTerminalman(String terminalman) {
            this.terminalman = terminalman;
        }

        public String getTerminalphone() {
            return terminalphone;
        }

        public void setTerminalphone(String terminalphone) {
            this.terminalphone = terminalphone;
        }
    }
}
