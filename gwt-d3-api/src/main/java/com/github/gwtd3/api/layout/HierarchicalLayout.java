/**
 * Copyright (c) 2013, Anthony Schiochet and Eric Citaire
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * The names Anthony Schiochet and Eric Citaire may not be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL MICHAEL BOSTOCK BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.gwtd3.api.layout;

import java.util.Comparator;
import java.util.List;

import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.svg.Diagonal;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

public class HierarchicalLayout<L, T, N extends Node<T>> extends JavaScriptObject {

    protected HierarchicalLayout() {
    }

    /**
     * Runs the layout, returning the array of nodes associated with the
     * specified root node.
     * <p>
     * The input argument to layout is the root node of the hierarchy, and the output return value is an array
     * representing the computed positions of all nodes. Several attributes are populated on each node:
     *
     * <ul>
     * <li>parent - the parent {@link Node}, or null for the root.
     * <li>children - the array of child nodes, or null for leaf nodes.
     * <li>depth - the depth of the node, starting at 0 for the root.
     * <li>x - the computed x-coordinate of the node position.
     * <li>y - the computed y-coordinate of the node position.
     * </ul>
     *
     * Although the layout has a size in x and y, this represents an arbitrary coordinate system; for example, you can
     * treat x as a radius and y as an angle to produce a radial rather than Cartesian layout.
     *
     * @param data the root node of the hierarchy
     * @return array of computed {@link Node}s
     */
    public final native Array<N> nodes(T data) /*-{
		return this.nodes({
			datum : data
		});
    }-*/;

    /**
     * Given the specified array of nodes, such as those returned by nodes,
     * returns an array of objects representing the from parent to child for
     * each node. Leaf nodes will not have any links. Each link is an object
     * with two attributes:
     *
     * <ul>
     * <li>source - the parent node (as described above).
     * <li>target - the child node.
     * </ul>
     *
     * This method is useful for retrieving a set of link descriptions suitable
     * for display, often in conjunction with the {@link Diagonal} shape
     * generator.
     *
     * @param the array of nodes, as returned by {@link #nodes(Object)}
     * @return array of {@link Link} connecting nodes
     */
    public final native Array<Link<T>> links(Array<N> n) /*-{
		return this.links(n);
    }-*/;

    /**
     * Sets the specified children accessor function. The default children
     * accessor function assumes the input data is an object with a children
     * array. The children accessor is first invoked for root node in the
     * hierarchy. If the accessor returns null, then the node is assumed to be a
     * leaf node at the layout traversal terminates. Otherwise, the accessor
     * should return an array of data elements representing the child nodes.
     *
     * @param df a datum function describing how to compute children
     * @return this layout for chaining
     */
    public final L children(final DatumFunction<List<T>> df) {
        return childrenImpl(new DatumFunction<Array<N>>() {
            @Override
            public Array<N> apply(final Element context, final Value d,
                    final int index) {
                // wrap the list of user values into an array of Nodes
                List<T> userValues = df.apply(context, d, index);
                Array<N> nodes = Array.create();
                for (T t : userValues) {
                    nodes.push(Node.create(t));
                }
                return nodes;
            }
        });

    }

    private final native L childrenImpl(DatumFunction<Array<N>> df) /*-{
		return this
				.children(function(node, depth) {
					return df.@com.github.gwtd3.api.functions.DatumFunction::apply(Lcom/google/gwt/dom/client/Element;Lcom/github/gwtd3/api/core/Value;I)(this,{datum:node.datum},depth);
				});
    }-*/;

    /**
     * Sets the value accessor to the specified function. The value accessor is
     * invoked for each input data element, and must return a number
     * representing the numeric value of the node.
     * <p>
     * For area-proportional layouts such as treemaps, this value is used to set the area of each node proportionally to
     * the value; for other hierarchical layouts, the value has no effect on the layout.
     *
     * @param df a datum function describing how to access node values
     * @return this layout for chaining
     */
    public final native L value(DatumFunction<Integer> df) /*-{
		return this
				.value(function(d, i) {
					return df.@com.github.gwtd3.api.functions.DatumFunction::apply(Lcom/google/gwt/dom/client/Element;Lcom/github/gwtd3/api/core/Value;I)(this,{datum:d},i);
				});
    }-*/;

    /**
     * Returns the current value accessor which defaults to null, meaning that
     * the value attribute is not computed.
     *
     * @return the current datum function registered for calculating node values
     */
    public final native DatumFunction<Integer> value() /*-{
		return this.value();
    }-*/;

    /**
     * Sets the sort order of sibling nodes for the layout using the specified
     * comparator function.
     * <p>
     * The comparator function is invoked for pairs of nodes, being passed the input data for each node. The default
     * comparator is null, which disables sorting and uses tree traversal order. Sorting by the node's name or key is
     * common and can be done easily via {@link #sort(JavaScriptObject)} using {@link D3#ascending()} or
     * {@link D3#descending()}.
     * <p>
     *
     * @param comparator the comparator to use or null to remove the sort
     * @return this layout for chaining
     */
    public final L sort(final Comparator<T> comparator) {
        return sort(comparator != null ? toJSOComparator(comparator) : null);
    }

    /**
     * Sets the sort order of sibling nodes for the layout using the specified
     * comparator function.
     * <p>
     * The comparator function is invoked for pairs of nodes, being passed the input data for each node. The default
     * comparator is null, which disables sorting and uses tree traversal order. Sorting by the node's name or key is
     * common and can be done easily using {@link D3#ascending()} or {@link D3#descending()}.
     * <p>
     * From Java, you may prefer use {@link #sort(Comparator)}.
     *
     * @param sortFunction the comparator to use or null to remove the sort
     * @return this layout for chaining
     */
    public final native L sort(JavaScriptObject sortFunction)/*-{
		return this.sort(sortFunction);
    }-*/;

    /**
     * internal trampoline method
     *
     * @param comparator
     * @return
     */
    protected final native JavaScriptObject toJSOComparator(Comparator<T> comparator) /*-{
		return function(a, b) {
			return comparator.@java.util.Comparator::compare(*)(a,b);
		}
    }-*/;

}
