
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.mcl.crawler.Crawler;



public class MR_Crawler {
	public static class CrawlerMapper extends Mapper<Object, Text, Text, Text>{ 
	    private Text board = new Text();
	 
	    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
	        String visitUrl = value.toString();
	        
			// get the corresponding board name
			String boardString = visitUrl.trim().split("=")[1];
			board.set(boardString);
			
			// crawl download files in the url
			Crawler crawler = new Crawler();
			ArrayList<String> contents = crawler.crawling(visitUrl);
			Text content = new Text();
			
			//write files 
			for (String str : contents) {
				content.set(str);
				context.write(board, content);
			}
	    }
	}
	 
	public static class CrawlerReducer extends  Reducer<Text,Text,Text,Text> {
	    public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException {
	        for (Text content : values) {
	        	//System.out.println(key.toString() + " " +content.toString());
	        	context.write(key, content);
	        }
	    }
	}
	 
	public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
	    if (otherArgs.length != 2) {
	        System.err.println("Usage: crawler <in> <out>");
	        System.exit(2);
	    }
	    
	    
	    Job job = new Job(conf, "BBS crawler");
	    job.setJarByClass(MR_Crawler.class);
	    job.setMapperClass(CrawlerMapper.class);
	    job.setCombinerClass(CrawlerReducer.class);
	    job.setReducerClass(CrawlerReducer.class);
	    
	    //set output format
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    
	    //set input and output path
	    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
	    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
	    
	    
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
