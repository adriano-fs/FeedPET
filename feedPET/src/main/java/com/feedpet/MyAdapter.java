package com.feedpet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	Context ctx;
	List<SyndEntry> list;
	
	public MyAdapter(Context ctx, ArrayList<SyndEntry> list){
		this.ctx = ctx;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.rss, null);
		
		TextView tv1 = (TextView) view.findViewById(R.id.textView1);
		String s=(list.get(position).getTitle()).replaceAll("\n"," ");

		//if(s.length()>80)
		//	s=s.substring(0,80);
		tv1.setText(s);
		
		//tv1.setTextColor(Color.BLACK);

		DateFormat f = DateFormat.getDateInstance(DateFormat.FULL,new Locale("pt","BR"));
		
		
		TextView tv2 = (TextView) view.findViewById(R.id.textView2);

		//tv2.setTextColor(Color.BLACK);
		String lnk = list.get(position).getLink();
		if(lnk.contains("facebook"))
			tv2.setText((list.get(position).getAuthor()).toUpperCase());
		else
			tv2.setText(lnk.substring(lnk.indexOf("//")+2, lnk.indexOf(".")).toUpperCase());
		
		TextView tv3 = (TextView) view.findViewById(R.id.textView3);
			tv3.setText(f.format(list.get(position).getPublishedDate()));


		//tv3.setText(list.get(position).getPublishedDate());
		//tv3.setTextColor(Color.BLACK);
		return view;
	}
	
}
