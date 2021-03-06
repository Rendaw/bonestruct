package com.zarbosoft.merman.syntax.back;

import com.zarbosoft.interface1.Configuration;
import com.zarbosoft.merman.editor.backevents.EKeyEvent;
import com.zarbosoft.merman.editor.backevents.EObjectCloseEvent;
import com.zarbosoft.merman.editor.backevents.EObjectOpenEvent;
import com.zarbosoft.merman.syntax.AtomType;
import com.zarbosoft.merman.syntax.Syntax;
import com.zarbosoft.pidgoon.Node;
import com.zarbosoft.pidgoon.events.MatchingEventTerminal;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.pidgoon.nodes.Set;

import java.util.HashMap;
import java.util.Map;

@Configuration(name = "record")
public class BackRecord extends BackPart {
	@Configuration
	public Map<String, BackPart> pairs = new HashMap<>();

	@Override
	public Node buildBackRule(final Syntax syntax, final AtomType atomType) {
		final Sequence sequence;
		sequence = new Sequence();
		sequence.add(new MatchingEventTerminal(new EObjectOpenEvent()));
		final Set set = new Set();
		pairs.forEach((key, value) -> {
			set.add(new Sequence()
					.add(new MatchingEventTerminal(new EKeyEvent(key)))
					.add(value.buildBackRule(syntax, atomType)));
		});
		sequence.add(set);
		sequence.add(new MatchingEventTerminal(new EObjectCloseEvent()));
		return sequence;
	}

	@Override
	public void finish(final Syntax syntax, final AtomType atomType, final java.util.Set<String> middleUsed) {
		pairs.forEach((k, v) -> {
			v.finish(syntax, atomType, middleUsed);
			v.parent = new PartParent() {
				@Override
				public BackPart part() {
					return BackRecord.this;
				}

				@Override
				public String pathSection() {
					return k;
				}
			};
		});
	}
}
