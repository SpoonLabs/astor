package fr.inria.astor.core.stats;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.io.Files;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class XMLStat {

	public static void saveResults(List<Stats> result, String out) {

		try {
			DecimalFormat df = new DecimalFormat("#.###");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document root = docBuilder.newDocument();
			Element rootElement = root.createElement("data");
			root.appendChild(rootElement);

			for (Stats statIteration : result) {

				Element run = root.createElement("run");
				rootElement.appendChild(run);

				Attr attr_id = root.createAttribute("id");
				attr_id.setValue(Integer.toString(statIteration.id));
				run.setAttributeNode(attr_id);

				Element ittime = root.createElement("time");
				run.appendChild(ittime);
				ittime.setTextContent(Long.toString(statIteration.timeIteraction));

				Element commitmess = root.createElement("numberOfRightCompilation");
				run.appendChild(commitmess);
				commitmess.setTextContent(Integer.toString(statIteration.numberOfRightCompilation));

				Element wcom = root.createElement("numberOfWrongCompilation");
				run.appendChild(wcom);
				wcom.setTextContent(Integer.toString(statIteration.numberOfFailingCompilation));

				Element g = root.createElement("numberGenerations");
				run.appendChild(g);
				g.setTextContent(Integer.toString(statIteration.numberGenerations));

				Element apOP = root.createElement("numberOfAppliedOp");
				run.appendChild(apOP);
				apOP.setTextContent(Integer.toString(statIteration.numberOfAppliedOp));

				Element napOP = root.createElement("numberOfNotAppliedOp");
				run.appendChild(napOP);
				napOP.setTextContent(Integer.toString(statIteration.numberOfNotAppliedOp));

				Element inmu = root.createElement("numberOfGenInmutated");
				run.appendChild(inmu);
				inmu.setTextContent(Integer.toString(statIteration.numberOfGenInmutated));

				Element pat = root.createElement("patches");
				run.appendChild(pat);
				// pat.setTextContent(Integer.toString(statIteration.patches));

				Attr attr_pat = root.createAttribute("number");
				attr_pat.setValue(Integer.toString(statIteration.patches));
				pat.setAttributeNode(attr_pat);

				for (StatPatch patchgen : statIteration.genPatches) {
					Element v = root.createElement("generation");
					pat.appendChild(v);
					v.setTextContent(Integer.toString(patchgen.generation));

					Attr attr_v1 = root.createAttribute("v1");
					attr_v1.setValue(Integer.toString(patchgen.val1));
					v.setAttributeNode(attr_v1);

					Attr attr_v2 = root.createAttribute("v2");
					attr_v2.setValue(Integer.toString(patchgen.val2));
					v.setAttributeNode(attr_v2);
				}

				Element v1 = root.createElement("val1");
				run.appendChild(v1);

				Attr s1 = root.createAttribute("size");
				s1.setValue(Integer.toString(statIteration.time1Validation.size()));
				v1.setAttributeNode(s1);

				for (Long l : statIteration.time1Validation) {
					Element v = root.createElement("time");
					v1.appendChild(v);
					v.setTextContent(Long.toString(l));
				}

				Element v2 = root.createElement("val2");
				run.appendChild(v2);

				Attr s2 = root.createAttribute("size");
				s2.setValue(Integer.toString(statIteration.time2Validation.size()));
				v2.setAttributeNode(s2);

				for (Long l : statIteration.time2Validation) {
					Element v = root.createElement("time");
					v2.appendChild(v);
					v.setTextContent(Long.toString(l));
				}
				Element sp = root.createElement("spaces");
				run.appendChild(sp);

				Attr s3 = root.createAttribute("size");
				s3.setValue(Integer.toString(statIteration.sizeSpace.size()));
				sp.setAttributeNode(s3);

				Attr fl = root.createAttribute("fl_size");
				fl.setValue(Integer.toString(statIteration.fl_size));
				sp.setAttributeNode(fl);

				Attr gens = root.createAttribute("gens");
				gens.setValue(Integer.toString(statIteration.fl_gens_size));
				sp.setAttributeNode(gens);

				Attr minsusp = root.createAttribute("minsusp");
				minsusp.setValue(df.format(statIteration.fl_threshold));
				sp.setAttributeNode(minsusp);

				for (StatSpaceSize l : statIteration.sizeSpace) {
					Element v = root.createElement("space");
					sp.appendChild(v);
					// v.setTextContent(Long.toString(l));

					Attr attr1 = root.createAttribute("ingredients");
					attr1.setValue(Integer.toString(l.ingredients));
					v.setAttributeNode(attr1);

					Attr attr2 = root.createAttribute("operations");
					attr2.setValue(Integer.toString(l.operations));
					v.setAttributeNode(attr2);

					Attr attr3 = root.createAttribute("size");
					attr3.setValue(Integer.toString(l.size()));
					v.setAttributeNode(attr3);
				}

			}

			// set attribute to staff element

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(root);
			// StreamResult result1 = new StreamResult(System.out);
			StreamResult result1 = new StreamResult(new File(out));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			// ----

			transformer.transform(source, result1);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	List<Integer> iterationTimeR = new ArrayList<Integer>();
	List<Integer> numberOfRightCompilationR = new ArrayList<Integer>();
	List<Integer> numberOfWrongCompilationR = new ArrayList<Integer>();
	List<Integer> numberOfAppliedOpR = new ArrayList<Integer>();
	List<Integer> numberGenerationsR = new ArrayList<Integer>();
	List<Integer> numberOfNotAppliedOpR = new ArrayList<Integer>();
	List<Integer> numberOfGenInmutatedR = new ArrayList<Integer>();

	List<Integer> numberPatchesR = new ArrayList<Integer>();
	List<Integer> generationMedianPatchesR = new ArrayList<Integer>();
	List<Integer> generationFirstPatchesR = new ArrayList<Integer>();

	List<Integer> patchesVal1R = new ArrayList<Integer>();
	List<Integer> patchesVal2R = new ArrayList<Integer>();

	List<Long> val1R = new ArrayList<Long>();
	List<Long> val2R = new ArrayList<Long>();
	List<Integer> spaceSizeR = new ArrayList<Integer>();
	List<Integer> ingredientsR = new ArrayList<Integer>();
	List<Integer> operatorsR = new ArrayList<Integer>();

	List<Integer> numberVal1R = new ArrayList<Integer>();
	List<Integer> numberVal2R = new ArrayList<Integer>();

	public void getPatchInformation(String statFile) throws Exception {
		patches.clear();
		File s = new File(statFile);
		File d = s.getParentFile();
		for (File itFolder : d.listFiles()) {
			if (itFolder.isFile())
				continue;
			for (File gpFolder : itFolder.listFiles()) {

				File patch = new File(gpFolder.getAbsolutePath() + File.separator + "patch.xml");
				if (patch.exists()) {
					processFilePatches(patch.getAbsolutePath());
					//copyPatch(patch);
				}
			}
		}

	}
	int i = 0;
	public void copyPatch(File patch){
		File patchDest = new File("c:/tmp/"+ "patch"+(i++)+".xml");
		System.out.println("coping "+patch+" to "+patchDest);	
		try {
			Files.copy(patch,patchDest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	List<String> patchesBug = new ArrayList<String>();

	Map<String, List<String>> patches = new HashMap<String, List<String>>();
	public String rowsct;
	public String rowsc;
	public String rowTimet;
	public String rowTime;
	public String rowvalidationt;
	public String rowvalidation;
	public String rowPatcht;
	public String rowPatch;
	public String rowSpacet;
	public String rowSpace;
	public String rowEffortTestt;
	public String rowEffortTest;

	private void processFilePatches(String absolutePath) throws Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(absolutePath);

		doc.getDocumentElement().normalize();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());

		NodeList runList = doc.getElementsByTagName("operation");
		Element eElement = (Element) runList.item(0);

		NodeList original = eElement.getElementsByTagName("original");
		String origString = original.item(0).getTextContent();

		NodeList mod = eElement.getElementsByTagName("modified");

		String location = eElement.getAttributes().getNamedItem("location").getNodeValue();
		String line = eElement.getAttributes().getNamedItem("line").getNodeValue();
		String modif = mod.item(0).getTextContent();

		String keyn=origString + ";" + modif+";"+location+";"+line;
		
		if (!patchesBug.contains(keyn)) {
			patchesBug.add(keyn);
		}

		String key = location + "-" + line;
		List<String> list = patches.get(key);
		if (list == null) {
			list = new ArrayList<String>();
			patches.put(key, list);
		}
		if (!list.contains(modif)) {
			list.add(modif);
		}
	}

	public void processStats(String statFile) throws Exception {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(statFile);

		doc.getDocumentElement().normalize();

		// System.out.println("Root element :" +
		// doc.getDocumentElement().getNodeName());

		NodeList runList = doc.getElementsByTagName("run");
		getPatchInformation(statFile);
		// System.out.println("----------------------------");

		for (int temp = 0; temp < runList.getLength(); temp++) {

			Node run = runList.item(temp);

			// System.out.println("\nCurrent Element :" +
			// run.getNodeName()+" "+temp);

			if (run.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) run;

				NodeList time = eElement.getElementsByTagName("time");

				NodeList numberOfRightCompilation = eElement.getElementsByTagName("numberOfRightCompilation");
				NodeList numberOfWrongCompilation = eElement.getElementsByTagName("numberOfWrongCompilation");
				NodeList numberGenerations = eElement.getElementsByTagName("numberGenerations");
				NodeList numberOfAppliedOp = eElement.getElementsByTagName("numberOfAppliedOp");
				NodeList numberOfNotAppliedOp = eElement.getElementsByTagName("numberOfNotAppliedOp");
				NodeList numberOfGenInmutated = eElement.getElementsByTagName("numberOfGenInmutated");
				NodeList patches = eElement.getElementsByTagName("patches");

				// numberOfRightCompilationR.add(numberOfWrongCompilation.item(0).getTextContent());
				addResult(time, iterationTimeR);
				addResult(numberOfWrongCompilation, numberOfWrongCompilationR);
				addResult(numberOfRightCompilation, numberOfRightCompilationR);
				addResult(numberGenerations, numberGenerationsR);
				addResult(numberOfAppliedOp, numberOfAppliedOpR);
				addResult(numberOfNotAppliedOp, numberOfNotAppliedOpR);
				addResult(numberOfGenInmutated, numberOfGenInmutatedR);
				addNumberPatcher(patches, numberPatchesR);

				List<Long> v1temp = new ArrayList<Long>();
				Long m1 = getTimeMedianForRun(eElement, "val1", v1temp);
				if (m1 != null)
					val1R.add(m1);

				List<Long> v2temp = new ArrayList<Long>();
				Long m2 = getTimeMedianForRun(eElement, "val2", v2temp);
				if (m2 != null)
					val2R.add(m2);

				List<Integer> gps = new ArrayList<Integer>();
				List<Integer> patchv1 = new ArrayList<Integer>();
				List<Integer> patchv2 = new ArrayList<Integer>();
				Integer[] generPatch = getGenerationMedianForPatches(eElement, gps, patchv1, patchv2);
				if (!gps.isEmpty()) {
					generationMedianPatchesR.add(generPatch[0]);
					generationFirstPatchesR.add(gps.get(0));
					patchesVal1R.add(generPatch[1]);
					patchesVal2R.add(generPatch[2]);
				}

				int[] ms = getSpace(eElement);
				spaceSizeR.add(ms[0]);
				operatorsR.add(ms[1]);
				ingredientsR.add(ms[2]);

				numberVal1R.add(getNumberValidation(eElement, "val1"));
				numberVal2R.add(getNumberValidation(eElement, "val2"));

			}
		}
		int time = getMedian(iterationTimeR);
		// System.out.println("time "+time);
		int rightcomp = getMedian(numberOfRightCompilationR);
		// System.out.println("numberOfRightCompilationR "+rightcomp);
		int wrongcomp = getMedian(numberOfWrongCompilationR);
		// System.out.println("numberOfWrongCompilation "+wrongcomp);
		int generation = getMedian(numberGenerationsR);
		// System.out.println("numberGenerationsR " +generation);
		int appliedOp = getMedian(numberOfAppliedOpR);
		// System.out.println("numberOfAppliedOp "+appliedOp);
		int notAppliedOp = getMedian(numberOfNotAppliedOpR);
		// System.out.println("numberOfNotAppliedOp "+notAppliedOp);
		int genInmut = getMedian(numberOfGenInmutatedR);
		// System.out.println("numberOfGenInmutated "+genInmut);

		int positivePatches = getNumberPositive(numberPatchesR);
		
		int medPatches = getMedian(numberPatchesR);
		int allPatches = getSum(numberPatchesR);
		double averPatches = getAver(numberPatchesR);
		int distinctPatches = getDistincPatches();
		int medpatchVal1 = getMedian(patchesVal1R);
		int medpatchVal2 = getMedian(patchesVal2R);

		int generationMedianPatches = ((generationMedianPatchesR.size() == 0) ? 0 : getMedian(generationMedianPatchesR));
		int generationFirstPatches = ((generationFirstPatchesR.size() == 0) ? 0 : getMedian(generationFirstPatchesR));

		long medTimeVal1 = (val1R.size() == 0) ? 0 : getMedianLong(val1R);
		// System.out.println("val1 "+medTimeVal1);
		long medTimeVal2 = (val2R.size() == 0) ? 0 : getMedianLong(val2R);
		// System.out.println("val2 "+medTimeVal2);
		int spaceSize = getMedian(spaceSizeR);
		// System.out.println("space "+space);

		int operatorSize = getMedian(operatorsR);
		int ingredientSize = getMedian(ingredientsR);
		//
		int numberVal1 = getMedian(numberVal1R);
		int numberVal2 = getMedian(numberVal2R);
		int allVal1 = getSum(numberVal1R);
		int allVal2 = getSum(numberVal2R);

		// ---
		// System.out.println("Code");

		rowsct = ("\\#RightCompilation &" + "\\#WrongCompilation &" + "\\#Generations &" + "\\#OfAppliedOp &"
				+ "\\#NotAppliedOp &" + "\\#GenInmutated "
		// + "&"+"val1 "+"& val2 "+"& space "
		);

		rowsc = (/* "numberOfRightCompilationR &" */rightcomp + "&" +
		/* "numberOfWrongCompilation &" */wrongcomp
		// /*"numberGenerationsR &"*/+"&"+generation
				/* "numberOfAppliedOp &" */+ "&" + appliedOp
				/* "numberOfNotAppliedOp &" */+ "&" + notAppliedOp
				/* "numberOfGenInmutated &" */+ "&" + genInmut
		// /*"val1 "*/+"&"+medTimeVal1
		// /*"& val2 "*/+"&"+medTimeVal2
		// /*"& space "*/ +"&"+space
		);

		// System.out.println("--Time");

		rowTimet = ("time &" + "t val1 " + "& t val2 "
		// +"& space "
		);

		rowTime = (
		/* time" */"&" + time
		/* "val1 " */+ "&" + medTimeVal1
		/* "& val2 " */+ "&" + medTimeVal2
		// /*"& space "*/ +"&"+spaceSize
		);

		// ----
		// System.out.println("--#Validations");
		rowvalidationt = ("Median \\#val1 &Total1&Median \\#val2&Total2 ");
		rowvalidation = (
		/* "val1 " */numberVal1 + "&" + allVal1
		/* "& val2 " */+ "&" + numberVal2 + "&" + allVal2);

		// ---Patches
		// System.out.println("--Patches");
		rowPatcht = (
		"distinct&" + " #runs with at least one repair ");//"median & " + "Avg &" + " median First gen patch");
		rowPatch =
		//distinctPatches + "&" + medPatches + "&" + averPatches;
				
		distinctPatches + "&" + //positivePatches +"&"
		 ((positivePatches>0)?((positivePatches)+"/100 ("+ (positivePatches) +"\\%)"):"-") ;
		

		// System.out.println("--Space");
		rowSpacet = (
		// "space " +
		" &operators" + "&ingredients");

		rowSpace = (
		// spaceSize
		// +
		"&" + operatorSize + "&" + ingredientSize);

		// ----
		rowEffortTestt = "med effort_v1 &med effort_v2";
		rowEffortTest = medpatchVal1 + "&" + medpatchVal2;
		int i = 1;
		for (String s : patchesBug) {
			System.out.println("FIX "+(i++)+": ");
			String[] s1 = s.split(";");
			System.out.println("buggy if: "+s1[0]);
			System.out.println("fixed if: "+s1[1]);
			System.out.println("location: "+s1[2]+ ", line: "+s1[3]);
			//System.out.println("::");
			System.out.println("---");
		}
	}

	public int getDistincPatches() {

		int d = 0;
		for (List l : patches.values()) {
			//System.out.println("---" + l);

			d += l.size();
		}
		return d;
	}

	public Integer[] getGenerationMedianForPatches(Element eElement, List<Integer> numberPatches,
			List<Integer> numberVal1Patches, List<Integer> numberVal2Patches) {
		NodeList val1List = ((Element) eElement.getElementsByTagName("patches").item(0))
				.getElementsByTagName("generation");
		for (int t1 = 0; t1 < val1List.getLength(); t1++) {
			Node var1 = val1List.item(t1);
			numberPatches.add(Integer.valueOf(var1.getTextContent()));
			Element e = (Element) var1;
			numberVal1Patches.add(Integer.valueOf(e.getAttributes().getNamedItem("v1").getNodeValue()));
			numberVal2Patches.add(Integer.valueOf(e.getAttributes().getNamedItem("v2").getNodeValue()));

		}
		if (numberPatches.size() == 0)
			return null;

		return new Integer[] { getMedian(numberPatches), getMedian(numberVal1Patches), getMedian(numberVal2Patches) };
	}

	public Long getTimeMedianForRun(Element eElement, String tag, List<Long> v1temp) {
		NodeList val1List = ((Element) eElement.getElementsByTagName(tag).item(0)).getElementsByTagName("time");
		for (int t1 = 0; t1 < val1List.getLength(); t1++) {

			Node var1 = val1List.item(t1);
			// System.out.println(var1.getTextContent());
			v1temp.add(Long.valueOf(var1.getTextContent()));
		}
		if (v1temp.size() == 0) {
			return null;
		}
		return getMedianLong(v1temp);
	}

	public int getNumberValidation(Element eElement, String tag) {
		NodeList val1List = ((Element) eElement.getElementsByTagName(tag).item(0)).getElementsByTagName("time");
		return val1List.getLength();
	}

	public int[] getSpace(Element eElement) {
		List<Integer> spaceSize = new ArrayList<Integer>();
		List<Integer> ops = new ArrayList<Integer>();
		List<Integer> ingredients = new ArrayList<Integer>();
		NodeList val1List = ((Element) eElement.getElementsByTagName("spaces").item(0)).getElementsByTagName("space");
		for (int t1 = 0; t1 < val1List.getLength(); t1++) {

			Node var1 = val1List.item(t1);
			// System.out.println(var1.getTextContent());
			String size = var1.getAttributes().getNamedItem("size").getNodeValue();
			spaceSize.add(Integer.valueOf(size));

			String operationsS = var1.getAttributes().getNamedItem("operations").getNodeValue();
			ops.add(Integer.valueOf(operationsS));

			String ingredientsS = var1.getAttributes().getNamedItem("ingredients").getNodeValue();
			ingredients.add(Integer.valueOf(ingredientsS));

		}
		return new int[] { getMedian(spaceSize), getMedian(ops), getMedian(ingredients) };
	}

	private void addResult(NodeList listNode, List<Integer> l) {
		l.add(Integer.valueOf(listNode.item(0).getTextContent()));
	}

	private void addNumberPatcher(NodeList listNode, List<Integer> l) {
		String size = listNode.item(0).getAttributes().getNamedItem("number").getNodeValue();
		l.add(Integer.valueOf(size));
	}

	private int getMedian(List<Integer> l) {
		Collections.sort(l);
		if (l.isEmpty())
			return 0;
		int middle = l.size() / 2;
		int medianValue = 0; // declare variable
		if (l.size() % 2 == 1)
			medianValue = l.get(middle);
		else
			medianValue = (l.get(middle - 1) + l.get(middle)) / 2;

		return medianValue;
	}

	private int getNumberPositive(List<Integer> l) {
		
		if (l.isEmpty())
			return 0;
		int numberPositives = 0;
		for (Integer patchesPerRun : l) {
			if(patchesPerRun > 0){
				numberPositives++;
			}
		}
		return numberPositives;
	}
	
	private int getSum(List<Integer> l) {
		Collections.sort(l);
		int sum = 0;
		for (Integer i : l) {
			sum += i;
		}
		return sum;
	}

	private double getAver(List<Integer> l) {
		if (l.isEmpty())
			return 0;
		// Collections.sort(l);
		int sum = 0;
		for (Integer i : l) {
			sum += i;
		}
		return ((double) sum) / l.size();
	}

	private long getMedianLong(List<Long> l) {
		Collections.sort(l);
		// TODO: par case
		if (l.isEmpty())
			return 0;

		int middle = l.size() / 2;
		long medianValue = 0; // declare variable
		if (l.size() % 2 == 1)
			medianValue = l.get(middle);
		else
			medianValue = (l.get(middle - 1) + l.get(middle)) / 2;

		return medianValue;
	}

	@Test
	public void results() throws Exception {

		Map<String, Integer> result = new HashMap<String, Integer>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder
				.parse("C:\\Personal\\inria\\sachaPublications\\2014-matias-git\\fixing2014\\data\\if-updates\\IFMATH-ALL.xml");

		doc.getDocumentElement().normalize();

		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

		NodeList nList = doc.getElementsByTagName("commitFile");

		System.out.println("----------------------------");

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);

			// System.out.println("\nCurrent Element :" +
			// nNode.getNodeName()+" "+temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				NodeList atrs = eElement.getElementsByTagName("actions");

				NodeList atr = ((Element) atrs.item(0)).getElementsByTagName("action");

				// For each action
				for (int temp1 = 0; temp1 < atr.getLength(); temp1++) {

					Node atti = atr.item(temp1);
					// System.out.println("attr"+atti );
					// System.out.println("pattern : " + ((Element)
					// atti).getElementsByTagName("pattern").item(0).getTextContent());
					NodeList pats = ((Element) atti).getElementsByTagName("pattern");

					for (int pat = 0; pat < pats.getLength(); pat++) {
						Node pati = pats.item(pat);
						String patContent = pati.getTextContent();

						if (!patContent.equals("-")) {
							Integer oc = result.get(patContent);
							if (oc == null) {
								result.put(patContent, 1);
							} else
								result.put(patContent, oc + 1);
						}
					}
				}

			}
		}
		System.out.println(result);
		for (String key : result.keySet()) {
			System.out.println(key + "&" + result.get(key) + "\\\\");
		}
	}

}
