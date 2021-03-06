package com.zarbosoft.merman.helper;

import com.zarbosoft.merman.syntax.FreeAtomType;
import com.zarbosoft.merman.syntax.Syntax;

public class ExpressionSyntax {
	final public static FreeAtomType infinity;
	final public static FreeAtomType factorial;
	final public static FreeAtomType plus;
	final public static FreeAtomType minus;
	final public static FreeAtomType multiply;
	final public static FreeAtomType divide;
	final public static FreeAtomType subscript;
	final public static FreeAtomType inclusiveRange;
	final public static Syntax syntax;

	static {
		infinity = new TypeBuilder("infinity")
				.back(Helper.buildBackPrimitive("infinity"))
				.front(new FrontMarkBuilder("infinity").build())
				.autoComplete(99)
				.build();
		factorial = new TypeBuilder("factorial")
				.middleAtom("value", "any")
				.back(new BackRecordBuilder().add("value", Helper.buildBackDataAtom("value")).build())
				.frontDataNode("value")
				.frontMark("!")
				.autoComplete(99)
				.build();
		plus = new TypeBuilder("plus")
				.middleAtom("first", "any")
				.middleAtom("second", "any")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontDataNode("first")
				.frontMark("+")
				.frontDataNode("second")
				.precedence(10)
				.associateForward()
				.autoComplete(99)
				.build();
		minus = new TypeBuilder("minus")
				.middleAtom("first", "any")
				.middleAtom("second", "any")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontDataNode("first")
				.frontMark("-")
				.frontDataNode("second")
				.precedence(10)
				.associateBackward()
				.autoComplete(99)
				.build();
		multiply = new TypeBuilder("multiply")
				.middleAtom("first", "any")
				.middleAtom("second", "any")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontDataNode("first")
				.frontMark("*")
				.frontDataNode("second")
				.precedence(20)
				.associateForward()
				.autoComplete(99)
				.build();
		divide = new TypeBuilder("divide")
				.middleAtom("first", "any")
				.middleAtom("second", "any")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontDataNode("first")
				.frontMark("/")
				.frontDataNode("second")
				.precedence(20)
				.associateForward()
				.autoComplete(99)
				.build();
		subscript = new TypeBuilder("subscript")
				.middleAtom("first", "name")
				.middleAtom("second", "name")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontDataNode("first")
				.frontMark("_")
				.frontDataNode("second")
				.precedence(0)
				.autoComplete(99)
				.build();
		inclusiveRange = new TypeBuilder("inclusiveRange")
				.middleAtom("first", "any")
				.middleAtom("second", "any")
				.back(new BackRecordBuilder()
						.add("first", Helper.buildBackDataAtom("first"))
						.add("second", Helper.buildBackDataAtom("second"))
						.build())
				.frontMark("[")
				.frontDataNode("first")
				.frontMark(", ")
				.frontDataNode("second")
				.frontMark("]")
				.precedence(50)
				.autoComplete(99)
				.build();
		syntax = new SyntaxBuilder("any")
				.type(infinity)
				.type(factorial)
				.type(plus)
				.type(minus)
				.type(multiply)
				.type(divide)
				.type(subscript)
				.type(inclusiveRange)
				.group("name", new GroupBuilder().type(infinity).type(subscript).build())
				.group(
						"any",
						new GroupBuilder()
								.type(factorial)
								.type(plus)
								.type(minus)
								.type(multiply)
								.type(divide)
								.group("name")
								.type(inclusiveRange)
								.build()
				)
				.build();
	}
}
