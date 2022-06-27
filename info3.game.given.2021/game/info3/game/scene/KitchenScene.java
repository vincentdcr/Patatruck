package info3.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import info3.game.Game;
import info3.game.content.Item;
import info3.game.content.ItemType;
import info3.game.content.Sauce;
import info3.game.entity.BasicTableTile;
import info3.game.entity.CockroachEntity;
import info3.game.entity.CookEntity;
import info3.game.entity.CookTile;
import info3.game.entity.CutTile;
import info3.game.entity.Entity;
import info3.game.entity.FrieTile;
import info3.game.entity.KitchenDeliveryTile;
import info3.game.entity.SauceTableTile;
import info3.game.entity.StockTable;
import info3.game.entity.Tile;
import info3.game.entity.TrashTile;
import info3.game.graphics.Graphics;
import info3.game.graphics.Graphics.Align;
import info3.game.graphics.Sprite;
import info3.game.position.AutDirection;
import info3.game.position.PositionF;
import info3.game.position.PositionI;

public class KitchenScene extends Scene {

	public Tile[][] kitchenGrid;

	// Position
	private static final PositionI KITCHEN_ORIGIN = new PositionI(44, 10);

	// Tables de stockage
	private final HashMap<ItemType, StockTable> stockTables = new HashMap<>();
	private final ArrayList<PositionF> indexStockEmplacements = new ArrayList<>(List.of(new PositionF(0, 6),
			new PositionF(0, 7), new PositionF(0, 8), new PositionF(3, 6), new PositionF(3, 7), new PositionF(3, 8)));

	// Les deux commandes simultanées
	public Item currentOrder0;
	public Item currentOrder1;

	// Les deux entités qui bougent dans la cuisine
	private final CookEntity cook;

	// Gestion de la fumée
	public boolean smoke = false;
	public boolean smokeFryingOil = false;
	public int smokeCounter = 200;

	public boolean deliveryInThisTick;

	private static final ArrayList<PositionF> DEFAULT_LOCATIONS = new ArrayList<>(List.of(new PositionF(0, 1),
			new PositionF(0, 2), new PositionF(0, 3), new PositionF(0, 4), new PositionF(0, 5), new PositionF(3, 1),
			new PositionF(3, 2), new PositionF(3, 3), new PositionF(3, 4), new PositionF(3, 5)));

	public KitchenScene(int pixelWidth, int pixelHeight, Game g) {
		super(pixelWidth, pixelHeight, g);
		cook = new CookEntity(this,
				new PositionF(KITCHEN_ORIGIN.getX() + getTileWidth(), KITCHEN_ORIGIN.getY() + getTileWidth()));
		addEntity(cook);

		deliveryInThisTick = false;

		currentOrder0 = Item.getRandomItem();
		currentOrder1 = Item.getRandomItem();
		game.setTimer();

		// Construction de la grille
		kitchenGrid = new Tile[][] {
				new Tile[] { new BasicTableTile(this, 0, 0, AutDirection.S),
						new BasicTableTile(this, 1, 0, AutDirection.S), new BasicTableTile(this, 2, 0, AutDirection.S),
						new FrieTile(this, 3, 0, AutDirection.S), new FrieTile(this, 4, 0, AutDirection.S),
						new CutTile(this, 5, 0, AutDirection.S),
						new StockTable(this, 6, 0, AutDirection.S, new Item(ItemType.TOMATO), Sprite.TOMATO),
						new StockTable(this, 7, 0, AutDirection.S, new Item(ItemType.POTATO), Sprite.POTATO),
						new StockTable(this, 8, 0, AutDirection.S, new Item(ItemType.CHEESE), Sprite.CHEESE),
						new BasicTableTile(this, 9, 0, AutDirection.S) },
				new Tile[] { new KitchenDeliveryTile(this, 0, 1, AutDirection.E), null, null, null, null, null, null,
						null, null, new SauceTableTile(this, 9, 1, AutDirection.W, Sauce.KETCHUP) },
				new Tile[] { new KitchenDeliveryTile(this, 0, 2, AutDirection.E), null, null, null, null, null, null,
						null, null, new SauceTableTile(this, 9, 2, AutDirection.W, Sauce.MAYO) },
				new Tile[] { new BasicTableTile(this, 0, 3, AutDirection.N), new TrashTile(this, 1, 3, AutDirection.N),
						new BasicTableTile(this, 2, 3, AutDirection.N), new CookTile(this, 3, 3, AutDirection.N),
						new CookTile(this, 4, 3, AutDirection.N), new CutTile(this, 5, 3, AutDirection.N),
						new StockTable(this, 6, 3, AutDirection.N, new Item(ItemType.SALAD), Sprite.SALADE),
						new StockTable(this, 7, 3, AutDirection.N, new Item(ItemType.BREAD), Sprite.BREAD),
						new StockTable(this, 8, 3, AutDirection.N, new Item(ItemType.MEAT), Sprite.MEAT),
						new BasicTableTile(this, 9, 3, AutDirection.N) } };
		game.setTimer();
		stockTables.put(ItemType.TOMATO, (StockTable) kitchenGrid[0][6]);
		stockTables.put(ItemType.POTATO, (StockTable) kitchenGrid[0][7]);
		stockTables.put(ItemType.CHEESE, (StockTable) kitchenGrid[0][8]);
		stockTables.put(ItemType.SALAD, (StockTable) kitchenGrid[3][6]);
		stockTables.put(ItemType.BREAD, (StockTable) kitchenGrid[3][7]);
		stockTables.put(ItemType.MEAT, (StockTable) kitchenGrid[3][8]);
	}

	/**
	 * Echange aléatoirement les KitchenTiles entre elles
	 */
	public void shuffle() {
		game.playSound("kitchen_shuffle");
		// generate future emplacements
		Random rdm = new Random();
		int[] movedTiles = new int[10], movedStockTables = new int[6];
		for (int i = 0; i < movedTiles.length; i++) {
			boolean acceptable;
			do {
				acceptable = true;
				movedTiles[i] = rdm.nextInt(movedTiles.length);
				for (int y = 0; y < i; y++) {
					if (movedTiles[i] == movedTiles[y]) {
						acceptable = false;
						break;
					}
				}
			} while (!acceptable);
		}
		for (int i = 0; i < movedStockTables.length; i++) {
			boolean acceptable;
			do {
				acceptable = true;
				movedStockTables[i] = rdm.nextInt(movedStockTables.length);
				for (int y = 0; y < i; y++) {
					if (movedStockTables[i] == movedStockTables[y]) {
						acceptable = false;
						break;
					}
				}
			} while (!acceptable);
		}
		Tile[][] futureKitchenGrid = new Tile[][] {
				new Tile[] { kitchenGrid[0][0], null, null, null, null, null, null, null, null, kitchenGrid[0][9] },
				new Tile[] { kitchenGrid[1][0], null, null, null, null, null, null, null, null, kitchenGrid[1][9] },
				new Tile[] { kitchenGrid[2][0], null, null, null, null, null, null, null, null, kitchenGrid[2][9] },
				new Tile[] { kitchenGrid[3][0], null, null, null, null, null, null, null, null, kitchenGrid[3][9] } };
		// replace everything
		for (int i = 0; i < movedTiles.length; i++) {
			PositionF pos = DEFAULT_LOCATIONS.get(i), futurePos = DEFAULT_LOCATIONS.get(movedTiles[i]);
			Tile toMove = kitchenGrid[(int) pos.getX()][(int) pos.getY()];
			toMove.setPosition(new PositionF(futurePos.getY(), futurePos.getX()).mul(this.getTileWidth())
					.sub(this.getOriginOffset()));
			if (futurePos.getX() == 0) {
				toMove.setDirection(AutDirection.S);
			} else {
				toMove.setDirection(AutDirection.N);
			}

			futureKitchenGrid[(int) futurePos.getX()][(int) futurePos.getY()] = toMove;
		}
		for (int i = 0; i < movedStockTables.length; i++) {
			PositionF pos = indexStockEmplacements.get(i), futurePos = indexStockEmplacements.get(movedStockTables[i]);
			Tile toMove = kitchenGrid[(int) pos.getX()][(int) pos.getY()];
			toMove.setPosition(new PositionF(futurePos.getY(), futurePos.getX()).mul(this.getTileWidth())
					.sub(this.getOriginOffset()));
			if (futurePos.getX() == 0) {
				toMove.setDirection(AutDirection.S);
			} else {
				toMove.setDirection(AutDirection.N);
			}
			futureKitchenGrid[(int) futurePos.getX()][(int) futurePos.getY()] = toMove;
		}
		this.kitchenGrid = futureKitchenGrid;
		this.game.playSound("kitchen_shuffle");
	}

	@Override
	public int getTileWidth() {
		return 13;
	}

	@Override
	public PositionF getOriginOffset() {
		return KITCHEN_ORIGIN.neg().toFloat();
	}

	@Override
	public Tile getTileAt(int gridX, int gridY) {
		if (gridX < 0 || gridY < 0 || gridX >= 10 || gridY >= 4)
			return null;
		return kitchenGrid[gridY][gridX];
	}

	@Override
	protected int getBackgroundColor() {
		return 0x000000;
	}

	public CookEntity getCook() {
		return cook;
	}

	public HashMap<ItemType, StockTable> getStockTables() {
		return stockTables;
	}

	public void addRandomItem() {
		Random rdm = new Random();
		PositionF pos = indexStockEmplacements.get(rdm.nextInt(indexStockEmplacements.size()));
		StockTable toAdd = (StockTable) kitchenGrid[(int) pos.getX()][(int) pos.getY()];
		toAdd.addStock(2);
	}

	/**
	 * Affiche le fond ou la liste des recettes si la touche [ESC] est enclenchée
	 *
	 * @param g
	 */

	@Override
	public void render(Graphics g) {
		g.fill(0xff511e43);
		g.drawSprite(Sprite.KITCHEN_TRUCK_FLOOR, KITCHEN_ORIGIN.getX(), KITCHEN_ORIGIN.getY());
		super.render(g); // Fond et case

		final int tileWidth = getTileWidth();
		final PositionF origin = getOriginOffset();

		for (Entity entity : entityList) {
			int gX = entity.getGridPosFromPos().getX();
			int gY = entity.getGridPosFromPos().getY();
			Graphics subGraphics = g.window(gX * tileWidth - origin.getX(), gY * tileWidth - origin.getY(), tileWidth,
					tileWidth);

			entity.render(subGraphics);
		}

		g.drawSprite(Sprite.KITCHEN_TRUCK, KITCHEN_ORIGIN.getX() - 13, KITCHEN_ORIGIN.getY() - 13);
		g.drawSprite(Sprite.ORDER_CARD, 224, 1);

		renderCurrentOrder(g);

		g.drawSprite(Sprite.CLOCK, 8, 3);
		g.drawText(String.valueOf((int) game.timeGame / 1000), Align.CENTER, 17, 24);

		long score = game.getCurrentScore();
		g.drawText("Score :", Align.CENTER, 17, 47);
		g.drawText(String.valueOf(score), Align.CENTER, 17, 55);

		if (smoke || smokeFryingOil) {
			g.drawSprite(Sprite.KITCHEN_TRUCK_SMOKE, KITCHEN_ORIGIN.getX() - 13, KITCHEN_ORIGIN.getY() - 13);

		}

		if (game.listener.isUp("ESCAPE")) {
			g.drawSprite(Sprite.RECIPES, 0, 0);
		}

	}

	/**
	 * Affiche la commande en cours : 2 plats + leurs options
	 *
	 * @param g
	 */
	public void renderCurrentOrder(Graphics g) {
		g.drawSprite(currentOrder0.getType().getSprite(), 226, 7);
		g.drawSprite(currentOrder1.getType().getSprite(), 240, 7);
		if (currentOrder0.getSauce() != null) {
			g.drawSprite(currentOrder0.getSauce().getSprite(), 226, 25);
		}
		if (currentOrder1.getSauce() != null) {
			g.drawSprite(currentOrder1.getSauce().getSprite(), 240, 25);
		}
		if (currentOrder0.hasOptionalSalad()) {
			g.drawSprite(Sprite.SALADELEAF, 227, 40);
		}
		if (currentOrder1.hasOptionalSalad()) {
			g.drawSprite(Sprite.SALADELEAF, 241, 40);
		}
		if (currentOrder0.hasOptionalTomato()) {
			g.drawSprite(Sprite.TOMATOSLICE, 226, 53);
		}
		if (currentOrder1.hasOptionalTomato()) {
			g.drawSprite(Sprite.TOMATOSLICE, 240, 53);
		}
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);

		// On réduit le temps qu'il reste pour l'affichage de la fumée
		if (smoke || smokeFryingOil) {
			smokeCounter--;
		}
		if (smokeCounter == 0) {
			smoke = false;
			smokeFryingOil = false;
			smokeCounter = 200;
		}

		// Réapparition aléatoire des cafards
		Random rand = new Random();
		if (rand.nextInt(1000) < 1) {
			this.addEntity(new CockroachEntity(this, new PositionF(135, 36)));
		}

		// On tick chaque Tile
		for (Tile[] tiles : kitchenGrid) {
			for (Tile tile : tiles) {
				if (tile != null) {
					tile.tick(elapsed);
				}
			}
		}

		if (deliveryInThisTick)
			deliveryInThisTick = false;

	}

}
