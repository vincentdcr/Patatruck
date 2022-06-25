package info3.game.entity;

import info3.game.content.Item;
import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutCategory;
import info3.game.position.AutDirection;
import info3.game.scene.KitchenScene;
import info3.game.scene.Scene;

public class FrieTile extends KitchenTile {
	Item item;
	int compteur;
	static final Sprite EMPTY = Sprite.OFF_FRIE_TILE, FULL = Sprite.ON_FRIE_TILE;

	public FrieTile(Scene parent, int gridX, int gridY, AutDirection d) {
		super(parent, gridX, gridY, null, d);
		defaultSprite = EMPTY;
	}

	@Override
	public EntityType getType() {
		return EntityType.TILE_FRYER;
	}

	@Override
	public boolean pop(AutDirection direction) {// prend un ingrédiant à frire
		if (player.m_assembly.getItems().size() != 1 || this.item != null) {
			return false;
		} else {
			if (player.m_assembly.getItems().get(0).fry() == null) { // est-ce qu'on peut frire ?
				return false;
			}
			this.item = player.m_assembly.getItems().get(0);
			;
			((KitchenScene) this.parentScene).getCook().item = null;
			player.m_assembly.getItems().clear();
			this.compteur = 100;
			defaultSprite = FULL;
			return true;
		}
	}

	@Override
	public boolean wizz(AutDirection direction) {// rend l'ingrédient cuit au joueur
		player.m_assembly.addItem(item);
		this.item = null;
		return true;

	}

	@Override
	public boolean gwait() {
		if (this.compteur > 0) {
			this.compteur--;
			return false;
		} else {
			this.item = item.fry();
			defaultSprite = EMPTY;
			this.compteur = 100;
			return true;

		}
	}

	@Override
	public void render(Graphics g) {
		g.drawSprite(defaultSprite, 0, 0);

		if (defaultSprite == EMPTY && item != null) {
			g.drawSprite(item.getType().getSprite(), 0, 0);

		}
	}

	@Override
	public boolean egg(AutDirection direction) {
		// TODO Auto-generated method stub
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
		if (this.compteur > 0) {
			this.compteur--;
			return false;
		} else {
			((KitchenScene) parentScene).smoke = true;
			item = null;
			return true;

		}
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

}
