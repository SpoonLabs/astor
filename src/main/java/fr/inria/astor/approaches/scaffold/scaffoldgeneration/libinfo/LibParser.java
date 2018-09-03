package fr.inria.astor.approaches.scaffold.scaffoldgeneration.libinfo;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ImportScanner;
import spoon.reflect.visitor.ImportScannerImpl;
import spoon.reflect.visitor.filter.TypeFilter;

public class LibParser {
	
	protected Logger log = Logger.getLogger(LibParser.class.getName());
	@SuppressWarnings("rawtypes")
	private CtClass faultyClass;
	private Factory factory;
	private ProjectRepairFacade projFacade;
	@SuppressWarnings("rawtypes")
	protected Map<String, CtClass> classMap = new HashMap<String, CtClass>();
    protected ArrayList<String> parsedfile = new ArrayList<String>();
    protected Map<String, Class<?>[]> external = new HashMap<String, Class<?>[]>();
	protected Map<String, CtEnum> enumMap = new HashMap<String, CtEnum>();

    @SuppressWarnings({ "static-access" })
	public LibParser(ModificationPoint modPoint, MutationSupporter supporter, ProjectRepairFacade facade) {
		this.faultyClass=modPoint.getCtClass();
		this.factory=supporter.getFactory();
		this.projFacade=facade;
	}
    
    public void analyzeLib() {
    	
    	String sourcefile=this.faultyClass.getPosition().getFile().getAbsolutePath();
    	sourcefile = sourcefile.substring(0,sourcefile.lastIndexOf("/"));
    	parseDirSingle(new File(sourcefile));

    	ImportScanner importContext = new ImportScannerImpl();
		importContext.computeImports(this.faultyClass);
        Collection<CtImport> imports = importContext.getAllImports();
        for (CtImport certainimport : imports) {

        	String importedlibname=certainimport.getReference().toString();
        	if(importedlibname.indexOf(".")==-1) 
        		continue;
        	
        	if(certainimport.getImportKind()==CtImportKind.FIELD) {
        		log.info("do not need to consider this case");
        		importedlibname="";
        	} else if(certainimport.getImportKind()==CtImportKind.TYPE) {
        		importedlibname=importedlibname;
        	} else if(certainimport.getImportKind()==CtImportKind.METHOD
        			||certainimport.getImportKind()==CtImportKind.ALL_TYPES) {
        		importedlibname=importedlibname.substring(0,importedlibname.lastIndexOf("."));
        	} else if (certainimport.getImportKind()==CtImportKind.ALL_STATIC_MEMBERS) {
        		log.info("temporily ignore this case");
        		importedlibname="";
        	}
        	
        	parseImportFile(importedlibname);
		}	
    }
    
    public void parseImportFile(String importFileName) {
    	
    	if(!importFileName.isEmpty()) {
    		String filelocation= this.projFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT)+ 
    				File.separator+
    				importFileName.replace(".", "/");
			if (!new File(filelocation).isDirectory())
				filelocation += ".java"; 
			if(new File(filelocation).exists()) {
				if(filelocation.endsWith(".java"))
    			    parseFile(new File(filelocation));
				else
					parseDirIterative(new File(filelocation));
			}
			else 
				if(!importFileName.startsWith("java"))
				    establishOutLib(importFileName);
    	}
    }
    
    public void parseFile(File name) {
    	
    	if(!parsedfile.contains(name.getAbsolutePath())) {
    	  parsedfile.add(name.getAbsolutePath());
    	  
		  for (CtClass<?> klass : this.factory.getModel().getElements(new TypeFilter<CtClass>(CtClass.class))) {
			if(klass.getPosition().getFile().getAbsolutePath().equals(name.getAbsolutePath())) {
				@SuppressWarnings("rawtypes")
				CtClass founded=klass;
				if (!classMap.containsKey(founded.getQualifiedName())) {
					classMap.put(founded.getQualifiedName(), founded);
					List<CtEnum> enumArray = founded.getElements(new TypeFilter<>(CtEnum.class));
                    for(int index=0; index<enumArray.size(); index++) {
                    	enumMap.put(enumArray.get(index).getQualifiedName(), enumArray.get(index));
                    }
				}			
				break;
			}
		  }
       }
	}
    
    @SuppressWarnings("rawtypes")
   	public void parseDirSingle(File folder) {
   		
   		for (File f : folder.listFiles()) {
   			if(f.isDirectory())
   				continue;
   			else
   				parseFile(f);
   		}
   	}
    
    @SuppressWarnings("rawtypes")
	public void parseDirIterative(File folder) {
    	
    	for (File f : folder.listFiles()) {
   			if(f.isDirectory())
   				parseDirIterative(f);
   			else
   				parseFile(f);
   		}
	}
    
    public void establishOutLib(String qualifiedname) {
		Class<?>[] clazz = new OutLibParser(this.projFacade).getClassesFromPrefix(qualifiedname);

		if (clazz != null && clazz.length > 0 && !external.containsKey(qualifiedname))
			external.put(qualifiedname, clazz);
	}

	@SuppressWarnings("rawtypes")
	public List<Class[]> fetchMethods(String clazz, String method) {
		Class<?>[] cls = external.get(clazz);
		
		if(cls == null||cls.length==0) {
			for(String key : external.keySet()) {
				if(clazz.startsWith(key)) {
					cls=external.get(key);
					break;
				}	
			}
		}
		
		List<Class[]> list = new ArrayList<Class[]>();
		
		if (cls == null||cls.length==0)
			return list;

		for (Class<?> cz : cls) {
			if(cz.getName().equals(clazz)) {
			    if (clazz.equals(method)) {
				  for (Constructor c : cz.getConstructors()) {
					list.add(c.getParameterTypes());
				  }
			    } else {
				  for (Method mtd : cz.getMethods()) {
					if (mtd.getName().equals(method)) {
						list.add(mtd.getParameterTypes());
					}
				  }
			   }
			   break;
			}
		 }
		return list;
	}
	
	public Map<String, CtClass> getClassMap() {
		return classMap;
	}
	
	public Map<String, Class<?>[]> getExternalMap() {
		return external;
	}
	
	public Map<String, CtEnum> getEnumMap() {
		return enumMap;
	}
}
