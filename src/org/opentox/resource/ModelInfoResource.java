package org.opentox.resource;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opentox.interfaces.IProvidesHttpAccess;
import org.opentox.media.OpenToxMediaType;
import org.opentox.ontology.namespaces.AbsOntClass;
import org.opentox.ontology.namespaces.OTClass;
import org.opentox.ontology.namespaces.OTProperties;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * This resource return meta information about a model such as its dependent variables
 * , its independent and predicted variables.
 * @author OpenTox - http://www.opentox.org/
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 * @version 1.3.3 (Last update: Dec 20, 2009)
 */
public class ModelInfoResource extends OTResource
        implements IProvidesHttpAccess {


    private String model_id, info;

    /**
     * Initialized the resource. Allowed HTTP operations and available MIMEs are
     * defined here.
     */
    @Override
    public void doInit() throws ResourceException {
        super.doInit();
        Collection<Method> allowedMethods = new ArrayList<Method>();
        allowedMethods.add(Method.GET);
        getAllowedMethods().addAll(allowedMethods);
        super.doInit();
        List<Variant> variants = new ArrayList<Variant>();
        variants.add(new Variant(MediaType.TEXT_URI_LIST));
        variants.add(new Variant(MediaType.TEXT_HTML));
        variants.add(new Variant(MediaType.APPLICATION_RDF_XML));
        variants.add(new Variant(MediaType.APPLICATION_RDF_TURTLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_TRIPLE));
        variants.add(new Variant(OpenToxMediaType.TEXT_N3));
        getVariants().put(Method.GET, variants);
        model_id = Reference.decode(getRequest().getAttributes().get("model_id").toString());
        info = Reference.decode(getRequest().getAttributes().get("info").toString());
    }

    /**
     * Returns a text/uri list which corresponds to the requested meta-information.
     * @param variant
     * @return StringRepresentation
     */
    @Override
    public Representation get(Variant variant) {
        Representation rep = null;
        MediaType mediatype = variant.getMediaType();
        try {
            FileInputStream in = new FileInputStream(Directories.modelRdfDir + "/" + model_id);
            OntModel jenaModel = AbsOntClass.createModel();
            jenaModel.read(in, null);
            StmtIterator stmtIt = null;
            ReferenceList uri_list = new ReferenceList();
            Property prop = null;
            if (info.equalsIgnoreCase("dependent")) {
                prop = OTProperties.dependentVariables;
            } else if (info.equalsIgnoreCase("independent")) {
                prop = OTProperties.independentVariables;
            } else if (info.equalsIgnoreCase("predicted")) {
                prop = OTProperties.predictedVariables;
            }

            stmtIt = jenaModel.listStatements(new SimpleSelector(null, prop, (Resource) null));


            if (MediaType.TEXT_URI_LIST.equals(variant.getMediaType())) {
                while (stmtIt.hasNext()) {
                  uri_list.add(new Reference(new URI(stmtIt.next().getObject().as(Resource.class).getURI())));
                }
                rep = uri_list.getTextRepresentation();
            } else if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
                while (stmtIt.hasNext()) {
                  uri_list.add(new Reference(new URI(stmtIt.next().getObject().as(Resource.class).getURI())));
                }
                rep = uri_list.getWebRepresentation();
            } else if ((mediatype.equals(MediaType.APPLICATION_RDF_XML))
                || (mediatype.equals(MediaType.APPLICATION_RDF_TURTLE))
                || (mediatype.equals(OpenToxMediaType.TEXT_TRIPLE))
                || (mediatype.equals(OpenToxMediaType.TEXT_N3))) {

                ByteArrayOutputStream outStream = null;


            jenaModel = OTProperties.createModel();


            OTClass.Feature.createOntClass(jenaModel);


            while (stmtIt.hasNext()) {
                jenaModel.createIndividual(
                        stmtIt.next().getObject().as(Resource.class).getURI(),
                        jenaModel.createOntResource(OTClass.Feature.getURI()));

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


        } catch (Exception ex) {
            Logger.getLogger(ModelInfoResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rep;
    }


}
