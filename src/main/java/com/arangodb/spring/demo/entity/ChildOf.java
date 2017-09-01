/*
 * DISCLAIMER
 *
 * Copyright 2017 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.spring.demo.entity;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;

/**
 * @author Mark Vollmary
 *
 */
@Edge
public class ChildOf {

	@Id
	private String id;

	@From
	private Character child;

	@To
	private Character parent;

	public ChildOf(final Character child, final Character parent) {
		super();
		this.child = child;
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Character getChild() {
		return child;
	}

	public void setChild(final Character child) {
		this.child = child;
	}

	public Character getParent() {
		return parent;
	}

	public void setParent(final Character parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "ChildOf [id=" + id + ", child=" + child + ", parent=" + parent + "]";
	}

}
