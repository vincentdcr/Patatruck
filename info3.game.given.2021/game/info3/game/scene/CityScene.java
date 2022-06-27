package info3.game.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import info3.game.Game;
import info3.game.entity.CarEntity;
import info3.game.entity.CityDeliveryTile;
import info3.game.entity.CityTile;
import info3.game.entity.Entity;
import info3.game.entity.Tile;
import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutCategory;
import info3.game.position.PositionF;
import info3.game.position.PositionI;
import info3.game.worldgen.WorldGenerator;
import info3.game.worldgen.WorldGenerator.LocatedMarket;

public class CityScene extends Scene {

	private final PositionF center = new PositionF((float) pixelWidth / 2F - 4.5F, (float) pixelHeight / 2F - 4.5F);
	private PositionF vanPosition = PositionF.ZERO;
	private CityDeliveryTile deliveryTile;
	Random rand = new Random();
	public final WorldGenerator worldGenerator = new WorldGenerator(rand.nextLong());
	private CarEntity cookCar;

	// Finding nearest market is costly in performance, so we do not want to call it
	// on every tick
	private PositionI nearestMarketPos = new PositionI(0, 0);
	private int count = 0;
	private final Map<PositionI, CityTile> cachedCityTiles = new HashMap<>();
	public final ArrayList<PositionI> cacheMarketVisited = new ArrayList<>();

	public CityScene(int pixelWidth, int pixelHeight, Game g) {
		super(pixelWidth, pixelHeight, g);
		cookCar = new CarEntity(this, vanPosition, true, true);
		addEntity(cookCar);
		deliveryTile = new CityDeliveryTile(this);
		addEntity(deliveryTile);
	}

	private void reloadCity() {
		Random rdm = new Random(System.currentTimeMillis());
		this.worldGenerator.changeSeed(rdm.nextLong());
		this.cachedCityTiles.clear();
		vanPosition = PositionF.ZERO;
		while (!((CityTile) (getTileAt((int) vanPosition.getX(), (int) vanPosition.getY()))).getGenTile().hasRoad()) {
			vanPosition = vanPosition.add(new PositionF(getTileWidth(), 0));
		}
		this.nearestMarketPos = this.getNearestMarketPos();
		this.entityList.clear();
		addEntity(cookCar);
		deliveryTile = new CityDeliveryTile(this);
		addEntity(deliveryTile);
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);
		if (this.entityList.size() < 50)
			getRandomTileNearViewport().tick(elapsed);

		count += 1;
		if (count == 100) {
			nearestMarketPos = this.getNearestMarketPos();
			count = 0;
		}
		removeUnusedTilesInCache();
		if (this.game.listener.isUp("G"))
			reloadCity();
	}

	@Override
	public int getTileWidth() {
		return 20;
	}

	@Override
	public PositionF getOriginOffset() {
		return cookCar.getPosition().sub(center);
	}

	@Override
	public Tile getTileAt(int gridX, int gridY) {
		PositionI posGrid = new PositionI(gridX, gridY);
		CityTile storedTile = cachedCityTiles.get(posGrid);
		if (storedTile != null) {
			return storedTile;
		} else {
			CityTile newTile = new CityTile(this, gridX, gridY);
			cachedCityTiles.put(posGrid, newTile);
			return newTile;
		}
	}

	private void removeUnusedTilesInCache() {
		PositionI gridPos = getGridPosFromPosCity(cookCar.getPosition());
		ArrayList<PositionI> gridPosTilesToRemove = new ArrayList<>();
		for (PositionI gridPosInCache : cachedCityTiles.keySet()) {
			PositionI gridPosDiff = gridPos.sub(gridPosInCache);
			if (Math.abs(gridPosDiff.getX()) > 13 || Math.abs(gridPosDiff.getY()) > 4)
				gridPosTilesToRemove.add(gridPosInCache);
		}
		for (PositionI gridPosToRemove : gridPosTilesToRemove) {
			CityTile tile = (CityTile) getTileAt(gridPosToRemove.getX(), gridPosToRemove.getY());
			if (tile.eSpeedbump != null)
				removeEntity(tile.eSpeedbump);
			if (tile.eMarketStall != null)
				removeEntity(tile.eMarketStall);
			if (tile.hasCones)
				tile.destroyCones();
			cachedCityTiles.remove(gridPosToRemove);
		}
	}

	/* Renvoit la categorie du cadrant de la tuile a cette pos */
	public AutCategory whatsTheCategoryOfTile(PositionF pos) {
		int gX = getGridPosFromPosCity(pos).getX();
		int gY = getGridPosFromPosCity(pos).getY();
		CityTile tile = (CityTile) getTileAt(gX, gY);

		switch (whereInTile(pos)) {
		case 0:
			if (tile.genTile.collisionBox.topLeft)
				return AutCategory.O;
			else
				return AutCategory.J;
		case 1:
			if (tile.genTile.collisionBox.top)
				return AutCategory.O;
			else
				return AutCategory.J;
		case 2:
			if (tile.genTile.collisionBox.left)
				return AutCategory.O;
			else
				return AutCategory.J;
		case 3:
			return AutCategory.O;
		default:
			System.out.println("panic");
		}
		return null;
	}

	/* Fct qui renvoit le cadrant parmi les 4 d'une tuile de la ville */
	public int whereInTile(PositionF pos) {
		PositionF posModuloTileWidth = pos.divMod(getTileWidth());
		if (posModuloTileWidth.getX() < 9 && posModuloTileWidth.getY() < 9)
			return 0;
		if (posModuloTileWidth.getX() >= 9 && posModuloTileWidth.getY() < 9)
			return 1;
		if (posModuloTileWidth.getX() < 9 && posModuloTileWidth.getY() >= 9)
			return 2;
		else
			return 3;
	}

	@Override
	protected int getBackgroundColor() {
		return 0xffeb6c82;
	}

	public CarEntity getCook() {
		return cookCar;
	}

	public void setCook(CarEntity car) {
		cookCar = car;
	}

	public CityDeliveryTile getDeliveryTile() {
		return deliveryTile;
	}

	@Override
	public void render(Graphics g) {
		super.render(g);

		for (Entity entity : entityList) {
			PositionF posGraphics = getPosRelativeToVan(entity);
			Graphics subGraphics = g.window(posGraphics.getX(), posGraphics.getY(), 4, 4);

			entity.render(subGraphics);
		}

		deliveryTile.render(g);

		renderArrow(g, nearestMarketPos.toFloat().mul(getTileWidth()), Sprite.CITY_ARROWS_MARKET);
		renderArrow(g, deliveryTile.getPosition(), Sprite.CITY_ARROWS_DELIVERY);
	}

	/* Visuellement la fenêtre le van n'est PAS en 0,0 mais en center */
	public PositionF getPosRelativeToVan(Entity entity) {
		return entity.getPosition().add(center).sub(getCook().getPosition());
	}

	/*
	 * Fct qui renvoit la grille correspondante à une position en pixels(marche ds
	 * les 2 scènes) Pour la cityScene 0,0 est le centre du viewport, il faut donc
	 * faire qq calculs pour les grilles qui peuvent être négatives, si on a des
	 * valeurs positives, on divise juste la position reçue en retirant l'offset.
	 * 
	 */
	public PositionI getGridPosFromPosCity(PositionF pos) {
		if (pos.getX() < 0 || pos.getY() < 0) {
			pos = pos.divFloorF(getTileWidth());
			float posTmpX = pos.getX();
			float posTmpY = pos.getY();
			int posX = (int) posTmpX;
			int posY = (int) posTmpY;
			if (posTmpX < 0)
				posX = (int) Math.floor(posTmpX);
			if (posTmpY < 0)
				posY = (int) Math.floor(posTmpY);
			return new PositionI(posX, posY);
		} else {
			return pos.divFloor(getTileWidth());
		}

	}

	private PositionI getNearestMarketPos() {
		PositionI cookCarCell = cookCar.getPosition().divFloor(20);
		worldGenerator.markMarketAsSeen(cookCarCell.getX(), cookCarCell.getY());

		Optional<LocatedMarket> optMarket = worldGenerator.locateMarkets(cookCarCell.getX(), cookCarCell.getY())
				.findFirst();

		assert optMarket.isPresent();
		LocatedMarket market = optMarket.get();
		return new PositionI(market.x, market.y);
	}

	public void renderArrow(Graphics g, PositionF pixelPos, Sprite[] arrow) {
		final float xCar = cookCar.getPosition().getX();
		final float yCar = cookCar.getPosition().getY();
		final int xTile = (int) pixelPos.getX();
		final int yTile = (int) pixelPos.getY();

		int centerW = pixelWidth / 2, centerH = pixelHeight / 2;
		float absX = Math.abs(xTile - (xCar - centerW));
		float absY = Math.abs(yTile - yCar + centerH);

		if (yTile < yCar - centerH) {
			if (xTile > xCar + centerW) {
				g.drawSprite(arrow[1], pixelWidth - 7, 0);
			} else if (xTile < xCar - centerW) {
				g.drawSprite(arrow[7], 0, 0);
			} else {
				g.drawSprite(arrow[0], absX, 0);
			}
		}

		else if (yTile > yCar + centerH) {
			if (xTile > xCar + centerW) {
				g.drawSprite(arrow[3], pixelWidth - 7, pixelHeight - 16);
			} else if (xTile < xCar - centerW) {
				g.drawSprite(arrow[5], 0, pixelHeight - 16);
			} else {
				g.drawSprite(arrow[4], absX, pixelHeight - 16);
			}
		}

		else if (xTile > xCar + centerW) {
			g.drawSprite(arrow[2], pixelWidth - 7, absY);
		}

		else if (xTile < xCar - centerW) {
			g.drawSprite(arrow[6], 0, absY);
		}

		else {
			g.drawSprite(arrow[4], -10, -10);
		}
	}

	private CityTile getRandomTileNearViewport() {
		Random rand = new Random();
		CityTile tile;
		do {
			PositionF randPos = new PositionF(rand.nextInt(13) * getTileWidth(), rand.nextInt(4) * getTileWidth());

			PositionF pos = randPos.sub(center).add(getCook().getPosition());
			PositionI gridPos = getGridPosFromPosCity(pos);
			tile = (CityTile) getTileAt(gridPos.getX(), gridPos.getY());
		} while (tile.genTile.collisionBox.topLeft);

		return tile;
	}

	public boolean isTooFarFromVan(Entity entity) {
		PositionF posFromVan = getPosRelativeToVan(entity).sub(center);
		float posFromVanX = posFromVan.getX();
		float posFromVanY = posFromVan.getY();
		return Math.abs(posFromVanX) > 192f || Math.abs(posFromVanY) > 108f;
	}

	public void addToMarketCache(int gridX, int gridY) {
		this.cacheMarketVisited.add(new PositionI(gridX, gridY));
	}
}
