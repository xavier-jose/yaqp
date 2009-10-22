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
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;


/**
 * List of all algorithms.
 * <p>
 * URI: /algorithm/<br/>
 * Allowed Methods: GET<br/>
 * Status Codes: 200, 503<br/>
 * Supported Mediatypes: TEXT_URI_LIST
 * </p>
 * @author OpenTox - http://www.opentox.org
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.1 (Last update: Oct 21, 2009)
 */
public class ListAlgorithms extends AbstractResource {

    private static final long serialVersionUID = 100121900060023101L;
    

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
    String cls=baseURI+"/algorithm/learning/classification";
    String reg=baseURI+"/algorithm/learning/regression";
    String feat=baseURI+"/algorithm/preprocessing/featureselection/";

    Map<String, Set<String>> map = getAlgorithmIdsAsMap();
    Iterator<String> classificationAlgorithms = map.get("classification").iterator();
    Iterator<String> regressionAlgorithms = map.get("regression").iterator();
    Iterator<String> featureSelectionAlgorithms = map.get("featureselection").iterator();    

    while (classificationAlgorithms.hasNext()){
        list.add(cls+"/"+classificationAlgorithms.next());
    }

    while (regressionAlgorithms.hasNext()){
        list.add(reg+"/"+regressionAlgorithms.next());
    }

    while(featureSelectionAlgorithms.hasNext()){
        list.add(feat+featureSelectionAlgorithms.next());
    }

    
    Representation rep = null;

    if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)){
        rep = list.getTextRepresentation();
        rep.setMediaType(MediaType.TEXT_URI_LIST);
    }else if (variant.getMediaType().equals(MediaType.TEXT_HTML)){
        rep = list.getWebRepresentation();
        rep.setMediaType(MediaType.TEXT_HTML);
    }
    

    getResponse().setStatus(Status.SUCCESS_OK);

    return rep;


    }
}
