package fr.inria.astor.approaches._3sfix;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Runtime;
import java.lang.Process;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import fr.inria.main.evolution.AstorMain;
import fr.inria.main.CommandSummary;

import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.validation.VariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.ProjectConfiguration;

import org.apache.commons.io.FileUtils;

// in Java, we cannot start an identifier with a number
// so we prefix it by an underscore
public class _3sFix
{
public static void main(String[] args) throws Exception
{

        CommandLine cmd;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        options.addOption("dependencies", true, "dependencies of the application, separated by char " + File.pathSeparator);
        options.addOption("location", true, "URL of the project to manipulate");
        options.addOption("javacompliancelevel", true, "(Optional) Compliance level (e.g., 7 for java 1.7, 6 for java 1.6). Default Java 1.7");
        options.addOption("binjavafolder", true, "(Optional) folder with the application binaries (default: /target/classes/)");
        options.addOption("bintestfolder", true, "(Optional) folder with the test cases binaries (default: /target/test-classes/)");
        options.addOption("srcjavafolder", true, "(Optional) folder with the application source code files (default: /src/java/main/)");
        options.addOption("srctestfolder", true, "(Optional) folder with the test cases source code files (default: /src/test/main/)");
        options.addOption("failing", true, "failing test cases, separated by Path separator char (: in linux/mac  and ; in windows)");
        options.addOption("package", true, "package to instrument e.g. org.commons.math");
        options.addOption("flthreshold", true, "(Optional) threshold for Fault locatication.");
        options.addOption("project", true, "Name of the (Defects4J) project");
        options.addOption("bugid", true, "bug id from Defects4J");

        cmd = parser.parse(options, args);
        String dependencies = cmd.getOptionValue("dependencies");
        String location = cmd.getOptionValue("location");
        String javacompliancelevel = cmd.getOptionValue("javacompliancelevel");
        String binjavafolder = cmd.getOptionValue("binjavafolder");
        String bintestfolder = cmd.getOptionValue("bintestfolder");
        String srcjavafolder = cmd.getOptionValue("srcjavafolder");
        String srctestfolder = cmd.getOptionValue("srctestfolder");
        String failing = cmd.getOptionValue("failing");
        String package_name = cmd.getOptionValue("package");
        String flthreshold = cmd.getOptionValue("flthreshold");
        String project = cmd.getOptionValue("project");
        String bugid = cmd.getOptionValue("bugid");


        AstorMain main1 = new AstorMain();
        CommandSummary cs = new CommandSummary();
        cs.command.put("-dependencies", dependencies);
        cs.command.put("-location", location);
        cs.command.put("-flthreshold", flthreshold);
        cs.command.put("-javacompliancelevel", javacompliancelevel);
        cs.command.put("-binjavafolder", binjavafolder);
        cs.command.put("-bintestfolder", bintestfolder);
        cs.command.put("-srcjavafolder", srcjavafolder);
        cs.command.put("-srctestfolder", srctestfolder);
        cs.command.put("-failing", failing);
        cs.command.put("-package", package_name);
        cs.command.put("-maxgen", "0");
        cs.command.put("-jvm4testexecution", "/mnt/vdb/jdk1.7.0_80/bin/");
	cs.command.put("-customengine", ZmEngine.class.getCanonicalName());
        cs.command.put("-parameters", "disablelog:false:logtestexecution:true");

        System.out.println(Arrays.toString(cs.flat()));
        main1.execute(cs.flat());

        ZmEngine zmengine = (ZmEngine) main1.getEngine();

        ProjectRepairFacade projectFacade = zmengine.getProjectFacade();
        ProjectConfiguration setUpProperties = projectFacade.getProperties();
        String projectRootDir = setUpProperties.getOriginalProjectRootDir();
        List<String> ingredientPool = getIngredients(projectRootDir);

        // Get the suspicious
        List<SuspiciousModificationPoint> susp = zmengine.getSuspicious();
        SuspiciousFile suspFile = null;
        String path_output = "/mnt/vdb/output_extension";
        int id = 1;
        PrintWriter writer = null;
        String path_to_patch;
	String path_to_diff;
        File patch = null;
	File diff = null;
        FileProgramVariant fvariant = null;
        VariantValidationResult resultValidation = null;
        Process p;
	String command;
        String line_output;
        StringBuffer output;
        BufferedReader reader;
	System.out.println("Number of SuspiciousModificationPoint: " + susp.size());

	long start = System.currentTimeMillis();
        long end = start + 2*60*60*1000;

        for(SuspiciousModificationPoint smp : susp)
        {
                suspFile = new SuspiciousFile(smp);
                Collections.sort(ingredientPool, new normalized_lcs_comparator(suspFile.getSuspiciousLine().trim()));
                List<String> original_allLines = suspFile.getAllLines();
                System.out.println("SuspiciousModificationPoint: " + suspFile.getSuspiciousLine());
		System.out.println("At: " + suspFile.getFileName()+" "+suspFile.getClassName());
		System.out.println("Line number: " + Integer.toString(suspFile.getSuspiciousLineNumber()));
                for(String ingredient : ingredientPool.subList(0,100))
                {
		    if(System.currentTimeMillis() >= end)
                    {
                        break;
                    }
                    System.out.println("Used ingredient: " + ingredient);
                    List<String> allLines = new ArrayList<String>(original_allLines);
		    allLines.set(suspFile.getSuspiciousLineNumber()-1, ingredient);
                    path_to_patch = path_output+File.separator+project+File.separator+(project+"_"+bugid)+File.separator+id+File.separator+suspFile.getFileName();
                    patch = new File(path_to_patch);
                    System.out.println(patch.getAbsolutePath());
                    patch.getParentFile().mkdirs();
                    patch.createNewFile();
                    writer = new PrintWriter(patch, "UTF-8");
                    for(String line : allLines)
                    {
                      writer.println(line);
                    }
		    writer.flush();
                    writer.close();

                    fvariant = new FileProgramVariant(id, suspFile.getClassName(), patch);
                    resultValidation = zmengine.validateInstance(fvariant);
                    if(resultValidation != null && resultValidation.isSuccessful())
                    {
                        System.out.println("Found patch for " + (project+"_"+bugid) + ", id: " + id);
                    	command = "diff -u " + smp.getCodeElement().getPosition().getFile().getAbsolutePath() + " " + patch.getAbsolutePath();
                        System.out.println("Execute command: " + command);
                        p = Runtime.getRuntime().exec(command);
                        p.waitFor();
                        
                        reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        output = new StringBuffer();
                        line_output = "";
                        while ((line_output = reader.readLine())!= null) {
                            output.append(line_output + "\n");
                        }
                        path_to_diff = path_output+File.separator+project+File.separator+(project+"_"+bugid)+File.separator+id+File.separator+"diff";
                        diff = new File(path_to_diff);
                        writer = new PrintWriter(diff, "UTF-8");
                        writer.print(output.toString());
                        writer.flush();
                        writer.close();
		    }else{
			patch.delete();
			patch.getParentFile().delete();
		    }
                    id++;
                }
		if(System.currentTimeMillis() >= end)
                {
                    break;
                }
        }
}

public static String getSuspiciousLine(SuspiciousModificationPoint smp) throws Exception
{
        File suspFile = smp.getCodeElement().getPosition().getFile();
        BufferedReader br = new BufferedReader(new FileReader(suspFile));
        int lineNumber = smp.getSuspicious().getLineNumber();
        for(int i = 0; i < lineNumber-1; ++i)
        {
                br.readLine();
        }
        String suspiciousLine = br.readLine();
        br.close();
        return suspiciousLine;
}

public static List<String> getIngredients(String projectRootDir) throws Exception
{
        HashSet<String> set = new HashSet<String>();
        BufferedReader br = null;
        String line;
        String lineStrip;
        List<String> ingredientPool = new ArrayList<String>();

        File rootDir = new File(projectRootDir);
        String[] extensions = new String[] { "java"};
        List<File> files = (List<File>)FileUtils.listFiles(rootDir, extensions, true);
        for(File file : files)
        {
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                        lineStrip = line.trim();
                        if(!set.contains(lineStrip))
                        {
                                ingredientPool.add(lineStrip);
                                set.add(lineStrip);
                        }
                }
                br.close();
        }
        return ingredientPool;
}

private static class normalized_lcs_comparator implements Comparator<String>
{
String suspiciousLineStrip;

public normalized_lcs_comparator(String suspiciousLineStrip)
{
        super();
        this.suspiciousLineStrip = suspiciousLineStrip;
}

public int compare(String a, String b)
{
        double a_score = normalized_lcs(suspiciousLineStrip, a);
        double b_score = normalized_lcs(suspiciousLineStrip, b);
        if(a_score < b_score)
        {
                return 1;
        }else if(a_score > b_score) {
                return -1;
        }else{
                return 0;
        }
}

//https://www.geeksforgeeks.org/longest-common-subsequence/
public double normalized_lcs(String a, String b)
{
        if(a.equals(b))
        {
          return 0;
        }

        char[] X = a.toCharArray();
        char[] Y = b.toCharArray();

        int m = X.length;
        int n = Y.length;

        int L[][] = new int[m+1][n+1];

        for (int i=0; i<=m; i++)
        {
                for (int j=0; j<=n; j++)
                {
                        if (i == 0 || j == 0)
                                L[i][j] = 0;
                        else if (X[i-1] == Y[j-1])
                                L[i][j] = L[i-1][j-1] + 1;
                        else
                                L[i][j] = max(L[i-1][j], L[i][j-1]);
                }
        }
        return (double)L[m][n]/max(m,n);
}

public int max(int a, int b)
{
        return (a > b) ? a : b;
}
}
}
