package fr.inria.astor.approaches.tos.entity;

import fr.inria.astor.util.MapCounter;
import fr.inria.astor.util.MapList;
import fr.inria.astor.util.Probability;
import spoon.reflect.declaration.CtElement;

/**
 * Counts and keeps track of the occurrence of a TOS.
 * 
 * @author Matias Martinez
 *
 */
public class TOSCounter {
	// Maps the Tos with the element where it was mined from.
	private MapList<String, CtElement> tosToCtElement = new MapList<>();
	// Stores the nr occurrence of each element
	private MapCounter<String> tosOcurrenceCounter = new MapCounter<>();
	// stores the probability of each element
	private Probability<String> probabilitiesTOS = null;

	public TOSCounter() {
		super();
	}

	public TOSCounter(MapList<String, CtElement> linkTemplateElements, MapCounter<String> linkTemplateCounter) {
		super();
		this.tosToCtElement = linkTemplateElements;
		this.tosOcurrenceCounter = linkTemplateCounter;
	}

	public MapList<String, CtElement> getLinkTemplateElements() {
		return tosToCtElement;
	}

	public MapCounter<String> getTosOcurrenceCounter() {
		return tosOcurrenceCounter;
	}

	public Probability<String> getProbabilitiesTOS() {
		if (probabilitiesTOS == null) {
			this.probabilitiesTOS = this.tosToCtElement.getSorted().getProb();
		}
		return probabilitiesTOS;
	}

	public void saveStatisticsOfTos(TOSEntity templateElement, CtElement originalIngredient) {
		String templateString = templateElement.getChacheCodeString();
		this.tosOcurrenceCounter.add(templateString);
		this.tosToCtElement.add(templateString, originalIngredient);

	}
}
