package info3.game.entity;

import java.io.IOException;

import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutCategory;
import info3.game.position.AutDirection;
import info3.game.position.PositionF;
import info3.game.scene.KitchenScene;
import info3.game.scene.Scene;

public class CockroachEntity extends Entity {

	public CockroachEntity(Scene parent, PositionF position) throws IOException {
		super(parent, position);
		category = AutCategory.A;
		cockroachCounterAdd(1);
	}

	@Override
	public EntityType getType() {
		return EntityType.COCKROACH;
	}

	@Override
	public boolean wizz(AutDirection direction) {

		finish = System.currentTimeMillis();
		timeElapsed = finish - start;

		if (timeElapsed >= 500) {
			AutDirection newDirection = convertRelativToAbsolutedir(direction);
			m_direction = newDirection;
			switch (newDirection) {
			case N: {
				PositionF newPos = new PositionF(0, -parentScene.getTileWidth());
				this.position = position.add(newPos);
				start = System.currentTimeMillis();

				return true;
			}
			case W: {
				PositionF newPos = new PositionF(-parentScene.getTileWidth(), 0);
				this.position = position.add(newPos);
				start = System.currentTimeMillis();

				return true;
			}
			case E: {
				PositionF newPos = new PositionF(parentScene.getTileWidth(), 0);
				this.position = position.add(newPos);
				start = System.currentTimeMillis();

				return true;
			}
			case S: {
				PositionF newPos = new PositionF(0, parentScene.getTileWidth());
				this.position = position.add(newPos);
				start = System.currentTimeMillis();

				return true;
			}
			default:
				start = System.currentTimeMillis();
				return false;
			}
		}
		return false;

	}

	@Override
	public boolean pop(AutDirection direction) { // explode

		return true; // temporary to prevent cockroach from being stuck in dupli statedd
	}

	@Override
	public void render(Graphics g) {
		if (m_direction == AutDirection.N) {
			g.drawSprite(Sprite.COCKROACH_ENTITY_N, 0, 0);
		} else if (m_direction == AutDirection.E) {
			g.drawSprite(Sprite.COCKROACH_ENTITY_E, 0, 0);
		} else if (m_direction == AutDirection.W) {
			g.drawSprite(Sprite.COCKROACH_ENTITY_W, 0, 0);
		} else {
			g.drawSprite(Sprite.COCKROACH_ENTITY_S, 0, 0);
		}
	}

	@Override
	public boolean gwait() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean egg(AutDirection direction) {
		if (((KitchenScene) parentScene).getCockroachCounter() <= KitchenScene.MAXIMUM_COCKROACH_NUMBER) {
			Entity newCockroach = null;
			try {
				newCockroach = new CockroachEntity(this.parentScene, position);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.parentScene.addEntity(newCockroach);
		}
		return false;
	}

	@Override
	public boolean hit(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean jump(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean explode() {
		cockroachCounterAdd(-1);
		return true;
	}

	@Override
	public boolean pick(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean power() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean protect(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean store() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean turn(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean gthrow(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean myDir(AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean closest(AutCategory category, AutDirection direction) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean gotPower() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean gotStuff() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cell(AutDirection direction, AutCategory category) {
		boolean c = super.cell(direction, category);
		return c;
	}

	public void cockroachCounterAdd(int value) {
		((KitchenScene) parentScene).setCockroachCounter(((KitchenScene) parentScene).getCockroachCounter() + value);
		;
	}

}
