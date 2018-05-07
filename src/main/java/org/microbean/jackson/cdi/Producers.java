/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2018 microBean.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.microbean.jackson.cdi;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.event.Event;

import javax.enterprise.inject.Produces;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@ApplicationScoped
final class Producers {

  private final Event<ObjectMapper> broadcaster;
  
  @Inject
  private Producers(final Event<ObjectMapper> broadcaster) {
    super();
    this.broadcaster = broadcaster;
  }

  @Produces
  @ApplicationScoped
  private final ObjectMapper produceObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    if (this.broadcaster != null) {
      // Give other configurators a chance to configure the one true
      // ObjectMapper that will be made.
      this.broadcaster.fire(objectMapper);
    }
    // Find and register Java SPI-provided Module implementations on
    // the classpath.  Duplicates will be rejected by default.
    objectMapper.findAndRegisterModules();
    return objectMapper;
  }
  
}
