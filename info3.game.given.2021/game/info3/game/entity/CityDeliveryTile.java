package info3.game.entity;

import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutDirection;
import info3.game.position.PositionF;
import info3.game.scene.Scene;

public class CityDeliveryTile extends CityTile {
	private boolean delivered = false;
	private int count = 0;
	private boolean blinkState = false;

	static int RADIUS = 20;

	public CityDeliveryTile(Scene parent) {
		super(parent, 0, 0);
	}

	public void delivered() {
		parentScene.game.playSound("delivery");
		delivered = !delivered;
	}

	@Override
	public boolean pop(AutDirection direction) { // Update the boolean used to display a blinking frame indicating the
													// delivery area
		count += 1;
		if (count == 5) {
			blinkState = !blinkState;
			count = 0;
		}
		return true;
	}

	@Override
	public boolean wizz(AutDirection direction) { // Find a new position for the delivery
		RADIUS += 10;
		delivered = false;

		double a = (Math.random() * 2 * 3.14);
		double r = (RADIUS * Math.sqrt(Math.random()));

		float x = (float) (r * Math.cos(a));
		float y = (float) (r * Math.sin(a));

		while (!(((CityTile) (parentScene.getTileAt((int) x, (int) y))).getGenTile().hasRoad())) {
			a = (Math.random() * 2 * 3.14);
			r = 10 + (RADIUS * Math.sqrt(Math.random()));

			x = (float) (r * Math.cos(a));
			y = (float) (r * Math.sin(a));
		}

		this.setPosition(new PositionF(Math.abs(gridX + x) * parentScene.getTileWidth(),
				Math.abs(gridY + y) * parentScene.getTileWidth()));

		return true;
	}

	@Override
	public void render(Graphics g) {
		if (blinkState) {
			int gX = (int) (this.position.getX() - parentScene.getOriginOffset().getX());
			int gY = (int) (this.position.getY() - parentScene.getOriginOffset().getY());
			g.drawSprite(Sprite.DELIVERY_TILE_FRAME, gX, gY);
		}
	}

	@Override
	public boolean egg(AutDirection direction) {
		if (parentScene.entityList.size() <= Scene.MAXIMUM_ENTITIES) {
			Entity newEntity;
			newEntity = new CityDeliveryTile(this.parentScene);
			return this.parentScene.addEntity(newEntity);
		}
		return false;
	}

	@Override
	public boolean gotStuff() {
		return delivered;
	}

	@Override
	public EntityType getType() {
		return EntityType.TILE_DELIVERY_CITY;
	}

}
