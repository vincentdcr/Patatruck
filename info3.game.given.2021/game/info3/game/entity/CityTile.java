package info3.game.entity;

import java.util.Map;
import java.util.Random;

import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutDirection;
import info3.game.position.PositionF;
import info3.game.position.PositionI;
import info3.game.scene.CityScene;
import info3.game.scene.Scene;
import info3.game.worldgen.GenTile;

public class CityTile extends Tile {
	private static final Map<GenTile.MarketRoad, Sprite> MARKET_SPRITE_TOP = Map.of(GenTile.MarketRoad.IN,
			Sprite.CITY_MARKET_TL, GenTile.MarketRoad.OUT, Sprite.CITY_MARKET_TR, GenTile.MarketRoad.BOTH,
			Sprite.CITY_MARKET_T);
	private static final Map<GenTile.MarketRoad, Sprite> MARKET_SPRITE_LEFT = Map.of(GenTile.MarketRoad.IN,
			Sprite.CITY_MARKET_LT, GenTile.MarketRoad.OUT, Sprite.CITY_MARKET_LB, GenTile.MarketRoad.BOTH,
			Sprite.CITY_MARKET_L);

	public final GenTile genTile;
	private final Sprite topRoadSprite, leftRoadSprite;
	private Sprite stallSprite, stallSprite2, stallSprite3, stallSprite4;
	public SpeedBumpEntity eSpeedbump = null;
	public MarketEntity eMarketStall = null;
	public ConeEntity cone0, cone1, cone2 = null;
	public boolean hasCones;

	public CityTile(Scene parent, int gridX, int gridY) {
		super(parent, gridX, gridY);
		position = new PositionF(gridX * parentScene.getTileWidth(), gridY * parentScene.getTileWidth());
		genTile = ((CityScene) parent).worldGenerator.generate(gridX, gridY);
		topRoadSprite = genTile.marketPavingTop == null ? null : MARKET_SPRITE_TOP.get(genTile.marketPavingTop);
		leftRoadSprite = genTile.marketPavingLeft == null ? null : MARKET_SPRITE_LEFT.get(genTile.marketPavingLeft);
		if (genTile.speedbumpLeft || genTile.speedbumpTop)
			eSpeedbump = new SpeedBumpEntity(parent, position, this);
		if (genTile.hasMarketPaving
				&& !((CityScene) parentScene).cacheMarketVisited.contains(new PositionI(gridX, gridY)))
			eMarketStall = new MarketEntity(parent, position, this);
		Random rng = new Random();
		if (genTile.hasMarketPaving) {
			stallSprite = Sprite.STALLS[rng.nextInt(Sprite.STALLS.length)];
			stallSprite2 = Sprite.STALLS[rng.nextInt(Sprite.STALLS.length)];
			stallSprite3 = Sprite.STALLS[rng.nextInt(Sprite.STALLS.length)];
			stallSprite4 = Sprite.STALLS[rng.nextInt(Sprite.STALLS.length)];
		}
		if (rng.nextInt(100) > 98 && !this.genTile.collisionBox.left) {
			hasCones = true;
			cone0 = new ConeEntity(parentScene, position.add(new PositionF(-0.5f, 12.5f)));
			cone1 = new ConeEntity(parentScene, position.add(new PositionF(2.5f, 12.5f)));
			cone2 = new ConeEntity(parentScene, position.add(new PositionF(5.5f, 12.5f)));
		} else if (rng.nextInt(100) > 98 && !this.genTile.collisionBox.top) {
			hasCones = true;
			cone0 = new ConeEntity(parentScene, position.add(new PositionF(12.5f, -0.5f)));
			cone1 = new ConeEntity(parentScene, position.add(new PositionF(12.5f, 2.5f)));
			cone2 = new ConeEntity(parentScene, position.add(new PositionF(12.5f, 5.5f)));
		}

	}

	public GenTile getGenTile() {
		return genTile;
	}

	@Override
	public boolean move(AutDirection direction) {
		// Can't move
		return false;
	}

	@Override
	public EntityType getType() {
		return EntityType.TILE_CITY;
	}

	@Override
	public void render(Graphics g) {
		if (genTile.hasMarketPaving) {
			g.drawSprite(Sprite.CITY_MARKET_CROSSING, 0, 0);
		}

		if (topRoadSprite != null) {
			g.drawSprite(topRoadSprite, 0, 0);
		}

		if (leftRoadSprite != null) {
			g.drawSprite(leftRoadSprite, 0, 0);
		}
		if (genTile.speedbumpTop) {
			g.drawSprite(Sprite.CITY_SPEEDBUMP_TOP, 0, 0);
		}

		if (genTile.speedbumpLeft) {
			g.drawSprite(Sprite.CITY_SPEEDBUMP_LEFT, 0, 0);
		}
		if (genTile.hasMarketPaving) {
			g.drawSprite(stallSprite, 0, 0);
			g.drawSprite(stallSprite2, 15, 5);
			g.drawSprite(stallSprite3, 5, 5);
			g.drawSprite(stallSprite4, 5, 15);
		}

		g.drawSpritePart(genTile.buildingSprite, 0, 0, genTile.buildingSpriteOffsetX, genTile.buildingSpriteOffsetY);
	}

	@Override
	public boolean pop(AutDirection direction) {
		this.parentScene.addEntity(new CarEntity(this.parentScene,
				new PositionF(gridX * parentScene.getTileWidth(), gridY * parentScene.getTileWidth()), true, false));
		return true;
	}

	@Override
	public boolean wizz(AutDirection direction) {
		this.parentScene.addEntity(new CarEntity(this.parentScene,
				new PositionF(gridX * parentScene.getTileWidth(), gridY * parentScene.getTileWidth()), false, false));
		return true;
	}

	@Override
	public boolean egg(AutDirection direction) {
		return false;
	}

	public void destroyCones() {
		if (cone0 != null)
			parentScene.removeEntity(cone0);
		if (cone1 != null)
			parentScene.removeEntity(cone1);
		if (cone2 != null)
			parentScene.removeEntity(cone2);
	}
}
