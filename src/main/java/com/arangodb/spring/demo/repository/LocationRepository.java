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

package com.arangodb.spring.demo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoPage;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import com.arangodb.spring.demo.entity.Location;
import com.arangodb.springframework.repository.ArangoRepository;

/**
 * @author Mark Vollmary
 *
 */
public interface LocationRepository extends ArangoRepository<Location, String> {

	GeoPage<Location> findByLocationNear(Point location, Pageable pageable);

	GeoResults<Location> findByLocationWithin(Point location, Distance distance);

	Iterable<Location> findByLocationWithin(Point location, Range<Double> distanceRange);

	Iterable<Location> findByLocationWithin(Polygon polygon);

}
