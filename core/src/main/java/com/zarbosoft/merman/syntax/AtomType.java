package com.zarbosoft.merman.syntax;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.zarbosoft.interface1.Configuration;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.syntax.alignments.AlignmentDefinition;
import com.zarbosoft.merman.syntax.back.*;
import com.zarbosoft.merman.syntax.front.FrontPart;
import com.zarbosoft.merman.syntax.middle.*;
import com.zarbosoft.pidgoon.events.Operator;
import com.zarbosoft.pidgoon.events.Store;
import com.zarbosoft.pidgoon.internal.Helper;
import com.zarbosoft.pidgoon.nodes.Sequence;
import com.zarbosoft.rendaw.common.DeadCode;
import com.zarbosoft.rendaw.common.Pair;

import java.util.*;

import static com.zarbosoft.rendaw.common.Common.enumerate;

@Configuration
public abstract class AtomType {

	@Configuration
	public Set<String> tags = new HashSet<>();

	public abstract List<FrontPart> front();

	public abstract Map<String, MiddlePart> middle();

	public abstract List<BackPart> back();

	public abstract Map<String, AlignmentDefinition> alignments();

	public abstract int precedence();

	public abstract boolean associateForward();

	public abstract String id();

	public abstract int depthScore();

	public static class NodeBackParent extends BackPart.Parent {
		public int index;

		public NodeBackParent(final int index) {
			this.index = index;
		}
	}

	public void finish(final Syntax syntax, final Set<String> allTypes, final Set<String> scalarTypes) {
		middle().forEach((k, v) -> {
			v.id = k;
			v.finish(allTypes, scalarTypes);
		});
		{
			final Set<String> middleUsedBack = new HashSet<>();
			enumerate(back().stream()).forEach(pair -> {
				final Integer i = pair.first;
				final BackPart p = pair.second;
				p.finish(syntax, this, middleUsedBack);
				p.parent = new NodeBackParent(i);
			});
			final Set<String> missing = Sets.difference(middle().keySet(), middleUsedBack);
			if (!missing.isEmpty())
				throw new InvalidSyntax(String.format("Middle elements %s in %s are unused by back parts.",
						missing,
						id()
				));
		}
		{
			final Set<String> middleUsedFront = new HashSet<>();
			front().forEach(p -> p.finish(this, middleUsedFront));
			final Set<String> missing = Sets.difference(middle().keySet(), middleUsedFront);
			if (!missing.isEmpty())
				throw new InvalidSyntax(String.format("Middle elements %s in %s are unused by front parts.",
						missing,
						id()
				));
		}
	}

	public com.zarbosoft.pidgoon.Node buildBackRule(final Syntax syntax) {
		final Sequence seq = new Sequence();
		seq.add(new Operator((store) -> store.pushStack(0)));
		back().forEach(p -> seq.add(p.buildBackRule(syntax, this)));
		return new Operator(seq, store -> {
			final Map<String, Value> data = new HashMap<>();
			store = (Store) Helper.<Pair<String, Value>>stackPopSingleList(store,
					pair -> data.put(pair.first, pair.second)
			);
			final Atom atom = new Atom(this, data);
			return store.pushStack(atom);
		});
	}

	public abstract String name();

	public BackPart getBackPart(final String id) {
		final Deque<Iterator<BackPart>> stack = new ArrayDeque<>();
		stack.addLast(back().iterator());
		while (!stack.isEmpty()) {
			final Iterator<BackPart> iterator = stack.pollLast();
			if (!iterator.hasNext())
				continue;
			stack.addLast(iterator);
			final BackPart next = iterator.next();
			if (next instanceof BackArray) {
				stack.addLast(((BackArray) next).elements.iterator());
			} else if (next instanceof BackRecord) {
				stack.addLast(((BackRecord) next).pairs.values().iterator());
			} else if (next instanceof BackDataArray) {
				if (((BackDataArray) next).middle.equals(id))
					return next;
			} else if (next instanceof BackDataRootArray) {
				if (((BackDataRootArray) next).middle.equals(id))
					return next;
			} else if (next instanceof BackDataKey) {
				if (((BackDataKey) next).middle.equals(id))
					return next;
			} else if (next instanceof BackDataAtom) {
				if (((BackDataAtom) next).middle.equals(id))
					return next;
			} else if (next instanceof BackType) {
				stack.addLast(Iterators.singletonIterator(((BackType) next).value));
			} else if (next instanceof BackDataType) {
				if (((BackDataType) next).type.equals(id))
					return next;
			} else if (next instanceof BackDataPrimitive) {
				if (((BackDataPrimitive) next).middle.equals(id))
					return next;
			} else if (next instanceof BackDataRecord) {
				if (((BackDataRecord) next).middle.equals(id))
					return next;
			}
		}
		throw new DeadCode();
	}

	public MiddleRecord getDataRecord(final String middle) {
		return getData(MiddleRecord.class, middle);
	}

	private <D extends MiddlePart> D getData(final Class<? extends MiddlePart> type, final String id) {
		final MiddlePart found = middle().get(id);
		if (found == null)
			throw new InvalidSyntax(String.format("No middle element [%s] in [%s]", id, this.id()));
		if (!type.isAssignableFrom(found.getClass()))
			throw new InvalidSyntax(String.format("Conflicting types for middle element [%s] in [%s]: %s, %s",
					id,
					this.id(),
					found.getClass(),
					type
			));
		return (D) found;
	}

	public MiddlePrimitive getDataPrimitive(final String key) {
		return getData(MiddlePrimitive.class, key);
	}

	public MiddleAtom getDataNode(final String key) {
		return getData(MiddleAtom.class, key);
	}

	public MiddleArrayBase getDataArray(final String key) {
		return getData(MiddleArrayBase.class, key);
	}

	@Override
	public String toString() {
		return String.format("<type %s>", id());
	}
}
