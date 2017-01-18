package com.zarbosoft.bonestruct.editor.model.back;

import com.zarbosoft.bonestruct.editor.model.NodeType;
import com.zarbosoft.bonestruct.editor.model.Syntax;
import com.zarbosoft.bonestruct.editor.model.middle.DataArray;
import com.zarbosoft.bonestruct.editor.model.middle.DataNode;
import com.zarbosoft.luxemj.Luxem;
import com.zarbosoft.luxemj.source.LArrayCloseEvent;
import com.zarbosoft.luxemj.source.LArrayOpenEvent;
import com.zarbosoft.pidgoon.events.BakedOperator;
import com.zarbosoft.pidgoon.events.Store;
import com.zarbosoft.pidgoon.events.Terminal;
import com.zarbosoft.pidgoon.internal.Helper;
import com.zarbosoft.pidgoon.internal.Node;
import com.zarbosoft.pidgoon.internal.Pair;
import com.zarbosoft.pidgoon.nodes.Reference;
import com.zarbosoft.pidgoon.nodes.Repeat;
import com.zarbosoft.pidgoon.nodes.Sequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Luxem.Configuration(name = "data-array")
public class BackDataArray implements BackPart {
	@Luxem.Configuration
	public String middle;
	private DataArray dataType;

	@Override
	public Node buildLoadRule(final Syntax syntax) {
		final Sequence sequence;
		sequence = new Sequence();
		sequence.add(new BakedOperator(new Terminal(new LArrayOpenEvent()), (store) -> store.pushStack(0)));
		sequence.add(new Repeat(new BakedOperator(
				new Reference(dataType.tag),
				(store) -> com.zarbosoft.pidgoon.internal.Helper.stackSingleElement(store)
		)));
		sequence.add(new Terminal(new LArrayCloseEvent()));
		return new BakedOperator(sequence, (store) -> {
			final List<DataNode.Value> temp = new ArrayList<>();
			store = (Store) com.zarbosoft.pidgoon.internal.Helper.<DataNode.Value>stackPopSingleList(store, temp::add);
			Collections.reverse(temp);
			final DataArray.Value value = new DataArray.Value(syntax, temp);
			store = (Store) store.pushStack(new Pair<>(middle, value));
			return Helper.stackSingleElement(store);
		});
	}

	@Override
	public void finish(final NodeType nodeType, final Set<String> middleUsed) {
		middleUsed.add(middle);
		dataType = nodeType.getDataArray(middle);
	}
}
