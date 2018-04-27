package fr.inria.astor.test.repair.evaluation.extensionpoints.deep.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.ExhaustiveSearchEngine;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

public class FakeCustomEngine extends ExhaustiveSearchEngine {

	public FakeCustomEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		System.out.println("Creating Custom Engine");
	}

	@Override
	public void startEvolution() throws Exception {
		//super.startEvolution();
		System.out.println("Executing Custom Engine");
		int nrtotal  = 0;
		int nrexist = 0;
		DKeyReader dkr = new DKeyReader();
		List<String> foundCts = new ArrayList<>(); 
		List<String> keys = dkr.readKeyFile(new File("/Users/matias/develop/results/deeprepair/dat/src2txt/Math/70/b/executables.key"));
		//System.out.println(keys);
		
		List<String> resultCtmapped = new ArrayList<>(); 
		List<String> resultCtNotmapped = new ArrayList<>(); 
		List<String> resultDPmapped = new ArrayList<>(); 
		List<String> resultDPNotmapped = new ArrayList<>(); 
		
		Factory  factory = this.getMutatorSupporter().getFactory();
		List<CtType<?>> types = factory.Type().getAll();
	//	System.out.println(types.size());
		for (CtType<?> ctType : types) {
			//ctType.getQualifiedName()
		//	System.out.println("\n----Analyzing "+ctType.getQualifiedName());
			Collection<CtExecutableReference<?>> executables = ctType.getDeclaredExecutables();
			//ctType.getAllExecutables();
			//System.out.println("--total "+executables.size());
			for (CtExecutableReference<?> ctExecutableReference : executables) {
				CtExecutable executable = ctExecutableReference.getDeclaration();
				if(executable != null){
					String exsig = ( (CtType)executable.getParent() ).getQualifiedName() + "#"+executable.getSignature();
					String exsig2 = null;//( (CtType)executable.getParent() ).getQualifiedName() + "#"+alternativeSignature(executable);
					
					nrtotal++;
					boolean exist = keys.contains(exsig) || keys.contains(exsig2) ;
					nrexist+=(exist)?1:0;
					foundCts.add(exsig);
					if(exist){
						resultCtmapped.add(exsig);
					}else{
					
						resultCtNotmapped.add(exsig);
					}
				}else{
				//	System.out.println("-->"+ctExecutableReference);
				}
			}
			Set<CtMethod<?>> methods = ctType.getAllMethods();
			for (CtMethod<?> ctMethod : methods) {
			//	System.out.println("method: "+ctMethod.getSignature());
				// boolean chiSquareTest(long[][],double)
				
			}
		}
		//
		int totalD = 0, dpfound = 0;
		for (String key : keys) {
			//System.out.println("key:"+key);
			totalD++;
			boolean exist = foundCts.contains(key);
		//	System.out.println(exist);
			
			if(exist){
				dpfound++;
				resultDPmapped.add(key);
			}else{
				resultDPNotmapped.add(key);
			}
		}
		///
		System.out.println("----");
		System.out.println("CT total exect "+nrtotal+", found exec "+nrexist + ", not found "+resultCtNotmapped.size());
		System.out.println("Ct not mapped "+resultCtNotmapped.size());
		for (int i = 0; i < resultCtNotmapped.size() && i < 10; i++) {
			String si = resultCtNotmapped.get(i);
			System.out.println("ctnotm: "+si);
		}
		System.out.println("----");
		System.out.println("DP total exect "+totalD+", found exec "+dpfound + ", not found "+resultDPNotmapped.size());
		System.out.println("--Not mapped from DP");
		for (int i = 0; i < resultDPNotmapped.size() && i < 100; i++) {
			String si = resultDPNotmapped.get(i);	
			System.out.println("dpnotm: "+si);
		}
		//System.out.println("ct not mapped "+resultDPNotmapped);
		System.out.println("----");
		System.out.println("Mapped: ");
		for (int i = 0; i < resultDPmapped.size() && i < 100; i++) {
			String si = resultDPmapped.get(i);	
			//System.out.println(si);
		}
	//	Files.write(new File("/tmp/cttest.txt").toPath(), resultCtmapped.toString().getBytes(),StandardOpenOption.values());
	}

	private String alternativeSignature(CtExecutable executable) {
		SimpleSignature sp = new SimpleSignature();
		sp.scan(executable);
		return sp.getSignature();
	}
	
}
