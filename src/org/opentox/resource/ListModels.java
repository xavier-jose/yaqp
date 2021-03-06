package org.opentox.resource;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.opentox.database.ModelsTable;
import org.opentox.interfaces.IProvidesHttpAccess;
import org.opentox.interfaces.Jterator;
import org.opentox.media.OpenToxMediaType;
import org.opentox.ontology.namespaces.OTClass;
import org.opentox.ontology.namespaces.OTProperties;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Lists all classificantion and regression models - returns a text/uri-list
 * representation for that list.
 * @author OpenTox - http://www.opentox.org/
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.3.3 (Last update: Dec 20, 2009)
 */
public class ListModels extends OTResource
        implements IProvidesHttpAccess {

    private static final long serialVersionUID = 203859831723987321L;
    /**
     * searchAlgorithm can be any keyword related to algorithms such as
     * classification, regression, learning, svm, svc, mlr, knn. This keyword
     * is not case sensitive, so svm will give the same results with SVM ro Svm.
     */
    private static final String ALGORITHM_TYPE_QUERY = "algorithm";
    private static final String MAX_RESULTS_QUERY = "max";
    /**
     * Database Query
     */
    private String searchAlgorithm = "";
    private int max = Integer.MAX_VALUE;

    /**
     * Initializes the resource.
     * @throws ResourceException
     */
    @Override
    public void doInit() throws ResourceException {
        super.doInit();
        Collection<Variant> variants = new ArrayList<Variant>();
        variants.add(new Variant(MediaType.TEXT_URI_LIST));
        variants.add(new Variant(MediaType.TEXT_HTML));
        getVariants().put(Method.GET, variants);
        variants.add(new Variant(MediaType.APPLICATION_RDF_XML));
        variants.add(new Variant(MediaType.APPLICATION_RDF_TURTLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_TRIPLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_N3));
        Form queryForm = getReference().getQueryAsForm();
        searchAlgorithm = queryForm.getFirstValue(ALGORITHM_TYPE_QUERY);
        try {
            max = Integer.parseInt(queryForm.getFirstValue(MAX_RESULTS_QUERY));
        } catch (NumberFormatException nfe) {
        }
    }

    /**
     * Return a text/uri-list or text/html representation of the list of
     * available models.
     * @param variant
     * @return Representation of list of models.
     */
    @Override
    public Representation get(Variant variant) {

        MediaType mediatype = variant.getMediaType();
        Representation rep = null;
        ReferenceList list = new ReferenceList();


//      ------- Create the list ----------
        Jterator<String> jt;
        if (!(searchAlgorithm == null)) {
            jt = ModelsTable.INSTANCE.search(ModelsTable.COL_MODEL_URI,
                    ModelsTable.COL_ALGORITHM_ID, searchAlgorithm);
        } else {
            jt = ModelsTable.INSTANCE.iterator(ModelsTable.COL_MODEL_URI);
        }

        int model_counter = 0;
        while (jt.hasNext() && model_counter<max) {
            list.add(jt.next());
            model_counter++;
        }
        jt.close();
//      -----------------------------------



        if ((MediaType.TEXT_URI_LIST).equals(mediatype)) {

            if (list.size() > 0) {
                getResponse().setStatus(Status.SUCCESS_OK);
                rep = list.getTextRepresentation();
                rep.setMediaType(MediaType.TEXT_URI_LIST);
            } else {
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
            }

        } else if ((MediaType.TEXT_HTML).equals(mediatype)) {
            if (list.size() > 0) {
                rep = list.getWebRepresentation();
                getResponse().setStatus(Status.SUCCESS_OK);
                rep.setMediaType(MediaType.TEXT_HTML);
            } else {
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
            }
        } else if ((mediatype.equals(MediaType.APPLICATION_RDF_XML))
                || (mediatype.equals(MediaType.APPLICATION_RDF_TURTLE))
                || (mediatype.equals(OpenToxMediaType.TEXT_TRIPLE))
                || (mediatype.equals(OpenToxMediaType.TEXT_N3))) {

            ByteArrayOutputStream outStream = null;
            OntModel jenaModel = null;

            jenaModel = OTProperties.createModel();


            OTClass.Model.createOntClass(jenaModel);

            Individual model;

            for (Iterator<Reference> it = list.iterator(); it.hasNext();) {
                model = jenaModel.createIndividual(it.next().getIdentifier(),
                        jenaModel.createOntResource(OTClass.Model.getURI()));

            }

            outStream = new ByteArrayOutputStream();
            String Lang = "RDF/XML";
            if (MediaType.APPLICATION_RDF_TURTLE.equals(mediatype)) {
                Lang = "TTL";
            } else if (OpenToxMediaType.TEXT_TRIPLE.equals(mediatype)) {
                Lang = "N-TRIPLE";
            } else if (OpenToxMediaType.TEXT_N3.equals(mediatype)) {
                Lang = "N3";
            }
            jenaModel.write(outStream, Lang);
            rep = new StringRepresentation(outStream.toString());


        }
        return rep;
    }
}
