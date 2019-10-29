package org.semanticweb.more.reasoner;

//import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.ProtegeReasonerFactory;
import org.semanticweb.more.util.Logger_MORe;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
//import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

//we access through the protege factory
//import com.clarkparsia.protege.plugin.pellet.PelletReasonerFactory;
//import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
//import org.semanticweb.owlapi.reasoner.BufferingMode;


public class OWL2ReasonerManager {
	
	OWLReasonerFactory factory; 
	OWLReasonerConfiguration configuration;
	OWLReasoner r = null;
	
	public OWL2ReasonerManager(OWLReasonerFactory factory, OWLReasonerConfiguration configuration){
		this.factory = factory;
		this.configuration = configuration;
	}
	
	public OWLReasoner getOWL2ReasonerInstance(OWLOntology ontology){
		if (factory == null)
			r = new ProtegeReasonerFactory().getReasonerFactory().createReasoner(ontology);
		else if (configuration != null)
			r = factory.createReasoner(ontology);
		else 
			r = factory.createReasoner(ontology, configuration);
		Logger_MORe.logTrace("created instance of OWL 2 reasoner: " + r.getReasonerName());
		return r;
	}
	
/*
	public OWLReasoner getOWL2ReasonerInstance(OWLOntology ontology){
		if (factory == null) {
			if (configuration == null) {
				configuration = new SimpleConfiguration();
			}
			r = new FaCTPlusPlusReasoner(ontology, configuration, bufferingMode);
		}
		else if (configuration != null) {
			r = factory.createReasoner(ontology, configuration);
		}
		else {
			r = factory.createReasoner(ontology);
		}
		Logger_MORe.logTrace("created instance of OWL 2 reasoner: " + r.getReasonerName());
		return r;
	}
*/

}
