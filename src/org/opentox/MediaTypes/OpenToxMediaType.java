/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opentox.MediaTypes;

import org.restlet.data.MediaType;

/**
 * This class owns the representations of the media types adopted by opentox
 * which are not standard RFC media types.
 * @author OpenTox - http://www.opentox.org
 * @author Sopasakis Pantelis
 * @author Sarimveis Harry
 */
public class OpenToxMediaType {
    
    private static final long serialVersionUID = 8282646955343977L;
    
    private OpenToxMediaType(){

    }

    public static final MediaType CHEMICAL_MDLSDF = new MediaType("chemical/x-mdl-sdfile");
    public static final MediaType CHEMICAL_MDLMOL= new MediaType("chemical/x-mdl-molfile");
    public static final MediaType CHEMICAL_CML = new MediaType("chemical/x-cml");
    public static final MediaType CHEMICAL_SMILES = new MediaType("chemical/x-daylight-smiles");
    public static final MediaType TEXT_YAML = new MediaType("text/x-yaml");
    public static final MediaType TEXT_ARFF = new MediaType("text/x-arff");
    public static final MediaType APPLICATION_YAML = new MediaType("application/x-yaml");




}