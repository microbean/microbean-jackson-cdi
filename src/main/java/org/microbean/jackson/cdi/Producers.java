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

/**
 * A package-protected class housing producer methods.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
@ApplicationScoped
final class Producers {


  /*
   * Instance fields.
   */

  
  /**
   * An {@link Event} broadcaster that fires {@link ObjectMapper}
   * instances as events during bean production permitting arbitrary
   * customization by other code.
   *
   * <p>This field is never {@code null}.</p>
   */
  private final Event<ObjectMapper> broadcaster;


  /*
   * Constructors.
   */



  /**
   * Creates a {@link Producers}.
   *
   * @param broadcaster an {@link Event} broadcaster that fires {@link
   * ObjectMapper} instances as events during bean production
   * permitting arbitrary customization by other code; may be {@code
   * null}
   */
  @Inject
  private Producers(final Event<ObjectMapper> broadcaster) {
    super();
    this.broadcaster = broadcaster;
  }


  /*
   * Instance methods.
   */


  /**
   * A producer method that produces {@link ObjectMapper} instances in
   * the {@linkplain ApplicationScoped application scope}.
   *
   * @return a new {@link ObjectMapper}; never {@code null}
   */
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
