/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.osgi.i18n.webservice;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceBundleWildcardTest {

  @Test
  public void testSetKeyRegex() throws Exception {
    ResourceBundleWildcard resourceBundleWildcard = new ResourceBundleWildcard();
    String regex = "test-regex";
    resourceBundleWildcard.setKeyRegex( regex );
    assertEquals( regex, resourceBundleWildcard.getKeyRegex() );
  }
}