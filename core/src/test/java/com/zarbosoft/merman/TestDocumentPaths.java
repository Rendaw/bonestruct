package com.zarbosoft.merman;

import com.google.common.collect.ImmutableList;
import com.zarbosoft.merman.document.Atom;
import com.zarbosoft.merman.document.values.Value;
import com.zarbosoft.merman.document.values.ValueArray;
import com.zarbosoft.merman.document.values.ValueAtom;
import com.zarbosoft.merman.editor.Context;
import com.zarbosoft.merman.editor.Path;
import com.zarbosoft.merman.helper.*;
import com.zarbosoft.merman.syntax.Syntax;
import org.junit.Test;

import static com.zarbosoft.merman.helper.Helper.buildDoc;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class TestDocumentPaths {
	@Test
	public void testRoot() {
		final Context context = buildDoc(MiscSyntax.syntax,
				new TreeBuilder(MiscSyntax.quoted).add("value", "").build(),
				new TreeBuilder(MiscSyntax.multiback).add("a", "").add("b", "").build(),
				new TreeBuilder(MiscSyntax.quoted).add("value", "").build()
		);
		final Value value1 = Helper.rootArray(context.document).data.get(0).data.get("value");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
		final Value value2 = Helper.rootArray(context.document).data.get(1).data.get("b");
		assertThat(value2.getPath().toList(), equalTo(ImmutableList.of("2")));
		assertThat(context.locateLong(value2.getPath()), equalTo(value2));
		final Value value3 = Helper.rootArray(context.document).data.get(2).data.get("value");
		assertThat(value3.getPath().toList(), equalTo(ImmutableList.of("3")));
		assertThat(context.locateLong(value3.getPath()), equalTo(value3));
	}

	@Test
	public void testRecord() {
		final Syntax syntax = new SyntaxBuilder("any")
				.type(new TypeBuilder("base")
						.back(new BackRecordBuilder().add("a", Helper.buildBackDataPrimitive("a")).build())
						.middlePrimitive("a")
						.frontDataPrimitive("a")
						.build())
				.group("any", ImmutableList.of("base"))
				.build();
		final Context context = buildDoc(syntax, new TreeBuilder(syntax.types.get(0)).add("a", "").build());
		final Value value1 = Helper.rootArray(context.document).data.get(0).data.get("a");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0", "a")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
	}

	@Test
	public void testArray() {
		final Syntax syntax = new SyntaxBuilder("any")
				.type(new TypeBuilder("base")
						.back(new BackArrayBuilder().add(Helper.buildBackDataPrimitive("a")).build())
						.middlePrimitive("a")
						.frontDataPrimitive("a")
						.build())
				.group("any", ImmutableList.of("base"))
				.build();
		final Context context = buildDoc(syntax, new TreeBuilder(syntax.types.get(0)).add("a", "").build());
		final Value value1 = Helper.rootArray(context.document).data.get(0).data.get("a");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0", "0")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
	}

	@Test
	public void testDataNode() {
		final Syntax syntax = new SyntaxBuilder("any")
				.type(new TypeBuilder("base")
						.back(Helper.buildBackDataAtom("a"))
						.middleAtom("a", "child")
						.frontDataNode("a")
						.build())
				.type(new TypeBuilder("child")
						.back(Helper.buildBackDataPrimitive("b"))
						.middlePrimitive("b")
						.frontDataPrimitive("b")
						.build())
				.group("any", ImmutableList.of("base"))
				.build();
		final Context context = buildDoc(syntax,
				new TreeBuilder(syntax.types.get(0)).add("a", new TreeBuilder(syntax.types.get(1)).add("b", "")).build()
		);
		final Value value1 =
				((ValueAtom) Helper.rootArray(context.document).data.get(0).data.get("a")).data.data.get("b");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
	}

	@Test
	public void testDataArray() {
		final Syntax syntax = new SyntaxBuilder("any")
				.type(new TypeBuilder("base")
						.back(Helper.buildBackDataArray("a"))
						.middleArray("a", "child")
						.frontDataArray("a")
						.build())
				.type(new TypeBuilder("child")
						.back(Helper.buildBackDataPrimitive("b"))
						.middlePrimitive("b")
						.frontDataPrimitive("b")
						.build())
				.group("any", ImmutableList.of("base"))
				.build();
		final Context context = buildDoc(syntax,
				new TreeBuilder(syntax.types.get(0))
						.addArray("a", ImmutableList.of(new TreeBuilder(syntax.types.get(1)).add("b", "").build()))
						.build()
		);
		final Value value1 =
				((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("a")).data.get(0).data.get("b");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0", "0")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
	}

	@Test
	public void testDataRecord() {
		final Syntax syntax = new SyntaxBuilder("any")
				.type(new TypeBuilder("base")
						.back(Helper.buildBackDataRecord("a"))
						.middleRecord("a", "element")
						.frontDataArray("a")
						.build())
				.type(new TypeBuilder("element")
						.back(Helper.buildBackDataKey("k"))
						.middlePrimitive("k")
						.frontDataPrimitive("k")
						.back(Helper.buildBackDataPrimitive("v"))
						.middlePrimitive("v")
						.frontDataPrimitive("v")
						.build())
				.group("any", ImmutableList.of("base"))
				.build();
		final Context context = buildDoc(syntax,
				new TreeBuilder(syntax.types.get(0))
						.addRecord("a", new TreeBuilder(syntax.types.get(1)).add("k", "K").add("v", "V").build())
						.build()
		);
		final Value value1 =
				((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("a")).data.get(0).data.get("v");
		assertThat(value1.getPath().toList(), equalTo(ImmutableList.of("0", "K")));
		assertThat(context.locateLong(value1.getPath()), equalTo(value1));
	}

	@Test
	public void testLocateRootElement() {
		final Context context = buildDoc(MiscSyntax.syntax,
				new TreeBuilder(MiscSyntax.quoted).add("value", "").build(),
				new TreeBuilder(MiscSyntax.multiback).add("a", "").add("b", "").build(),
				new TreeBuilder(MiscSyntax.quoted).add("value", "").build()
		);
		final Path path0 = new Path("0");
		assertThat(context.locateLong(path0),
				equalTo(Helper.rootArray(context.document).data.get(0).data.get("value"))
		);
		final Path path1 = new Path("1");
		assertThat(context.locateLong(path1), equalTo(Helper.rootArray(context.document).data.get(1).data.get("a")));
		final Path path2 = new Path("2");
		assertThat(context.locateLong(path2), equalTo(Helper.rootArray(context.document).data.get(1).data.get("b")));
		final Path path3 = new Path("3");
		assertThat(context.locateLong(path3),
				equalTo(Helper.rootArray(context.document).data.get(2).data.get("value"))
		);
	}

	@Test
	public void testLocateEmpty() {
		final Context context = buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.one).build());
		assertThat(context.locateLong(new Path("0")), equalTo(Helper.rootArray(context.document).data.get(0)));
	}

	@Test
	public void testLocateArrayPrimitiveLong() {
		final Context context =
				buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "x").build());
		assertThat(context.locateLong(new Path("0")),
				equalTo(Helper.rootArray(context.document).data.get(0).data.get("value"))
		);
	}

	@Test
	public void testLocateArrayPrimitiveShort() {
		final Context context =
				buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "x").build());
		assertThat(context.locateShort(new Path("0")), equalTo(Helper.rootArray(context.document).data.get(0)));
	}

	@Test
	public void testLocateNodePrimitiveLong() {
		final Atom quoted = new TreeBuilder(MiscSyntax.quoted).add("value", "x").build();
		final Context context =
				buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.snooze).add("value", quoted).build());
		assertThat(context.locateLong(new Path("0", "value")), equalTo(quoted.data.get("value")));
	}

	@Test
	public void testLocateNodePrimitiveShort() {
		final Atom quoted = new TreeBuilder(MiscSyntax.quoted).add("value", "x").build();
		final Context context =
				buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.snooze).add("value", quoted).build());
		assertThat(context.locateShort(new Path("0", "value")), equalTo(quoted.parent.value()));
	}

	@Test
	public void testLocatePrimitive() {
		final Context context =
				buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.quoted).add("value", "").build());
		assertThat(context.locateLong(new Path("0")),
				equalTo(Helper.rootArray(context.document).data.get(0).data.get("value"))
		);
	}

	@Test
	public void testLocateRecordNode() {
		final Context context = buildDoc(MiscSyntax.syntax,
				new TreeBuilder(MiscSyntax.plus)
						.add("first", new TreeBuilder(MiscSyntax.one))
						.add("second", new TreeBuilder(MiscSyntax.one))
						.build()
		);
		assertThat(context.locateLong(new Path("0")), equalTo(Helper.rootArray(context.document).data.get(0)));
		assertThat(context.locateLong(new Path("0", "first")),
				equalTo(((ValueAtom) Helper.rootArray(context.document).data.get(0).data.get("first")).data)
		);
		assertThat(context.locateLong(new Path("0", "second")),
				equalTo(((ValueAtom) Helper.rootArray(context.document).data.get(0).data.get("second")).data)
		);
	}

	@Test
	public void testLocateRecordPrimitive() {
		final Context context = buildDoc(MiscSyntax.syntax,
				new TreeBuilder(MiscSyntax.ratio).add("first", "").add("second", "").build()
		);
		assertThat(context.locateLong(new Path("0", "first")),
				equalTo(Helper.rootArray(context.document).data.get(0).data.get("first"))
		);
		assertThat(context.locateLong(new Path("0", "second")),
				equalTo(Helper.rootArray(context.document).data.get(0).data.get("second"))
		);
	}

	@Test
	public void testLocateArrayElement() {
		final Context context = buildDoc(MiscSyntax.syntax,
				new TreeBuilder(MiscSyntax.pair)
						.add("first", new TreeBuilder(MiscSyntax.one))
						.add("second", new TreeBuilder(MiscSyntax.one))
						.build()
		);
		assertThat(context.locateLong(new Path("0", "0")),
				equalTo(((ValueAtom) Helper.rootArray(context.document).data.get(0).data.get("first")).data)
		);
		assertThat(context.locateLong(new Path("0", "1")),
				equalTo(((ValueAtom) Helper.rootArray(context.document).data.get(0).data.get("second")).data)
		);
	}

	@Test
	public void testLocateDataRecordElement() {
		final Context context = buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.record).addRecord("value",
				new TreeBuilder(MiscSyntax.recordElement)
						.add("key", "first")
						.add("value", new TreeBuilder(MiscSyntax.one))
						.build(),
				new TreeBuilder(MiscSyntax.recordElement)
						.add("key", "second")
						.add("value", new TreeBuilder(MiscSyntax.one))
						.build()
		).build());
		assertThat(context.locateLong(new Path("0", "first")), equalTo((
				(ValueAtom) ((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("value")).data.get(0).data
						.get("value")
		).data));
		assertThat(context.locateLong(new Path("0", "second")), equalTo((
				(ValueAtom) ((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("value")).data.get(1).data
						.get("value")
		).data));
	}

	@Test
	public void testLocateDataArrayElement() {
		final Context context = buildDoc(MiscSyntax.syntax, new TreeBuilder(MiscSyntax.array)
				.addArray("value",
						new TreeBuilder(MiscSyntax.one).build(),
						new TreeBuilder(MiscSyntax.one).build()
				)
				.build());
		assertThat(context.locateLong(new Path("0", "0")),
				equalTo(((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("value")).data.get(0))
		);
		assertThat(context.locateLong(new Path("0", "1")),
				equalTo(((ValueArray) Helper.rootArray(context.document).data.get(0).data.get("value")).data.get(1))
		);
	}
}
