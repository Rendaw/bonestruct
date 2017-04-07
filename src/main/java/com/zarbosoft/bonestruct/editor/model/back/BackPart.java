package com.zarbosoft.bonestruct.editor.model.back;

import com.zarbosoft.bonestruct.Path;
import com.zarbosoft.bonestruct.editor.model.NodeType;
import com.zarbosoft.bonestruct.editor.model.Syntax;
import com.zarbosoft.interface1.Configuration;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.Pair;

import java.util.Set;

@Configuration
public abstract class BackPart {
	public abstract Node buildBackRule(Syntax syntax, NodeType nodeType);

	public Parent parent = null;

	public void finish(final Syntax syntax, final NodeType nodeType, final Set<String> middleUsed) {
	}

	final public Pair<Integer, Path> getSubpath() {
		if (parent instanceof NodeType.NodeBackParent)
			return new Pair<>(((NodeType.NodeBackParent) parent).index, new Path());
		else if (parent instanceof PartParent) {
			final Pair<Integer, Path> base = ((PartParent) parent).part().getSubpath();
			return new Pair<>(base.first, base.second.add(((PartParent) parent).pathSection()));
		} else
			throw new DeadCode();
	}

	public abstract static class Parent {

	}

	public abstract static class PartParent extends Parent {
		public abstract BackPart part();

		public abstract String pathSection();
	}
}
