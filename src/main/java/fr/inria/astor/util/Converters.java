package fr.inria.astor.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Converters {

	
	
	public static URL[] toURLArray(String[] cp) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();

		for (String c : cp) {
			urls.add(new File(c).toURI().toURL());
		}
		URL[] u = new URL[urls.size()];

		return (URL[]) urls.toArray(u);
	}

	public static URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}
}
