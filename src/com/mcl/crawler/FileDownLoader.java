package com.mcl.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import sun.misc.Cleaner;

public class FileDownLoader {
	
	//clean text
	public String clean(String str){
		String []strs = str.split("\n");
		String content = "";
		for(int i=4;i<strs.length;i++){
			if(strs[i].startsWith("--"))//remove --
				break;
			if(strs[i].startsWith("http:"))//remove links
				continue;
			content += strs[i];
		}
		String result=content.replaceAll("\\s", "");
		//System.out.println(result);
		return result;
	}

	/* download bbs files */
	public String downloadFile(String url, String title) {
		//
		Parser parser;
		try {
			parser = new Parser(url);
			parser.setEncoding("UTF-8");
			NodeList tableOfPre1 = parser
					.extractAllNodesThatMatch((NodeFilter) new AndFilter(
							new NodeClassFilter(TableTag.class),
							new HasAttributeFilter("class", "main")));
			if (tableOfPre1 != null && tableOfPre1.size() > 0) {
				// get table tag <tbody>
				NodeList tList = tableOfPre1
						.elementAt(0)
						.getChildren()
						.extractAllNodesThatMatch(new TagNameFilter("tr"), true);
				// get tag：pre
				String text = tList.elementAt(1).getFirstChild()
						.toPlainTextString();
				String cleanText = title+"\t"+clean(text);
				return cleanText;
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return title+"\t";
	}

}