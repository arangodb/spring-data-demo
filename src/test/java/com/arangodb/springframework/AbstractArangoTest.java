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

package com.arangodb.springframework;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;

/**
 * @author Mark Vollmary
 *
 */
public class AbstractArangoTest {

	protected static final String COLLECTION_CUSTOMER = "customer";
	protected static final String COLLECTION_SHOPPING_CART = "shopping-cart";
	protected static final String COLLECTION_PRODUCT = "product";
	protected static final String COLLECTION_PERSON = "person";
	protected static final String EDGE_COLLECTION_KNOWS = "knows";
	protected static final String EDGE_COLLECTION_OWNS = "owns";

	protected static final String[] COLLECTIONS = new String[] { COLLECTION_CUSTOMER, COLLECTION_SHOPPING_CART,
			COLLECTION_PRODUCT, COLLECTION_PERSON };
	protected static final String[] EDGE_COLLECTIONS = new String[] { EDGE_COLLECTION_KNOWS, EDGE_COLLECTION_OWNS };

	@Autowired
	protected ArangoOperations template;

	@Before
	public void before() {
		try {
			for (final String collection : COLLECTIONS) {
				template.driver().db(ArangoTestConfiguration.DB).collection(collection).drop();
			}
			for (final String collection : EDGE_COLLECTIONS) {
				template.driver().db(ArangoTestConfiguration.DB).collection(collection).drop();
			}
		} catch (final Exception e) {
		}
		for (final String collection : COLLECTIONS) {
			template.driver().db(ArangoTestConfiguration.DB).createCollection(collection);
		}
		for (final String collection : EDGE_COLLECTIONS) {
			template.driver().db(ArangoTestConfiguration.DB).createCollection(collection,
				new CollectionCreateOptions().type(CollectionType.EDGES));
		}
	}

	@After
	public void after() {
		try {
			for (final String collection : COLLECTIONS) {
				template.driver().db(ArangoTestConfiguration.DB).collection(collection).drop();
			}
			for (final String collection : EDGE_COLLECTIONS) {
				template.driver().db(ArangoTestConfiguration.DB).collection(collection).drop();
			}
		} catch (final Exception e) {
		}
	}
}
