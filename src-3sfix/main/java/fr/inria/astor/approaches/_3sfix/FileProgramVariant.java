package fr.inria.astor.approaches._3sfix;

import java.io.File;

import fr.inria.astor.core.entities.ProgramVariant;

/**
 * Program Variant that stored in a file
 * 
 * @author Matias Martinez
 *
 */
public class FileProgramVariant extends ProgramVariant {

	/**
	 * File with the content of the program variant
	 */
	private File locationVariantCodeSource = null;
	/**
	 * Class name of the modified class
	 */
	private String className = null;

	public FileProgramVariant(int id, String className, File locationVariant) {
		super();
		this.id = id;
		this.className = className;
		this.locationVariantCodeSource = locationVariant;
	}

	public File getLocationVariantCodeSource() {
		return locationVariantCodeSource;
	}

	public void setLocationVariantCodeSource(File locationVariant) {
		this.locationVariantCodeSource = locationVariant;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
