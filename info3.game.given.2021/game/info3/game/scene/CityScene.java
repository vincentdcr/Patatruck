package info3.game.scene;

import info3.game.Game;
import info3.game.entity.CarEntity;
import info3.game.entity.CityTile;
import info3.game.entity.Entity;
import info3.game.entity.PhysicsEntity;
import info3.game.entity.Tile;
import info3.game.graphics.Graphics;
import info3.game.graphics.Graphics.Align;
import info3.game.graphics.Sprite;
import info3.game.position.AutCategory;
import info3.game.position.PositionF;
import info3.game.worldgen.WorldGenerator;
import info3.game.position.PositionI;

public class CityScene extends Scene {

	private final PositionF center = new PositionF((float) pixelWidth / 2F - 4.5F, (float) pixelHeight / 2F - 4.5F);
	private PositionF vanPosition = PositionF.ZERO;
	public final WorldGenerator worldGenerator = new WorldGenerator(0);
	private CarEntity car;
	private CarEntity cookCar;

	private HashMap<PositionI, CityTile> cachedCityTiles;

	public CityScene(int pixelWidth, int pixelHeight, Game g) {
		super(pixelWidth, pixelHeight, g);
		try {
		car = new CarEntity(this, vanPosition, false, false);
		addEntity(car);
		cookCar = new CarEntity(this, center, true, true);
		addEntity(cookCar);
		} catch (IOException e) {
			e.printStackTrace();
		} // To change with vanEntity


	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);
		/*
		 * if (this.m_game.m_listener.isUp("UP")) {
		 * cookPhysics.addForce(AutDirection.N); } if
		 * (this.m_game.m_listener.isUp("DOWN")) { cookPhysics.addForce(AutDirection.S);
		 * } if (this.m_game.m_listener.isUp("LEFT")) {
		 * cookPhysics.addForce(AutDirection.W); } if
		 * (this.m_game.m_listener.isUp("RIGHT")) {
		 * cookPhysics.addForce(AutDirection.E); }
		 * cook.setPosition(cook.getPosition().add(cookPhysics.Shift(elapsed)));
		 */
	}

	// Commenté pour tester avec une scène fixe
	// @Override
	// public void tick(long elapsed) {
	// vanPosition = vanPosition.add(new PositionF(0.2F, 0.1F));
	// }

	@Override
	public int getTileWidth() {
		return 20;
	}

	@Override
	public PositionF getOriginOffset() {
		return vanPosition.add(center.neg()).add(vanPosition);
	}

	@Override
	public Tile getTileAt(int gridX, int gridY) {
		Tile tile = new CityTile(this, gridX, gridY);
		return tile;
	}

	/* Renvoit la categorie du cadrant de la tuile a cette pos */
	public AutCategory whatsTheCategoryOfTile(PositionF pos, Entity entity) {
		int gX = entity.getGridPosFromPos().getX();
		int gY = entity.getGridPosFromPos().getY();
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

	@Override
	public void render(Graphics g) {
		super.render(g);

		for (Entity entity : entity_list) {
			PositionF posGraphics = getPosRelativeToVan(entity);
			System.out.println("posgraphx=" + posGraphics.getX() + " posgraphy=" + posGraphics.getY());
			Graphics subGraphics = g.window(posGraphics.getX(), posGraphics.getY(), 4, 4);

			entity.render(subGraphics);
		}

		Sprite speed = Sprite.SPEEDOMETER;
		if (cookCar.physics.getVelocity() < 20) {
			speed = Sprite.SPEEDOMETER_LOW;
		} else if (cookCar.physics.getVelocity() > 50) {
			speed = Sprite.SPEEDOMETER_HIGH;
		}
		g.drawSprite(speed, 2, 0);
		g.drawText(cookCar.physics.getVelocity() + "", Align.CENTER, 12, 18);

	}

	/* Visuellement la fenêtre le van n'est PAS en 0,0 mais en center */
	private PositionF getPosRelativeToVan(Entity entity) {
		return entity.getPosition().add(center).sub(getCook().getPosition());
	}

}
