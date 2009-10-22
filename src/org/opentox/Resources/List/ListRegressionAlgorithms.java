package org.opentox.Resources.List;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.opentox.Resources.*;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.ReferenceList;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;


/**
 * List of all regression algorithms
 * <p>
 * URI: /algorithm/learningalgorithm/regression<br/>
 * Allowed Methods: GET<br/>
 * Status Codes: 200, 503<br/>
 * Supported Mediatypes: TEXT_URI_LIST
 * </p>
 * @author OpenTox, http://www.opentox.org
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.1 (Last Update: Aug 26, 2009)
 */
public class ListRegressionAlgorithms extends AbstractResource {

    private static final long serialVersionUID = 10012190006005501L;

       /**
     * Initialize the resource.
     * @throws ResourceException
     */
    @Override
    public void doInit() throws ResourceException{
        super.doInit();
        List<Variant> variants = new ArrayList<Variant>();
        variants.add(new Variant(MediaType.TEXT_URI_LIST));
        variants.add(new Variant(MediaType.TEXT_HTML));
        getVariants().put(Method.GET, variants);
    }

    @Override
    public Representation get(Variant variant) {


        ReferenceList list = new ReferenceList();
        String reg=baseURI+"/algorithm/learning/regression";
        
        Map<String, Set<String>> map = getAlgorithmIdsAsMap();
    
        Iterator<String> regressionAlgorithms = map.get("regression").iterator();

        
        while (regressionAlgorithms.hasNext()){
        list.add(reg+"/"+regressionAlgorithms.next());
        }

        Representation rep = null;
        if (MediaType.TEXT_URI_LIST.equals(variant.getMediaType())) {
            rep = list.getTextRepresentation();
            rep.setMediaType(MediaType.TEXT_URI_LIST);
        } else if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
            rep = list.getWebRepresentation();
            rep.setMediaType(MediaType.TEXT_HTML);
        }
        return rep;

    }
}
