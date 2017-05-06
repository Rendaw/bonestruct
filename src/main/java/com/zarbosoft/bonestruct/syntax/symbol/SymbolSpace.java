package com.zarbosoft.bonestruct.syntax.symbol;

import com.zarbosoft.bonestruct.editor.Context;
import com.zarbosoft.bonestruct.editor.display.Blank;
import com.zarbosoft.bonestruct.editor.display.DisplayNode;
import com.zarbosoft.bonestruct.editor.wall.Brick;
import com.zarbosoft.bonestruct.editor.wall.BrickInterface;
import com.zarbosoft.bonestruct.editor.wall.bricks.BrickSpace;
import com.zarbosoft.bonestruct.syntax.style.Style;
import com.zarbosoft.interface1.Configuration;

@Configuration(name = "space")
public class SymbolSpace extends Symbol {
	@Override
	public DisplayNode createDisplay(final Context context) {
		final Blank blank = context.display.blank();
		return blank;
	}

	@Override
	public void style(final Context context, final DisplayNode node, final Style.Baked style) {
	}

	@Override
	public Brick createBrick(final Context context, final BrickInterface inter) {
		return new BrickSpace(context, inter);
	}
}
