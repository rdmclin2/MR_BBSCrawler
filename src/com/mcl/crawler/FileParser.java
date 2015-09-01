package com.mcl.crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

//get all the links of target board
public class FileParser {
	// links number : 5000
	private final int LINK_UP = 5000;

	Map<String, String> links = new HashMap<String, String>();

	private String extracLinks(String url, LinkFilter filter) {
		// links of next page
		String nextPage = null;
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("gb2312");
			// linkFilter to filter <a> tag
			NodeClassFilter linkFilter = new NodeClassFilter(LinkTag.class);
			// filter tags
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag)// <a> tag
				{
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// url

					// get link of "下一页"
					if (link.getLinkText().equals("上一页")) {
						nextPage = linkUrl;
					}

					if (filter.accept(linkUrl) && links.size() < LINK_UP) {
						String title = link.getLinkText();
						links.put(linkUrl, title);
					}
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return nextPage;
	}

	// get board link and change to theme mode
	public Map<String, String> getFileUrls(String seed) {
		System.out.println("seed: " + seed);
		links.clear();
		int linkCount = 0;
		final String rule = seed.replace("bbstdoc", "bbstcon");
		String list = seed;
		while (linkCount < LINK_UP) {
			list = extracLinks(list, new LinkFilter() {
				// get links starting with http://bbs.nju.edu.cn/bbstcon?board=... 
				public boolean accept(String url) {
					if (url.startsWith(rule))
						return true;
					else
						return false;
				}
			});
			linkCount = links.size();
			// if no nextpage
			if (list == null) {
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("sum: " + links.size());
//		for (Entry<String, String> entry : links.entrySet()) {
//			System.out.println(entry.getKey() + " " + entry.getValue());
//		}
		return links;
	}

	// 测试的 main 方法
	public static void main(String[] args) {
		FileParser parser = new FileParser();
		parser.getFileUrls("http://bbs.nju.edu.cn/bbstdoc?board=Blog");
	}
}
