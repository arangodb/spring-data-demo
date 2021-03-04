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

package com.arangodb.spring.demo;

import com.arangodb.spring.demo.runner.AQLRunner;
import com.arangodb.spring.demo.runner.ByExampleRunner;
import com.arangodb.spring.demo.runner.CrudRunner;
import com.arangodb.spring.demo.runner.DerivedQueryRunner;
import com.arangodb.spring.demo.runner.GeospatialRunner;
import com.arangodb.spring.demo.runner.RelationsRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Mark Vollmary
 */
@SpringBootApplication
public class DemoApplication {
    public static void main(final String... args) {
        final Class<?>[] runner = new Class<?>[]{
                CrudRunner.class,
                ByExampleRunner.class,
                DerivedQueryRunner.class,
                RelationsRunner.class,
                AQLRunner.class,
                GeospatialRunner.class
        };
        System.exit(SpringApplication.exit(SpringApplication.run(runner, args)));
    }
}