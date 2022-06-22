package info3.game.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

public class Assembly {
	private List<Item> items = new ArrayList<>();

	private Map<List<ItemType>, ItemType> ASSEMBLE_RECIPES = new LinkedHashMap<>() {
		private static final long serialVersionUID = 1L;

		{
			put(List.of(ItemType.SALAD_LEAF, ItemType.COOKED_POTATO), ItemType.POTATO_SALAD);
			put(List.of(ItemType.CHEESE, ItemType.CHEESE, ItemType.COOKED_FRIES), ItemType.POUTINE);
			put(List.of(ItemType.MASHED_POTATO, ItemType.COOKED_HASHED_MEAT), ItemType.SHEPHERDS_PIE);

			put(List.of(ItemType.COOKED_MEAT, ItemType.CHEESE, ItemType.BREAD_SLICE), ItemType.CLASSIC_BURGER);
			put(List.of(ItemType.COOKED_MEAT, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.SALAD_LEAF),
					ItemType.CLASSIC_BURGER_SALAD);
			put(List.of(ItemType.COOKED_MEAT, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.TOMATO_SLICE),
					ItemType.CLASSIC_BURGER_TOMATO);
			put(List.of(ItemType.COOKED_MEAT, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.SALAD_LEAF,
					ItemType.TOMATO_SLICE), ItemType.CLASSIC_BURGER_SALAD_TOMATO);

			put(List.of(ItemType.COOKED_GALETTE, ItemType.CHEESE, ItemType.BREAD_SLICE), ItemType.VEGI_BURGER);
			put(List.of(ItemType.COOKED_GALETTE, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.SALAD_LEAF),
					ItemType.VEGI_BURGER_SALAD);
			put(List.of(ItemType.COOKED_GALETTE, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.TOMATO_SLICE),
					ItemType.VEGI_BURGER_TOMATO);

			put(List.of(ItemType.COOKED_GALETTE, ItemType.CHEESE, ItemType.BREAD_SLICE, ItemType.SALAD_LEAF,
					ItemType.TOMATO_SLICE), ItemType.VEGI_BURGER_SALAD_TOMATO);
		}
	};

	private List<ItemType> getItemTypes() {
		return items.stream().map(item -> item.type).collect(Collectors.toList());
	}

	private static boolean includes(List<ItemType> a, List<ItemType> b) {
		b = new ArrayList<>(b);
		for (ItemType i : a) {
			if (!b.remove(i))
				return false;
		}
		return true;
	}

	public List<Item> getItems() {
		return items;
	}

	public void removeItem(Item item) {
		this.getItems().remove(item);
	}

	public void emptyAssembly() {
		this.getItems().clear();
	}

	private List<ItemType> getRecipe(ItemType item) {
		List<ItemType> tmp = new ArrayList<ItemType>();
		for (Entry<List<ItemType>, ItemType> entry : ASSEMBLE_RECIPES.entrySet()) {
			if (item.equals(entry.getValue())) {
				tmp = entry.getKey();
				return tmp;
			}
		}
		return null;
	}

	/**
	 * getRecipe
	 * 
	 * @implSpec : Decompose a recipe into simple ingredients, in order to compose
	 *           more complex recipe
	 */
	private Sauce explode() {
		ArrayList<Item> currentItems = new ArrayList<Item>(this.getItems());
		List<ItemType> tmp = new ArrayList<ItemType>();
		Sauce sauce = null;

		for (Item iter : currentItems) {
			if (iter.getType().isFinalItem()) {
				sauce = iter.getSauce();
			}
			if (iter.getType().isFinalItem()) {
				tmp = this.getRecipe(iter.getType());
				this.removeItem(iter);
				;
				for (ItemType iter2 : tmp) {
					this.getItems().add(new Item(iter2, null)); // on utilise pas addItem
				}
			}
		}
		return sauce;
	}

	private boolean equal(List<ItemType> l) {
		ArrayList<ItemType> x = new ArrayList<ItemType>(this.getItemTypes());
		ArrayList<ItemType> y = new ArrayList<ItemType>(l);
		if (y.size() != x.size()) {
			return false;
		}
		for (ItemType tmp : l) {
			y.remove(tmp);
			if (x.contains(tmp)) {
				x.remove(tmp);
			}
		}
		if (y.size() == x.size()) {
			return true;
		}
		return false;
	}

	public void addItem(Item item) {
		items.add(item);
		Sauce sauce = this.explode(); // on divise les potentielles recettes finales présentes dans le sac du
		// cuisinier
		if (this.getItems().size() == 1) {
			return;
		}
		List<ItemType> currentItems = getItemTypes();
		for (Entry<List<ItemType>, ItemType> entry : ASSEMBLE_RECIPES.entrySet()) {
			if (this.equal(entry.getKey())) { // on va former une recette
				this.emptyAssembly();
				this.getItems().add(new Item(entry.getValue(), sauce));
				return;
			} else if (includes(currentItems, entry.getKey())) {// on est en bonne voie
				return;
			}
		}
		this.emptyAssembly();
		this.getItems().add(new Item(ItemType.FAILED_Item));// on est en mauvaise voie
	}

	public void addAssembly(Assembly a) {
		java.util.Iterator<Item> i = a.getItems().iterator();
		while (i.hasNext()) {
			this.addItem(i.next());
		}

	}

	public Entry<List<ItemType>, ItemType> getRandomRecipe() {
		Random rand = new Random();
		int i = rand.nextInt(ASSEMBLE_RECIPES.size());
		int j = 0;
		for (Entry<List<ItemType>, ItemType> entry : ASSEMBLE_RECIPES.entrySet()) {
			if (i == j)
				return entry;
			j++;
		}
		throw new IllegalStateException("no recipe found");
	}

}