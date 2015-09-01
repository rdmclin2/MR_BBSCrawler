package com.mcl.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class Crawler {

	public ArrayList<String> crawling(String visitUrl) throws IOException {
		FileParser fileParser = new FileParser();
		FileDownLoader downLoader = new FileDownLoader();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//get links of the visitUrl
		Map<String, String> fileMap = fileParser.getFileUrls(visitUrl);
		ArrayList<String > contents = new ArrayList<String>();
		int cnt = 0;
		int size = fileMap.size();
		ArrayList<Entry<String, String>> list = new ArrayList<Entry<String, String>>(fileMap.entrySet());
		
		// get each doc
		for (int i=0; i< list.size();i++) {
			Entry<String, String> entry = list.get(i);
			if(cnt %10 == 0){
				System.out.println("Download Process: "+cnt+" / "+size);
			}
			cnt++;
			String title = entry.getValue();
			
			String content = downLoader.downloadFile(entry.getKey(), title);
			contents.add(content);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return contents;
	}

//	// main 方法入口
//	public static void main(String[] args) {
//		Crawler crawler = new Crawler();
//		try {
//			crawler.crawling("http://bbs.nju.edu.cn/bbstopb10");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}