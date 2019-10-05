/* -*- mode: Java; c-basic-offset: 2; indent-tabs-mode: nil; coding: utf-8-unix -*-
 *
 * Copyright © 2019 microBean™.
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

import java.lang.annotation.Annotation;

import java.lang.reflect.Type;

import java.util.Collection;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.event.Observes;

import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An {@link Extension} that permits {@link ObjectMapper} instances to
 * be injected in CDI-based applications.
 *
 * @author <a href="https://about.me/lairdnelson"
 * target="_parent">Laird Nelson</a>
 */
public final class JacksonCdiExtension implements Extension {

  private final Set<Set<Annotation>> qualifierSets;

  /**
   * Creates a new {@link JacksonCdiExtension}.
   */
  public JacksonCdiExtension() {
    super();
    this.qualifierSets = new HashSet<>();
  }

  private final <T, X extends ObjectMapper> void processInjectionPoint(@Observes final ProcessInjectionPoint<T, X> event) {
    Objects.requireNonNull(event);
    final InjectionPoint injectionPoint = event.getInjectionPoint();
    if (injectionPoint != null) {
      this.qualifierSets.add(injectionPoint.getQualifiers());
    }
  }

  private final void afterBeanDiscovery(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
    Objects.requireNonNull(event);
    Objects.requireNonNull(beanManager);
    for (final Set<Annotation> qualifiers : this.qualifierSets) {
      final Annotation[] qualifiersArray;
      if (qualifiers == null || qualifiers.isEmpty()) {
        qualifiersArray = null;
      } else {
        qualifiersArray = qualifiers.toArray(new Annotation[qualifiers.size()]);
      }
      if (noBeans(beanManager, ObjectMapper.class, qualifiersArray)) {
        event.addBean()
          .scope(ApplicationScoped.class)
          .addTransitiveTypeClosure(ObjectMapper.class)
          .qualifiers(qualifiers)
          .createWith(cc -> createObjectMapper(beanManager, qualifiersArray));
      }
    }
    this.qualifierSets.clear();
  }

  private static final ObjectMapper createObjectMapper(final BeanManager beanManager, final Annotation[] qualifiersArray) {
    Objects.requireNonNull(beanManager);
    final ObjectMapper returnValue = new ObjectMapper();
    returnValue.findAndRegisterModules();
    beanManager.getEvent().select(ObjectMapper.class, qualifiersArray).fire(returnValue);
    return returnValue;
  }

  private static final boolean noBeans(final BeanManager beanManager, final Type type, final Annotation[] qualifiersArray) {
    final Collection<?> beans;
    if (beanManager != null && type != null) {
      if (qualifiersArray == null || qualifiersArray.length <= 0) {
        beans = beanManager.getBeans(type);
      } else {
        beans = beanManager.getBeans(type, qualifiersArray);
      }
    } else {
      beans = null;
    }
    return beans == null || beans.isEmpty();
  }
  
}
