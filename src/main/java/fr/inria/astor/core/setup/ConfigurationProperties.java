package fr.inria.astor.core.setup;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import fr.inria.main.AbstractMain;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ConfigurationProperties {

	public	static Properties properties;
	protected static Logger log = Logger.getLogger(ConfigurationProperties.class);
	
	static{
		  InputStream propFile;
		try {
			properties = new Properties();
			propFile = ConfigurationProperties.class.getClassLoader().getResourceAsStream("configuration.properties");

				properties.load(propFile);
				} catch (Exception e) {
					e.printStackTrace();
				}

	}
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public static Integer getPropertyInt(String key){
		return Integer.valueOf(properties.getProperty(key));
	}
	
	public static Boolean getPropertyBool(String key){
		return Boolean.valueOf(properties.getProperty(key));
	}
	
	public static Double getPropertyDouble(String key){
		return Double.valueOf(properties.getProperty(key));
	}

	public static void print(){
		log.info("----------------------------");
		log.info("---Configuration properties:---Execution values");
		for(String key :properties.stringPropertyNames()){
			log.info("p:"+key+" "+properties.getProperty(key));
		}
		log.info("----------------------------");
	}
	
}
