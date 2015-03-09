package util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfReader {

	private static String confFilePath="init.properties";
	private static Properties properties;
	static{
		properties=new Properties();
		try {
			properties.load(new FileReader(confFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, String> getConfs(List<String> keys){
		Map<String, String> result=new HashMap<String, String>();
		
		String temp=null;
		for(String key : keys){
			temp=properties.getProperty(key);
			result.put(key,temp);
		}
		return result;
	}
}
