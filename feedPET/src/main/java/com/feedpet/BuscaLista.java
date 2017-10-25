package com.feedpet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;


public class BuscaLista {

	public static ArrayList<SyndEntry> buscaP(String palavra, ArrayList<SyndEntry> items) {
		SyndEntry O = null;
		ArrayList<SyndEntry> l = new ArrayList<SyndEntry>();
		String t,c;
		
		for (int j = 0; j < items.size(); j++) {
			O = items.get(j);
			t = O.getTitle().toLowerCase();

			if(O.getLink().indexOf("blogspot")==-1)
				c = O.getDescription().getValue();
			else
				c = Noticias.getContentFeed(O);
			
			c.toLowerCase();
			
			if (t.indexOf(palavra)!= -1 || c.indexOf(palavra)!= -1) {
				l.add(O);
			}		
		}
		return l;
	}
		
}