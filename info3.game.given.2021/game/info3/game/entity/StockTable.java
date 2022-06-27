package info3.game.entity;

import info3.game.content.Item;
import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutDirection;
import info3.game.scene.Scene;

public class StockTable extends KitchenTile {
	private static final int[] FULL_INDICATOR_AMOUNTS = new int[] { 0, 1, 2, 3, 3, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 6 };

	final Item item;
	private int stock = 5;
	private final Sprite stockItem;
	boolean empty = false;
	private boolean onWizz = false;
	private int timerWizz = 0;

	public StockTable(Scene parent, int gridX, int gridY, AutDirection d, Item item, Sprite stockItem) {
		super(parent, gridX, gridY, Sprite.STOCK_TABLE, d);
		this.item = item;
		this.stockItem = stockItem;
	}

	@Override
	public EntityType getType() {
		return EntityType.TILE_STOCK;
	}

	@Override
	public boolean pop(AutDirection direction) { // prendre un aliment
		Entity eInteracting = selectEntityToInteractWith();
		if (eInteracting instanceof CookEntity) {
			if (((CookEntity) eInteracting).assembly.getItems().size() != 0) {
				parentScene.game.playSound("pick");
				return false;
			} else {
				if (stock == 0) {
					return false;
				} else {
					((CookEntity) eInteracting).assembly.getItems().add(item);
					stock--;
					empty = !gotStuff();
					return true;
				}
			}

		}
		if (eInteracting instanceof CockroachEntity) {
			if (((CockroachEntity) eInteracting).item != null) {
				return false;
			} else {
				if (stock == 0) {
					return false;
				} else {
					((CockroachEntity) eInteracting).item = item;
					stock--;
					empty = !gotStuff();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);
		if (onWizz) {
			timerWizz++;
			int maxTimerWizz = 10;
			if (timerWizz == maxTimerWizz) {
				timerWizz = 0;
				onWizz = false;
			}
		}
	}

	@Override
	public boolean wizz(AutDirection direction) {
		onWizz = true;
		return true;
	}

	public void addStock(int x) {
		this.stock += x;
	}

	@Override
	public void render(Graphics g) {
		g.drawSprite(direction == AutDirection.S ? Sprite.STOCK_TABLE : Sprite.STOCK_TABLE_N, 0, 0);
		if (!empty)
			g.drawSprite(this.stockItem, 0, 0);
		if (onWizz)
			g.drawSprite(Sprite.STOCKTABLE_WIZZ, 0, 0);

		Sprite fullIndicator;
		if (stock < FULL_INDICATOR_AMOUNTS.length) {
			fullIndicator = Sprite.FULL_INDICATORS[FULL_INDICATOR_AMOUNTS[stock]];
		} else {
			fullIndicator = Sprite.FULL_7;
		}
		g.drawSprite(fullIndicator, 0, direction == AutDirection.S ? 0 : 9);
	}

	@Override
	public boolean egg(AutDirection direction) {
		if (parentScene.entityList.size() <= Scene.MAXIMUM_ENTITIES) {
			Entity newEntity;
			newEntity = new StockTable(this.parentScene, this.gridX, this.gridY, this.direction, item,
					this.defaultSprite);
			return this.parentScene.addEntity(newEntity);
		}
		return false;
	}

	@Override
	public boolean gotStuff() {
		if (stock > 0) {
			empty = false;
			return true;
		}
		return false;
	}

}
