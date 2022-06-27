package info3.game.entity;

import info3.game.content.Assembly;
import info3.game.content.Item;
import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutDirection;
import info3.game.scene.CityScene;
import info3.game.scene.KitchenScene;
import info3.game.scene.Scene;
import info3.game.screen.GameScreen;

public class KitchenDeliveryTile extends KitchenTile {

	final Assembly assembly = new Assembly();

	public KitchenDeliveryTile(Scene parent, int gridX, int gridY, AutDirection d) {
		super(parent, gridX, gridY, null, d);
	}

	@Override
	public EntityType getType() {
		return EntityType.TILE_DELIVERY;
	}

	@Override
	public void render(Graphics g) {
		g.drawSprite(Sprite.DELIVERYTILE, 0, 0);
		if (!assembly.getItems().isEmpty()) {
			for (Item item : assembly.getItems()) {
				g.drawSprite(item.getType().getSprite(), 0, 0);

			}
		}
	}

	boolean recetteReady(Item currentOrder1) {
		return (!assembly.getItems().isEmpty() && currentOrder1.equals(assembly.getItems().get(0)));
	}

	@Override
	public boolean pop(AutDirection direction) {
		Entity eInteracting = selectEntityToInteractWith();
		if (eInteracting instanceof CookEntity) {
			if (player.assembly.getItems().isEmpty()) {
				return false;
			} else {
				parentScene.game.playSound("drop");
				assembly.addAssembly(player.assembly);
				player.assembly.getItems().clear();

				return true;

			}
		}
		return false;
	}

	@Override
	public boolean wizz(AutDirection direction) {
		Entity eInteracting = selectEntityToInteractWith();
		if (eInteracting instanceof CookEntity) {
			if (player.assembly.getItems().size() == 0 && assembly.getItems().size() != 0) {
				if (assembly.getItems().get(0).getType().isFinalItem()) {
					player.assembly.addAssembly(assembly);
					assembly.getItems().clear();
				} else {
					player.assembly.addItem(assembly.getItems().remove(assembly.getItems().size() - 1));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean egg(AutDirection direction) {
		if (parentScene.entityList.size() <= Scene.MAXIMUM_ENTITIES) {
			Entity newEntity;
			newEntity = new KitchenDeliveryTile(this.parentScene, this.gridX, this.gridY, this.direction);
			return this.parentScene.addEntity(newEntity);
		}
		return false;
	}

	@Override
	public boolean hit(AutDirection direction) {
		CityScene cityScene = ((CityScene) ((GameScreen) this.parentScene.game.getScreen()).getCityScene());
		boolean hasBeenDelivered = false;
		if (recetteReady(((KitchenScene) parentScene).currentOrder0) && cityScene.getCook().canDeliver()) {
			((KitchenScene) parentScene).currentOrder0 = Item.getRandomItem();
			assembly.getItems().clear();
			parentScene.game.addTime(50);
			// Indicates that the delivery has been done
			hasBeenDelivered = true;
		}

		if (recetteReady(((KitchenScene) parentScene).currentOrder1) && cityScene.getCook().canDeliver()) {
			((KitchenScene) parentScene).currentOrder1 = Item.getRandomItem();
			assembly.getItems().clear();
			parentScene.game.addTime(50);
			// Indicates that the delivery has been done
			hasBeenDelivered = true;
		}
		if (hasBeenDelivered && !((KitchenScene) parentScene).deliveryInThisTick) {
			((KitchenScene) parentScene).deliveryInThisTick = true;
			((CityScene) ((GameScreen) this.parentScene.game.getScreen()).getCityScene()).getDeliveryTile().delivered();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean gotPower() {
		return recetteReady(((KitchenScene) parentScene).currentOrder0)
				|| recetteReady(((KitchenScene) parentScene).currentOrder1);
	}

	@Override
	public boolean gotStuff() {
		return !player.assembly.getItems().isEmpty();
	}

}
