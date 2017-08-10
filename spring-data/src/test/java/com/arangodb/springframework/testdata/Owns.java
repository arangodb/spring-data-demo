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

package com.arangodb.springframework.testdata;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;

/**
 * @author Mark Vollmary
 *
 */
@Edge
public class Owns {

	@From
	private Customer from;
	@To
	private Product to;

	public Owns() {
		super();
	}

	public Owns(final Customer from, final Product to) {
		super();
		this.from = from;
		this.to = to;
	}

	public Customer getFrom() {
		return from;
	}

	public void setFrom(final Customer from) {
		this.from = from;
	}

	public Product getTo() {
		return to;
	}

	public void setTo(final Product to) {
		this.to = to;
	}

}
