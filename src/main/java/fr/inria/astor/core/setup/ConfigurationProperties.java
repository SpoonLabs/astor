package fr.inria.astor.core.setup;

import java.io.FileInputStream;
import java.util.Properties;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ConfigurationProperties {

	public	static Properties properties;
	
	static{
		  FileInputStream propFile;
		try {
			properties = new Properties();
			propFile = new FileInputStream("configuration.properties");

				properties.load(propFile);
				} catch (Exception e) {
					e.printStackTrace();
				}

	}
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
}
