package fr.inria.astor.approaches.scaffold.scaffoldgeneration.libinfo;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

import fr.inria.astor.core.setup.ProjectRepairFacade;

public class OutLibParser {
	
	private String libPath;
	private ProjectRepairFacade projFacade;
	protected static Logger log = Logger.getLogger(OutLibParser.class.getName());
    private static Map<String, Class<?>[]> classesOfPrefix = new HashMap<>();
	
	@SuppressWarnings({ "static-access" })
	public OutLibParser (ProjectRepairFacade facade) {
			this.projFacade=facade;
			this.libPath=this.projFacade.getProperties().getDependenciesString();
	}
	
	public void findClasses(String prefix) {
		
		if(!classesOfPrefix.containsKey(prefix)) {

	      String[] paths = this.libPath.split(System.getProperty("path.separator"));
	      File file;
	      for (String path : paths) {
	         file = new File(path);
	         if (file.isDirectory()) {
	        	// log.info("should not happen, astor bug");
	         } else if (file.getName().toLowerCase().endsWith(".jar")) {
	                 JarFile jar = null;
	                 try {
	                     jar = new JarFile(file);
	                 } catch (Exception ex) {

	                 }
	                 if (jar != null) {
	                     classesOfPrefix.put(prefix, findClassesWithPrefix(jar,prefix));
	                 }
	            } 
	        }
	    }
	}
	
	public Class<?>[] findClassesWithPrefix(JarFile jar, String prefix) {
        Set<Class<?>> classesGivenPrefix = new HashSet<>();

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            int extIndex = name.lastIndexOf(".class");
            if (extIndex > 0) {
           	 String className=name.substring(0, extIndex).replace("/", ".");
                if (className.startsWith(prefix)) {
                    try {
                        classesGivenPrefix.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return classesGivenPrefix.toArray(new Class[classesGivenPrefix.size()]);
    }
     
    public Class<?>[] getClassesFromPrefix(final String prefix) {
    	findClasses(prefix);
        return classesOfPrefix.get(prefix); 
    }
}
