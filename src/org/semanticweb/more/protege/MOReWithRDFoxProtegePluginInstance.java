package org.semanticweb.more.protege;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.semanticweb.more.reasoner.MOReReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class MOReWithRDFoxProtegePluginInstance extends 
		MOReProtegePluginInstance implements ProtegePluginInstance {

	@Override
	public OWLReasonerConfiguration getConfiguration(ReasonerProgressMonitor monitor) {
    	return new MOReReasonerConfiguration(true);
    }
}
