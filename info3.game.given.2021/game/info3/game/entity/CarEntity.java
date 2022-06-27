package info3.game.entity;

import info3.game.graphics.Graphics;
import info3.game.graphics.Sprite;
import info3.game.position.AutCategory;
import info3.game.position.AutDirection;
import info3.game.position.PositionF;
import info3.game.scene.CityScene;
import info3.game.scene.KitchenScene;
import info3.game.scene.Scene;
import info3.game.screen.GameScreen;

public class CarEntity extends Entity {

	long startJump;
	long finishJump;
	final boolean isTruck;
	boolean isPlayer;
	private boolean swapInThisTick = false;
	private Entity entityEncountered;
	public Physics physics = new PhysicsClassic(15);
	public int shuffleCooldown = 0; // only for player controlled car
	public int marketScreamsCooldown;
	boolean isClassicPhysic = true;

	boolean truckHasStock = true;

	public CarEntity(Scene parent, PositionF position, boolean isTruck, boolean isPlayer) {
		super(parent, position);
		category = AutCategory.A;
		this.isTruck = isTruck;
		this.isPlayer = isPlayer;
		changeCategory();
	}

	public boolean canDeliver() {
		if (isPlayer) {
			float tileX = ((CityScene) parentScene).getDeliveryTile().position.getX();
			float tileY = ((CityScene) parentScene).getDeliveryTile().position.getY();
			float truckX = this.position.getX();
			float truckY = this.position.getY();

			return truckX > tileX - 15 && truckX < tileX + 15 && truckY > tileY - 15 && truckY < tileY + 15;
		}
		return false;
	}

	@Override
	public void render(Graphics g) {
		switch (this.direction) {
		case N:
			if (!isTruck) {
				g.drawSprite(Sprite.RED_CAR_N, 0, 0);
			} else if (isPlayer) {
				g.drawSprite(Sprite.YELLOW_CAR_N, 0, 0);
			} else {
				g.drawSprite(Sprite.BLUE_CAR_N, 0, 0);
			}
			break;
		case S:
			if (!isTruck) {
				g.drawSprite(Sprite.RED_CAR_S, 0, 0);
			} else if (isPlayer) {
				g.drawSprite(Sprite.YELLOW_CAR_S, 0, 0);
			} else {
				g.drawSprite(Sprite.BLUE_CAR_S, 0, 0);
			}
			break;
		case E:
			if (!isTruck) {
				g.drawSprite(Sprite.RED_CAR_E, 0, 0);
			} else if (isPlayer) {
				g.drawSprite(Sprite.YELLOW_CAR_E, 0, 0);
			} else {
				g.drawSprite(Sprite.BLUE_CAR_E, 0, 0);
			}
			break;
		case W:
			if (!isTruck) {
				g.drawSprite(Sprite.RED_CAR_W, 0, 0);
			} else if (isPlayer) {
				g.drawSprite(Sprite.YELLOW_CAR_W, 0, 0);
			} else {
				g.drawSprite(Sprite.BLUE_CAR_W, 0, 0);
			}
			break;
		default:
			break;

		}

	}

	@Override
	public EntityType getType() {
		if (isPlayer) {
			return EntityType.TRUCK;
		}
		return EntityType.CAR;
	}

	@Override
	public void tick(long elapsed) {
		if (((CityScene) parentScene).isTooFarFromVan(this))
			this.parentScene.removeEntity(this);
		else {
			super.tick(elapsed);
			this.position = this.position.add(physics.shift());

			finish = System.currentTimeMillis();
			timeElapsed = finish - start;

			if (timeElapsed >= 1000) {
				this.swapInThisTick = false;
			}

			if (this.shuffleCooldown > 0)
				this.shuffleCooldown--;
			if (this.marketScreamsCooldown > 0)
				this.marketScreamsCooldown--;

		}
		KitchenScene kitchenScene = ((KitchenScene) ((GameScreen) this.parentScene.game.getScreen()).getKitchenScene());
		if (isClassicPhysic && kitchenScene.smoke) {
			this.toSmokePhysics();
			isClassicPhysic = false;
		} else if (isClassicPhysic && kitchenScene.smokeFryingOil) {
			this.toNoBrakesPhysics();
			isClassicPhysic = false;
		} else if (!isClassicPhysic && !kitchenScene.smoke && !kitchenScene.smokeFryingOil) {
			this.toClassicPhysics();
			isClassicPhysic = true;
		}

	}

	@Override
	public boolean pop(AutDirection direction) {
		if (!swapInThisTick && isPlayer && entityEncountered != null && ((CarEntity) entityEncountered).isTruck) {
			parentScene.game.playSound("woosh");
			this.swap((CarEntity) entityEncountered);
			start = System.currentTimeMillis();
		}
		return true;
	}

	// seulement utilisé pour le déplacement du joueur
	@Override
	public boolean wizz(AutDirection direction) {
		AutDirection newDirection = convertRelativToAbsolutedir(direction);
		this.direction = newDirection;
		switch (newDirection) {
		case N:
		case W:
		case E:
		case S:
			physics.addForce(this.direction);
			return true;
		default:
			return false;
		}

	}

	@Override
	public boolean egg(AutDirection direction) {
		if (parentScene.entityList.size() <= Scene.MAXIMUM_ENTITIES) {
			Entity newEntity;
			newEntity = new CarEntity(this.parentScene, position, isTruck, isPlayer);
			return this.parentScene.addEntity(newEntity);
		}
		return false;
	}

	@Override
	public boolean hit(AutDirection direction) {
		parentScene.game.playSound("crash");
		this.position = this.position.add(physics.bounce());
		return true;
	}

	@Override
	public boolean protect(AutDirection direction) {
		AutDirection newDirection = convertRelativToAbsolutedir(direction);
		this.direction = newDirection;
		switch (newDirection) {
		case N: {
			PositionF newPos = new PositionF(0, -0.5f);
			this.position = position.add(newPos);
			return true;
		}
		case W: {
			PositionF newPos = new PositionF(-0.5f, 0);
			this.position = position.add(newPos);

			return true;
		}
		case E: {
			PositionF newPos = new PositionF(0.5f, 0);
			this.position = position.add(newPos);
			return true;
		}
		case S: {
			PositionF newPos = new PositionF(0, 0.5f);
			this.position = position.add(newPos);
			return true;
		}
		default:
			return false;
		}
	}

	@Override
	public boolean gthrow(AutDirection direction) {
		physics.removeForce();
		return true;
	}

	@Override
	public boolean gwait() {
		return true;
	}

	/* Slows down player when crosses path with another car */
	@Override
	public boolean jump(AutDirection direction) {
		finishJump = System.currentTimeMillis();
		long timeElapsedJump = finishJump - startJump;
		if (isPlayer && timeElapsedJump > 300) {
			physics.stop();
			startJump = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	// Point de collision pr l'instant HARDCODE a l'entite CarEntity
	@Override
	public boolean cell(AutDirection direction, AutCategory category) {
		AutDirection newDirection = convertRelativToAbsolutedir(direction);
		for (Entity entity : parentScene.entityList) {
			if (entity != this) {
				switch (newDirection) {
				case N: {
					if (entity.catAtThisPos(position.add(new PositionF(0, -1))) == category
							|| entity.catAtThisPos(position.add(new PositionF(3, -1))) == category) {
						entityEncountered = entity;
						return true;
					}
					break;
				}
				case W: {
					if (entity.catAtThisPos(position.add(new PositionF(-1, 0))) == category
							|| entity.catAtThisPos(position.add(new PositionF(-1, 3))) == category) {
						entityEncountered = entity;
						return true;
					}
					break;
				}
				case E: {
					if (entity.catAtThisPos(position.add(new PositionF(4, 0))) == category
							|| entity.catAtThisPos(position.add(new PositionF(4, 3))) == category) {
						entityEncountered = entity;
						return true;
					}
					break;
				}
				case S: {
					if (entity.catAtThisPos(position.add(new PositionF(0, 4))) == category
							|| entity.catAtThisPos(position.add(new PositionF(3, 4))) == category) {
						entityEncountered = entity;
						return true;
					}
					break;
				}
				case H: {
					if (entity instanceof ConeEntity && !isPlayer)
						System.out.println("test");
					if (entity.catAtThisPos(position.add(new PositionF(0, 0))) == category
							|| entity.catAtThisPos(position.add(new PositionF(3, 0))) == category
							|| entity.catAtThisPos(position.add(new PositionF(0, 3))) == category
							|| entity.catAtThisPos(position.add(new PositionF(3, 3))) == category) {
						entityEncountered = entity;
						return true;
					}
					break;
				}
				default:
					return false;
				}
			}
		}
		switch (newDirection) {
		case N: {
			if (((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(1, -1))) == category
					|| ((CityScene) parentScene)
							.whatsTheCategoryOfTile(position.add(new PositionF(2, -1))) == category) {
				return true;
			}
			break;
		}
		case W: {
			if (((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(-1, 1))) == category
					|| ((CityScene) parentScene)
							.whatsTheCategoryOfTile(position.add(new PositionF(-1, 2))) == category) {
				return true;
			}
			break;
		}
		case E: {
			if (((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(4, 1))) == category
					|| ((CityScene) parentScene)
							.whatsTheCategoryOfTile(position.add(new PositionF(4, 2))) == category) {
				return true;
			}
			break;
		}
		case S: {
			if (((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(1, 4))) == category
					|| ((CityScene) parentScene)
							.whatsTheCategoryOfTile(position.add(new PositionF(2, 4))) == category) {
				return true;
			}
			break;
		}
		case H: {
			if (((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(1, 1))) == category
					|| ((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(2, 1))) == category
					|| ((CityScene) parentScene).whatsTheCategoryOfTile(position.add(new PositionF(1, 2))) == category
					|| ((CityScene) parentScene)
							.whatsTheCategoryOfTile(position.add(new PositionF(2, 2))) == category) {
				return true;

			}
			break;
		}
		default:
			return false;

		}
		return false;

	}

	public void changeCategory() {
		if (!isPlayer)
			category = AutCategory.A;
		else
			category = AutCategory.AROBASE;
	}

	public void toNoBrakesPhysics() {
		this.physics = new PhysicsNoBrakes(15, this.physics.getAccX(), this.physics.getAccY(), this.physics.getVelX(),
				this.physics.getVelY(), this.physics.getMaxVel(), this.physics.getAvgVelBuff(),
				this.physics.getAvgVel(), this.physics.getTimerVel(), this.physics.getTimerMaxVel(),
				this.physics.getLastPosChange());
	}

	public void toClassicPhysics() {
		this.physics = new PhysicsClassic(15, this.physics.getAccX(), this.physics.getAccY(), this.physics.getVelX(),
				this.physics.getVelY(), this.physics.getMaxVel(), this.physics.getAvgVelBuff(),
				this.physics.getAvgVel(), this.physics.getTimerVel(), this.physics.getTimerMaxVel(),
				this.physics.getLastPosChange());
	}

	public void toSmokePhysics() {
		this.physics = new PhysicsSmoke(15, this.physics.getAccX(), this.physics.getAccY(), this.physics.getVelX(),
				this.physics.getVelY(), this.physics.getMaxVel(), this.physics.getAvgVelBuff(),
				this.physics.getAvgVel(), this.physics.getTimerVel(), this.physics.getTimerMaxVel(),
				this.physics.getLastPosChange());
	}

	public void swap(CarEntity carentity) {
		carentity.isPlayer = !carentity.isPlayer;
		this.isPlayer = !this.isPlayer;
		this.changeCategory();
		carentity.changeCategory();

		((CityScene) this.parentScene).setCook(carentity);

		Physics physics = this.physics;
		this.physics = carentity.physics;
		carentity.physics = physics;

		carentity.physics.bounce();
		this.physics.bounce();

		carentity.swapInThisTick = true;
		this.swapInThisTick = true;
		this.start = System.currentTimeMillis();
		carentity.start = System.currentTimeMillis();

		if (this.truckHasStock) {
			((KitchenScene) ((GameScreen) this.parentScene.game.getScreen()).getKitchenScene()).addRandomItem();
			this.truckHasStock = false;
		}
	}

}
