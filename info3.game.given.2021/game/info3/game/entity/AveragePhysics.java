package info3.game.entity;

import info3.game.position.AutDirection;
import info3.game.position.PositionF;

public class AveragePhysics implements Physics {

	double accX = 0, accY = 0;
	double velX = 0, velY = 0;
	double maxVel = 100;
	final double force;
	double friction = 1.5f;

	double lastVel = 0;
	double avgVelBuff = 0, avgVel = 0;
	int timerVel = 0, timerMaxVel = 10;
	PositionF lastPosChange = new PositionF(0, 0);

	/**
	 * average force is 15
	 * 
	 * @param force
	 */
	public AveragePhysics(int force) {
		this.force = force;
	}

	/**
	 * average force is 15
	 * 
	 * @param force
	 */
	public AveragePhysics(int force, double accX, double accY, double velX, double velY, double maxVel,
			double avgVelBuff, double avgVel, int timerVel, int timerMaxVel, PositionF lastPosChange) {
		this.force = force;
		this.accX = accX;
		this.accY = accY;
		this.velX = velX;
		this.velY = velY;
		this.maxVel = maxVel;
		this.avgVelBuff = avgVelBuff;
		this.avgVel = avgVel;
		this.timerVel = timerVel;
		this.timerMaxVel = timerMaxVel;
		this.lastPosChange = lastPosChange;
	}

	@Override
	public void addForce(AutDirection absoluteDir) {
		switch (absoluteDir) {
		case N:
			accY -= force;
			accX = 0;
			velX /= friction;
			break;
		case E:
			accX += force;
			accY = 0;
			velY /= friction;
			break;
		case S:
			accY += force;
			accX = 0;
			velX /= friction;
			break;
		case W:
			accX -= force;
			accY = 0;
			velY /= friction;
			break;
		default:
			break;
		}
	}

	@Override
	public void removeForce() {
		accY = 0;
		accX = 0;
		velX /= friction;
		velY /= friction;
	}

	@Override
	public void stop() {
		accY = 0;
		accY = 0;
		velX = 0;
		velY = 0;
	}

	@Override
	public PositionF shift() {
		float shiftX = 0, shiftY = 0;

		shiftX += velX;
		shiftY += velY;

		velX += accX;
		velY += accY;

		if (shiftX > maxVel) {
			shiftX = (float) maxVel;
			velX = maxVel;
			accX = 0;
		} else if (shiftX < -maxVel) {
			shiftX = (float) -maxVel;
			velX = -maxVel;
			accX = 0;
		}
		if (shiftY > maxVel) {
			shiftY = (float) maxVel;
			velY = maxVel;
			accY = 0;
		} else if (shiftY < -maxVel) {
			shiftY = (float) -maxVel;
			velY = -maxVel;
			accY = 0;
		}
		if (timerVel == 0) {
			timerVel = timerMaxVel;
			avgVel = avgVelBuff / 10;
			avgVelBuff = 0;
		} else {
			avgVelBuff += Math.hypot(shiftX, shiftY);
			timerVel--;
		}
		lastVel = Math.hypot(shiftX, shiftY);
		lastPosChange = new PositionF(shiftX / 100, shiftY / 100);

		return lastPosChange;
	}

	@Override
	public int getVelocity() {
		return (int) (avgVel / 3);
	}

	public double getAccX() {
		return accX;
	}

	public double getAccY() {
		return accY;
	}

	public double getVelX() {
		return velX;
	}

	public double getVelY() {
		return velY;
	}

	public double getMaxVel() {
		return maxVel;
	}

	public double getAvgVelBuff() {
		return avgVelBuff;
	}

	public double getAvgVel() {
		return avgVel;
	}

	public int getTimerVel() {
		return timerVel;
	}

	public int getTimerMaxVel() {
		return timerMaxVel;
	}

	@Override
	public PositionF bounce() {
		accX = 0;
		accY = 0;
		velX = -velX / 16;
		velY = -velY / 16;
		return lastPosChange.neg();

	}

	@Override
	public int getInRealTimeVelocity() {
		return (int) lastVel;
	}

	@Override
	public PositionF getLastPosChange() {
		return lastPosChange;
	}

}
