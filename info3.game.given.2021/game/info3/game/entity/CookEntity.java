package info3.game.entity;

import java.awt.image.BufferedImage;
import java.io.IOException;

import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.Direction;
import info3.game.position.PositionF;
import info3.game.scene.Scene;

public class CookEntity extends Entity {
	// Item holding; Class Item TODO

	BufferedImage[] m_images;
	long m_imageElapsed;
	long m_moveElapsed;
	int m_imageIndex;

	public CookEntity(Scene parent, PositionF position) throws IOException {
		super(parent, position);
		m_images = loadSprite("resources/winchester-4x6.png", 4, 6);
		move_timer_max = 100;
	}

	// @Override
	public boolean wizz(Direction direction) {
		switch (direction) {
		case NORD: {
			float x = position.getX();
			float y = position.getY();
			PositionF newPos = new PositionF(0, -parentScene.getTileWidth());
			// Tile nextTile = parent.getTile(x, y - parent.getTileWidth()); // On récupère
			// la tile où le cuisinier veut se déplacer
			// On gère plus tard les colisions, la vérification que la tile soit libre
			this.position = position.add(newPos);
			return true;
		}
		case OUEST: {
			float x = position.getX();
			float y = position.getY();
			PositionF newPos = new PositionF(-parentScene.getTileWidth(), 0);
			// Tile nextTile = parent.getTile(x - parent.getTileWidth(), y); // On récupère
			// la tile où le cuisinier veut se déplacer
			// On gère plus tard les colisions, la vérification que la tile soit libre
			this.position = position.add(newPos);
			return true;
		}
		case EST: {
			float x = position.getX();
			float y = position.getY();
			PositionF newPos = new PositionF(parentScene.getTileWidth(), 0);
			// Tile nextTile = parent.getTile(x + parent.getTileWidth(), y); // On récupère
			// la tile où le cuisinier veut se déplacer
			// On gère plus tard les colisions, la vérification que la tile soit libre
			this.position = position.add(newPos);
			return true;
		}
		case SUD: {
			float x = position.getX();
			float y = position.getY();
			PositionF newPos = new PositionF(0, parentScene.getTileWidth());
			// Tile nextTile = parent.getTile(x, y + parent.getTileWidth()); // On récupère
			// la tile où le cuisinier veut se déplacer
			// On gère plus tard les colisions, la vérification que la tile soit libre
			this.position = position.add(newPos);
			return true;
		}
		default:
			return false;
		}
	}

	@Override
	public void tick(long elapsed) {
		m_imageElapsed += elapsed;
		if (m_imageElapsed > 200) {
			m_imageElapsed = 0;
			m_imageIndex = (m_imageIndex + 1) % m_images.length;
		}
		m_moveElapsed += elapsed;
		if (m_moveElapsed > 24 & this.parentScene.getPixelWidth() != 0) {
			m_moveElapsed = 0;
			this.setPosition(new PositionF(this.position.getX() % this.parentScene.getPixelWidth(),
					this.position.getY() % this.parentScene.getPixelHeight()));
		}
		move_timer -= elapsed;
		if (move_timer < 0) {
			move_timer = 0;
		}
	}

	@Override
	public void render(Graphics g) {
		// BufferedImage img = m_images[m_imageIndex];
		g.drawSprite(Sprite.COWBOY1, this.position.getX(), this.position.getY());
	}
}
