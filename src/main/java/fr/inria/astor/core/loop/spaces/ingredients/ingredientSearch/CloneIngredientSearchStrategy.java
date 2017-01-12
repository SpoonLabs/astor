package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * A strategy to pick an ingredient from the fix space using code fragments' similarities.
 * 
 * @author Martin White
 *
 */
public class CloneIngredientSearchStrategy<T extends CtNamedElement> extends EfficientIngredientStrategy {
	
	private Class<T> cls;
	private TypeFilter<T> filter;
	private List<String> distances; // Each item is a string of comma-separated values representing distances.
	private Map<String, Integer> key2row; // String is row from src2txt. Integer is index into this.distances.
	private Map<Integer, String> row2key;
	private Map<String, T> key2element = new HashMap<>(); // Elements in scope.
	private Map<T, List<T>> element2simlist = new HashMap<>(); // Cache of elements' similarity lists.
	
	public CloneIngredientSearchStrategy(@SuppressWarnings("rawtypes") IngredientSpace space) throws ClassNotFoundException, IOException {
		super(space);
		setfilter();
		readinput();
	}
	
	@SuppressWarnings("unchecked")
	private void setfilter() throws ClassNotFoundException {
		cls = (Class<T>) Class.forName(ConfigurationProperties.properties.getProperty("cloneclass"));
		if (cls.equals(CtType.class) || cls.equals(CtExecutable.class)) {
			filter = new TypeFilter<T>(cls) {
				@Override
				public boolean matches(T element) {
					// Definition of "top level" T.
					return element.getParent(cls) == null && !element.isImplicit() && !(element instanceof CtAnonymousExecutable);
				}
			};
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private void readinput() throws IOException {
		// Read distance vectors stored in distances.csv file (e.g., executables.distances.csv).
		distances = read(Input.DISTANCES.getpath()); // Distance vectors will be parsed on demand.
		
		// Read keys stored in key file (e.g., executables.key).
		List<String> keys = read(Input.KEYS.getpath());
		
		// Read map stored in map file (e.g., executables.map).
		// The map file contains one record of comma-separated values.
		List<String> values = Arrays.asList(read(Input.MAP.getpath()).get(0).split(","));
		
		// We need to subtract 1 because MATLAB is 1-indexed.
		List<Integer> map = new ArrayList<>();
		values.forEach(value -> map.add(Integer.valueOf(value) - 1));
		
		// We randomly permuted the rows of the {executables,types}.int corpora before encoding them.
		// Therefore, to get the row for a row in the distance matrix, the row number needs to be mapped to the row.
		String key;
		key2row = new HashMap<>();
		row2key = new HashMap<>();
		for (int row = 0; row < map.size(); row++) {
			key = keys.get(map.get(row));
			if (key2row.containsKey(key)) {
				log.error("Duplicate key: " + key);
				throw new RuntimeException();
			}
			key2row.put(key, row);
			row2key.put(row, key);
		}
		log.debug("Number of keys loaded: " + key2row.keySet().size());
	}
	
	private List<String> read(Path path) throws IOException {
		try (Stream<String> stream = Files.lines(path)) {
			return stream.map(String::trim).collect(Collectors.toList());
		}
	}
	
	@Override
	public Ingredient getFixIngredient(ModificationPoint mp, AstorOperator op) {
		// TODO Move to constructor when Spoon model building happens before ingredient strategy is initialized.
		if (key2element.isEmpty())
			key2element = queryelements().orElseThrow(RuntimeException::new); // Or use EfficientIngredientStrategy.
		
		T suspicious = mp.getCodeElement().getParent(filter);
		
		if (suspicious == null) {
			// TODO Count number of times modification point does not map to "top level" T.
			log.info("Modification point does not map to \"top level\" " + ConfigurationProperties.properties.getProperty("cloneclass") + ": " + mp);
			return super.getFixIngredient(mp, op); // Use EfficientIngredientStrategy for this modification instance.
		}
		
		String key = getkey(suspicious);
		
		if (!key2element.containsKey(key)) {
			log.error("Suspicious element is not in scope: " + key);
			throw new RuntimeException(); //return super.getFixIngredient(mp, op);
		}
		
		// element2simlist is a cache of element-specific similarity lists.
		if (!element2simlist.containsKey(suspicious))
			computesimlist(suspicious);
		
		Queue<CtCodeElement> fixspace = getfixspace(mp, op, suspicious);
		log.debug("Fix space is empty? " + fixspace.isEmpty());
		if (fixspace.isEmpty())
			return super.getFixIngredient(mp, op);
		
		boolean continueSearching = true;
		CtElement ingredient;
		boolean alreadyApplied;
		
		while (continueSearching) {
			ingredient = getingredient(fixspace);
			
			if (ingredient == null) {
				// TODO Count number of times similarity heuristic is exhausted.
				log.info("Exhausted similarity heuristic.");
				break; // Use EfficientIngredientStrategy.
			}
			
			alreadyApplied = alreadySelected(mp, ingredient, op);
			
			if (!alreadyApplied && !ingredient.equals(mp.getCodeElement()))
				continueSearching = !VariableResolver.fitInPlace(mp.getContextOfModificationPoint(), ingredient);
			
			if (!continueSearching) {
				IngredientSpaceScope scope = determineIngredientScope(mp.getCodeElement(), ingredient);
				return new Ingredient(ingredient, scope);
			}
		}
		
		return super.getFixIngredient(mp, op);
	}
	
	private String getkey(T element) {
		String key;
		if (element instanceof CtExecutable) {
			key = element.getParent(CtType.class).getQualifiedName() + "#" + ((CtExecutable<?>) element).getSignature();
		} else if (element instanceof CtType) {
			key = ((CtType<?>) element).getQualifiedName();
		} else {
			throw new IllegalArgumentException();
		}
		return key;
	}
	
	@SuppressWarnings({ "unchecked" })
	public Optional<Map<String, T>> queryelements() {
		// Use ingredient space to get locations.
		List<CtElement> locations = getIngredientSpace().getLocations();
		log.debug("Number of locations: " + locations.size());
		
		// Use locations to get T elements.
		Map<String, T> elements = locations.stream()
				.flatMap(l -> l.getElements(filter).stream())
				.collect(Collectors.toMap(e -> getkey((T) e), e -> e));
		log.debug("Number of \"top level\" elements: " + elements.size());
		
		Set<String> orphans = new HashSet<>(elements.keySet());
		orphans.removeAll(key2row.keySet());
		if (!orphans.isEmpty()) {
			log.error("Number of \"top level\" elements that do not have a src2txt key: " + orphans.size());
			log.error(orphans.stream().collect(Collectors.joining(",")));
			throw new RuntimeException();
		}
		
		return Optional.of(elements);
	}
	
	private void computesimlist(T element) {
		String key = getkey(element);
		int row = key2row.get(key); // Get row of distance matrix.
		String[] values = distances.get(row).split(",");
		
		// The distance matrix should be square (for now).
		if (key2row.size() != values.length) {
			log.error(String.format("Distance vector on line %d has wrong dimension.", row+1)); // row is 0-indexed so we add 1.
			throw new RuntimeException(); // Or use EfficientIngredientStrategy.
		}
		
		List<Distance> distances = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
			distances.add(new Distance(i, Double.parseDouble(values[i])));
		
		List<T> simlist = distances.stream()
				// Sort Distance instances in ascending order.
				.sorted(Distance::compareTo)
				// Map sorted values to row numbers.
				.map(Distance::getRow)
				// Map row numbers to keys.
				.map(row2key::get)
				// Map keys to elements.
				.map(key2element::get)
				// Some code fragments are out of scope.
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		
		// Store the sorted list of elements that will be used to process ingredients in a specific order.
		element2simlist.put(element, simlist);
	}
	
	// TODO Use a Spoon processor to collect the statements.
	public Queue<CtCodeElement> getfixspace(ModificationPoint mp, AstorOperator op, T suspicious) {		
		List<CtStatement> statements = null; // Statements (i.e., ingredients) extracted from elements.
		String type = mp.getCodeElement().getClass().getSimpleName(); // The modification point's type in case we selected the ReplaceOp.
		Queue<CtCodeElement> fixspace = new LinkedList<>(); // FIFO queue where ingredients from similar elements are queued first.
		
		// Get the list of elements sorted by similarity.
		List<T> simlist = element2simlist.get(suspicious);
		for (T element : simlist) {
			if (element instanceof CtType) {
				// Astor uses block reification.
				List<CtBlock<?>> blocks = element.getElements(new TypeFilter<CtBlock<?>>(CtBlock.class));
				if (blocks == null)
					continue;
				statements = blocks.stream().flatMap(b -> b.getStatements().stream()).collect(Collectors.toList());
			}
			
			if (element instanceof CtExecutable) {
				// If the element doesn't have a body then it won't have ingredients so skip it.
				if (((CtExecutable<?>) element).getBody() == null)
					continue;
				statements = ((CtExecutable<?>) element).getBody().getStatements();
			}
			
			// If the element has a body but it doesn't have ingredients then skip it.
			if (statements == null)
				continue;
			
			// If we intend to replace the suspicious statement then we can only select statements of the same type.
			statements.removeIf(s -> op instanceof ReplaceOp && !s.getClass().getSimpleName().equals(type));
			
			// Blocks of statements are added to the queue according to how similar the parent element is to the suspicious element.
			// We can use a queue because the operator is fixed in the modification instance.
			if (statements != null)
				fixspace.addAll(statements);
		}
		
		return fixspace;
	}
	
	private CtCodeElement getingredient(Queue<CtCodeElement> fixspace) {
		CtCodeElement element = fixspace.poll();
		if (element == null) // ??? Can .clone receive null?
			return null;
		return MutationSupporter.clone(element); // ??? Is it necessary to clone here?
	}
}

enum Input {
	DISTANCES(".distances.csv"),
	KEYS(".key"),
	MAP(".map");
	
	private final String learningdir; // Contains learning artifacts.
	private final String granularity;
	private final String extension;
	
	Input(String extension) {
		learningdir = ConfigurationProperties.properties.getProperty("learningdir");
		granularity = ConfigurationProperties.properties.getProperty("clonegranularity");
		this.extension = extension;
	}
	
	public Path getpath() {
		return Paths.get(learningdir + File.separator + granularity + extension);
	}
}

class Distance implements Comparable<Distance> {
	private int row;
	private Double value; // TODO double
	
	public Distance(int row, Double value) {
		this.row = row;
		this.value = value;
	}
	
	public int getRow() {
		return row;
	}
	
	public Double getValue() {
		return value;
	}
	
	@Override
	public int compareTo(Distance distance) {
		return this.value.compareTo(distance.getValue());
	}
}
