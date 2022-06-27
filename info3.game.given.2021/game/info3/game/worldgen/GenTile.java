package info3.game.worldgen;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import info3.game.graphics.Sprite;

public class GenTile {
	public final Sprite buildingSprite;
	public final int buildingSpriteOffsetX, buildingSpriteOffsetY;
	public final CollisionBox collisionBox;
	public final boolean hasMarketPaving;
	public final MarketRoad marketPavingTop, marketPavingLeft;
	public final boolean speedbumpTop, speedbumpLeft;

	GenTile(Sprite buildingSprite, int buildingSpriteOffsetX, int buildingSpriteOffsetY, CollisionBox collisionBox,
			boolean hasMarketPaving, MarketRoad marketPavingTop, MarketRoad marketPavingLeft, boolean speedbumpTop,
			boolean speedbumpLeft) {
		this.buildingSprite = buildingSprite;
		this.buildingSpriteOffsetX = buildingSpriteOffsetX;
		this.buildingSpriteOffsetY = buildingSpriteOffsetY;
		this.collisionBox = collisionBox;
		this.hasMarketPaving = hasMarketPaving;
		this.marketPavingTop = marketPavingTop;
		this.marketPavingLeft = marketPavingLeft;
		this.speedbumpTop = speedbumpTop;
		this.speedbumpLeft = speedbumpLeft;
	}

	GenTile(Sprite sprite, int buildingSpriteOffsetX, int buildingSpriteOffsetY, CollisionBox collision) {
		this(sprite, buildingSpriteOffsetX, buildingSpriteOffsetY, collision, false, null, null, false, false);
	}

	public GenTile withDecoration(boolean hasMarketPaving, boolean speedbumpTop, boolean speedbumpLeft) {
		if (collisionBox.topLeft)
			hasMarketPaving = false;
		if (collisionBox.top)
			speedbumpTop = false;
		if (collisionBox.left)
			speedbumpLeft = false;

		return new GenTile(buildingSprite, buildingSpriteOffsetX, buildingSpriteOffsetY, collisionBox, hasMarketPaving,
				null, null, speedbumpTop, speedbumpLeft);
	}

	public GenTile withMarketPaving(MarketRoad marketPavingTop, MarketRoad marketPavingLeft) {
		if (collisionBox.top)
			marketPavingTop = null;
		if (collisionBox.left)
			marketPavingLeft = null;

		return new GenTile(buildingSprite, buildingSpriteOffsetX, buildingSpriteOffsetY, collisionBox, hasMarketPaving,
				marketPavingTop, marketPavingLeft, speedbumpTop, speedbumpLeft);
	}

	public enum MarketRoad {
		OUT, IN, BOTH,
	}

	public static class CollisionBox {
		public final boolean topLeft, top, left;

		private CollisionBox(boolean topLeft, boolean top, boolean left) {
			this.topLeft = topLeft;
			this.top = top;
			this.left = left;
		}

		static Map<GridPos, CollisionBox> fromOffsets(GridPos[] offsets) {
			return Arrays.stream(offsets).collect(Collectors.toMap(Function.identity(), pos -> {
				boolean top = Arrays.asList(offsets).contains(pos.top());
				boolean left = Arrays.asList(offsets).contains(pos.left());
				boolean topLeft = top && left && Arrays.asList(offsets).contains(pos.top().left());

				return new CollisionBox(topLeft, top, left);
			}));
		}
	}

	public boolean hasRoad() {
		return (!collisionBox.topLeft);
	}
}
