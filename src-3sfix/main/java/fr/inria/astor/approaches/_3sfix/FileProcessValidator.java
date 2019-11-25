package fr.inria.astor.approaches._3sfix;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.junit.JUnitProcessValidator;
import fr.inria.astor.util.Converters;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FileProcessValidator extends JUnitProcessValidator {
	SpoonClassCompiler compiled = new SpoonClassCompiler();

	
	@Override
	protected URL[] createClassPath(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade)
			throws MalformedURLException {

		List<URL> originalURL = createOriginalURLs(projectFacade);
		URL[] bc;

		File variantOutputFile = defineLocationOfCompiledCode(mutatedVariant, projectFacade);

		bc = Converters.redefineURL(variantOutputFile, originalURL.toArray(new URL[0]));
		return bc;
	}
	
	@Override
	protected File defineLocationOfCompiledCode(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {

		FileProgramVariant fileVariant = (FileProgramVariant) mutatedVariant;
		File codeFile = fileVariant.getLocationVariantCodeSource();

		String content = "";
		try {
			content = new String(Files.readAllBytes(codeFile.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

		Map<String, String> toCompile = new HashMap<String, String>();
		toCompile.put(fileVariant.getClassName(), content);

		URL[] cp;
		try {
			cp = projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}

		CompilationResult compilation = compiled.compile(cp, toCompile);
		log.debug("Compilation: " + compilation.getErrorList());
		fileVariant.setCompilation(compilation);

		return super.defineLocationOfCompiledCode(fileVariant, projectFacade);
	}



}
