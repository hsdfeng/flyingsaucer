/*
* {{{ header & license
* Copyright (c) 2004 Joshua Marinacci
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public License
* as published by the Free Software Foundation; either version 2.1
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
* }}}
*/
package org.xhtmlrenderer.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.forms.InputButton;
import org.xhtmlrenderer.forms.InputCheckbox;
import org.xhtmlrenderer.forms.InputImage;
import org.xhtmlrenderer.forms.InputPassword;
import org.xhtmlrenderer.forms.InputRadio;
import org.xhtmlrenderer.forms.InputSelect;
import org.xhtmlrenderer.forms.InputText;
import org.xhtmlrenderer.forms.InputTextArea;
import org.xhtmlrenderer.render.Renderer;
import org.xhtmlrenderer.table.TableCellLayout;
import org.xhtmlrenderer.table.TableLayout2;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.u;

/**
* Returns the appropriate layout for a given node. Currently this hard codes
* specific elements to specific layouts (for example, h1 is always an inline
* layout). Instead this should use the display attribute to figure it out. The
* Inline and Box layouts work together so that you don't need to specify one or
* the other, always just use Inline. For lists, tables, and comments, though,
* we still need to specify the particular layout here.
*
 * @author    jmarinacci
 * @created   October 18, 2004
 */

public class LayoutFactory {
    /** Description of the Field */
    private Map element_map = new HashMap();
    private Map display_map = new HashMap();


    public LayoutFactory() {
        initializeLayouts();
    }
    /**
    * <p>
    *
    * Associate a particular layout with a certain element name. This lets you
    * add new layouts to the system or override existing ones. The following
    * example overrides the default layout for paragraphs with the custom
    * <code>FunnyLayout</code>. <pre>Layout lt = new FunnyLayout();
    *LayoutFactory.addCustomLayout("p",lt);
    *</pre>
    *
    * @param elem_name  The string name of the element to be added
    * @param layout     The layout to associate with the specified element
    */
    public void addCustomLayout( String elem_name, Layout layout ) {
        addElementLayout(elem_name, layout);
    }
    
    /* add a new layout by display name */
    public void addDisplayLayout( String display_name, Layout layout) {
        display_map.put( display_name, layout );
    }
    /** add a new layout by element name. */
    public void addElementLayout( String element_name, Layout layout) {
        element_map.put( element_name, layout );
    }


    /**
    * <p>
    *
    * Retreive the correct Layout for the specified Node</p>
    *
    * @param elem  Element to retrieve layout for.
    * @return      the correct layout for this node
    * @see         org.xhtmlrenderer.layout.Layout
    */

    public Layout getLayout(Context c, Node elem ) {
        //u.p("getting layout for node: " + elem);
        // we have to do the inputs manually since they don't depend on
        // the element name
        if ( elem.getNodeType() == elem.ELEMENT_NODE ) {
            if ( elem.getNodeName().equals( "input" ) ) {
                Element el = (Element)elem;
                String type = el.getAttribute( "type" );
                if ( type == null ) {
                    return new InputButton();
                }
                if ( type.equals( "button" ) ) {
                    return new InputButton();
                }

                if ( type.equals( "checkbox" ) ) {
                    return new InputCheckbox();
                }

                //if(type.equals("file")) { return new InputCheckbox(); }
                if ( type.equals( "hidden" ) ) {
                    return new NullLayout();
                }
                if ( type.equals( "image" ) ) {
                    return new InputImage();
                }
                if ( type.equals( "password" ) ) {
                    return new InputPassword();
                }
                if ( type.equals( "radio" ) ) {
                    return new InputRadio();
                }
                if ( type.equals( "reset" ) ) {
                    return new InputButton();
                }
                if ( type.equals( "submit" ) ) {
                    return new InputButton();
                }
                if ( type.equals( "text" ) ) {
                    return new InputText();
                }
            }
        }


        // check for floats
        if(LayoutUtil.isFloated(c,elem)) {
            //u.p("in layout factory, found a floated element. forcing display: block");
            return getCustomLayout(c,elem,"block");
        }

        // do normal layout resolution next
        Layout lyt = getCustomLayout(c, elem );
        if ( lyt != null ) {
            return lyt;
        }


        // skip whitespace only nodes
        if ( elem.getNodeType() == elem.TEXT_NODE ) {
            if ( elem.getNodeValue().trim().equals( "" ) ) {
                return new NullLayout();
            }
        }

        if ( elem.getNodeType() == elem.TEXT_NODE ) {
            return new AnonymousBoxLayout();
        }

        if ( elem.getNodeType() == elem.COMMENT_NODE ) {
            return new NullLayout();
        }
        u.p("yo! got here w/ " + elem);
        XRLog.layout( Level.WARNING, "error! returning null! type = " + elem.getNodeType() );
        XRLog.layout( Level.INFO, "error! returning null! type = " + elem.getNodeType() );
        XRLog.layout( Level.INFO, "name = " + elem.getNodeName() );

        XRLog.layout( Level.INFO, "node = " + elem );
        XRLog.layout( Level.INFO, "attribute = " + elem.ATTRIBUTE_NODE );
        XRLog.layout( Level.INFO, "cdata = " + elem.CDATA_SECTION_NODE );
        XRLog.layout( Level.INFO, "coomment = " + elem.COMMENT_NODE );
        XRLog.layout( Level.INFO, "frag = " + elem.DOCUMENT_FRAGMENT_NODE );
        XRLog.layout( Level.INFO, "document = " + elem.DOCUMENT_NODE );
        XRLog.layout( Level.INFO, "doctype = " + elem.DOCUMENT_TYPE_NODE );
        XRLog.layout( Level.INFO, "element = " + elem.ELEMENT_NODE );
        XRLog.layout( Level.INFO, "entity = " + elem.ENTITY_NODE );
        XRLog.layout( Level.INFO, "entity ref = " + elem.ENTITY_REFERENCE_NODE );
        XRLog.layout( Level.INFO, "notation = " + elem.NOTATION_NODE );
        XRLog.layout( Level.INFO, "processing inst = " + elem.PROCESSING_INSTRUCTION_NODE );
        XRLog.layout( Level.INFO, "text node = " + elem.TEXT_NODE );
        return new InlineLayout();
    }



    /**
    * Gets the renderer attribute of the LayoutFactory object
     *
     * @param node  PARAM
     * @return      The renderer value
     */
    public Renderer getRenderer(Context c, Node node ) {
        return getLayout(c, node ).getRenderer();
    }

    public Renderer getAnonymousRenderer() {
        return new AnonymousBoxLayout().getRenderer();
    }

    public boolean isBreak( Node node ) {

        if ( node instanceof Element ) {

            if ( ( (Element)node ).getNodeName().equals( "br" ) ) {

                return true;
            }

        }

        return false;
    }
    
    public boolean isLink( Node node ) {
         return node.getNodeName().equals( "a" );
    }

    public boolean isReplaced( Node node ) {
        // all images are replaced (because they have intrinsic sizes)
        if ( node.getNodeName().equals( "img" ) ) {
            return true;
        }
        if ( node.getNodeName().equals( "select" ) ) {
            return true;
        }
        if ( node.getNodeName().equals( "textarea" ) ) {
            return true;
        }
        // all input elements are replaced except for hidden forms
        if ( node.getNodeName().equals( "input" ) ) {
            Element el = (Element)node;
            // skip hidden forms. they aren't replaced
            if ( el.getAttribute( "type" ) != null
                    && el.getAttribute( "type" ).equals( "hidden" ) ) {
                return false;
            }
            return true;
        }

        return false;
    }

    public boolean isForm(Element elem) {
        if ( elem.getNodeName().equals( "form" ) ) {
            return true;
        }
        return false;
    }


    /* -------------- internal utility functions ----------------- */

    /**
    * Gets the customLayout attribute of the LayoutFactory class
    *
    * @param elem  Description of the Parameter
    * @return      The customLayout value
    */

    private Layout getCustomLayout(Context c, Node node ) {
        
        if ( element_map.containsKey( node.getNodeName() ) ) {
            return (Layout)element_map.get( node.getNodeName() );
        }
        
        if(node instanceof Element) {
            Element elem =  (Element)node;
            String display = c.css.getStringProperty( elem, "display", false);
            return getCustomLayout(c,node,display);
        }

        return null;
    }

    private Layout getCustomLayout(Context c, Node node, String display) {
        if ( display_map.containsKey( display ) ) {
            return (Layout)display_map.get( display );
        }
        return null;
    }
    
    /**
     * Initialize the standard layouts. Called by a static initializer.
     */

    private void initializeLayouts() {
        InlineLayout inline = new InlineLayout();
        BoxLayout block = new BoxLayout();
        addDisplayLayout("block",inline);
        addDisplayLayout("inline",inline);
        addDisplayLayout("list-item", inline);
        addDisplayLayout("none",inline);

        addCustomLayout( "ol", new ListLayout() );
        addCustomLayout( "ul", new ListLayout() );
        addCustomLayout( "img", new ImageLayout() );
        addCustomLayout( "table", new TableLayout2() );
        addCustomLayout( "td", new TableCellLayout() );
        addCustomLayout( "th", new TableCellLayout() );
        addCustomLayout( "br", inline );
        addCustomLayout( "hr", new NullLayout() );
        addCustomLayout( "form", inline );
        addCustomLayout( "select", new InputSelect() );
        addCustomLayout( "option", new NullLayout() );
        addCustomLayout( "textarea", new InputTextArea() );

    }

    
    


}

/*
* $Id$
*
* $Log$
* Revision 1.15  2004/11/18 23:29:38  joshy
* fixed xml bug
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.14  2004/11/18 18:49:49  joshy
* fixed the float issue.
* commented out more dead code
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.13  2004/11/18 16:45:11  joshy
* improved the float code a bit.
* now floats are automatically forced to be blocks
*
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.12  2004/11/14 16:40:58  joshy
* refactored layout factory
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.11  2004/11/03 15:17:04  joshy
* added intial support for absolute positioning
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.10  2004/11/02 20:44:56  joshy
* put in some prep work for float support
* removed some dead debugging code
* moved isBlock code to LayoutFactory
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.9  2004/10/28 13:46:32  joshy
* removed dead code
* moved code about specific elements to the layout factory (link and br)
* fixed form rendering bug
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.8  2004/10/28 01:34:24  joshy
* moved more painting code into the renderers
*
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.7  2004/10/27 13:17:01  joshy
* beginning to split out rendering code
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.6  2004/10/27 13:00:13  joshy
* removed double spacing
* Issue number:
* Obtained from:
* Submitted by:
* Reviewed by:
*
* Revision 1.5  2004/10/23 13:46:47  pdoubleya
* Re-formatted using JavaStyle tool.
* Cleaned imports to resolve wildcards except for common packages (java.io, java.util, etc).
* Added CVS log comments at bottom.
*
*
*/
