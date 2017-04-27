package com.zarbosoft.bonestruct.editor.visual.nodes;

import com.google.common.collect.Iterables;
import com.zarbosoft.bonestruct.document.values.Value;
import com.zarbosoft.bonestruct.editor.Context;
import com.zarbosoft.bonestruct.editor.visual.VisualParent;
import com.zarbosoft.bonestruct.editor.visual.VisualPart;
import com.zarbosoft.bonestruct.wall.Brick;
import com.zarbosoft.bonestruct.wall.bricks.BrickSpace;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.Pair;
import org.pcollections.HashTreePSet;

import java.util.Arrays;
import java.util.Set;

public class VisualSpace extends VisualPart {
	public VisualParent parent;
	public BrickSpace brick;

	public VisualSpace(final Set<Tag> tags) {
		super(HashTreePSet.from(tags).plus(new PartTag("space")));
	}

	@Override
	public Brick getFirstBrick(final Context context) {
		return brick;
	}

	@Override
	public Brick getLastBrick(final Context context) {
		return brick;
	}

	@Override
	public Iterable<Pair<Brick, Brick.Properties>> getPropertiesForTagsChange(
			final Context context, final TagsChange change
	) {
		if (brick == null)
			return Iterables.concat();
		return Arrays.asList(new Pair<Brick, Brick.Properties>(brick,
				brick.getPropertiesForTagsChange(context, change)
		));
	}

	@Override
	public void destroy(final Context context) {
		if (brick != null)
			brick.destroy(context);
	}

	@Override
	public boolean isAt(final Value value) {
		return false;
	}

	@Override
	public void setParent(final VisualParent parent) {
		this.parent = parent;
	}

	@Override
	public VisualParent parent() {
		return parent;
	}

	@Override
	public boolean selectDown(final Context context) {
		return false;
	}

	@Override
	public void select(final Context context) {
		throw new DeadCode();
	}

	@Override
	public void selectUp(final Context context) {
		throw new DeadCode();
	}

	@Override
	public Brick createFirstBrick(final Context context) {
		if (brick != null)
			return null;
		brick = new BrickSpace(this, context);
		return brick;
	}

	@Override
	public Brick createLastBrick(final Context context) {
		return createFirstBrick(context);
	}

	@Override
	public void tagsChanged(final Context context) {
		if (brick != null) {
			brick.setStyle(context);
		}
	}

}