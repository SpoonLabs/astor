package fr.inria.astor.test.repair.evaluation.extensionpoints.deep.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DKeyReader {

	public static void main(String[] args) {
		
	}
	
	public List<String> readKeyFile(File path) throws IOException{
		Stream<String> stream = Files.lines(path.toPath());
		List<String> listOfStream = stream.collect(Collectors.toList());
		System.out.println("#lines "+listOfStream.size());
		try {
			System.out.println("List "+listOfStream);
	       // stream.forEach(System.out::println);
		}catch(Exception e){
			e.printStackTrace();
		}
	      //  System.out.println(stream.count());
		return listOfStream;
	}
}
