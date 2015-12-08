package fr.inria.astor.core.faultlocalization.entity.runtestsuite;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


public class ClassloaderFinder implements ClassFinder {

	private URLClassLoader urlClassloader;
	public ClassloaderFinder(URLClassLoader urlClassloader) {
		this.urlClassloader = urlClassloader;
	}

	@Override
	public String[] getClasses() {
		List<String> classes = new ArrayList<>();
		for (URL url : urlClassloader.getURLs()) {
			if(new File(url.getPath()).isDirectory())
				classes.addAll(SourceFolderFinder.getClassesLoc(new File(url.getPath()), null));	
		}
		return classes.toArray(new String[0]);
	}

}
