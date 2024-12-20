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

package org.pentaho.osgi.platform.plugin.deployer.impl.handlers.pluginxml;

import org.pentaho.osgi.platform.plugin.deployer.api.PluginHandlingException;
import org.pentaho.osgi.platform.plugin.deployer.api.PluginMetadata;
import org.pentaho.osgi.platform.plugin.deployer.impl.handlers.PluginXmlFileHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.pentaho.osgi.platform.plugin.deployer.impl.handlers.pluginxml.PluginXmlStaticPathsHandler
  .BLUEPRINT_BEAN_NS;

/**
 * Created by nbaker on 7/18/16.
 */
public class PluginXmlLifecycleListenerHandler extends PluginXmlFileHandler {

  public PluginXmlLifecycleListenerHandler() {
    super( "//lifecycle-listener" );
  }

  @Override protected void handle( String relativePath, List<Node> nodes, PluginMetadata pluginMetadata )
    throws PluginHandlingException {

    pluginMetadata.executeAtEnd( () -> {
      Document blueprint = pluginMetadata.getBlueprint();

      for ( Node node : nodes ) {
        Map<String, String> attributes = getAttributes( node );
        if ( attributes.containsKey( "ignore" ) ) {
          continue;
        }

        String clazz = attributes.get( "class" );
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext( new NamespaceContext() {
          @Override public String getNamespaceURI( String prefix ) {
            return "http://www.osgi.org/xmlns/blueprint/v1.0.0";
          }

          @Override public String getPrefix( String namespaceURI ) {
            return null;
          }

          @Override public Iterator getPrefixes( String namespaceURI ) {
            return null;
          }
        } );
        String expression = "//bp:bean[@class='" + clazz + "']";
        try {
          XPathExpression compiledExpression = xpath.compile( expression );
          NodeList nodeList = (NodeList) compiledExpression.evaluate( blueprint, XPathConstants.NODESET );
          if ( nodeList.getLength() > 0 ) {
            // alredy defined, maybe by plugin author
            continue;
          }
        } catch ( XPathExpressionException e ) {
          e.printStackTrace();
        }
        Element bean = blueprint.createElementNS( BLUEPRINT_BEAN_NS,
          "bean" );
        bean.setAttribute( "class", clazz );
        bean.setAttribute( "init-method", "init" );
        blueprint.getDocumentElement().appendChild( bean );
      }

    } );

  }
}
