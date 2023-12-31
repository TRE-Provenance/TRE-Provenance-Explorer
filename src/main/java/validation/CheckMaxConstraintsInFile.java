package validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.apache.jena.rdf.model.Model;

import Utils.SPARQLUtils;
import Utils.ValidationUtils;
import semantic.parser.Constants;
import semantic.parser.Entity;
import semantic.parser.Variable;

public class CheckMaxConstraintsInFile implements ValidationRuleInterface{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Variables Violating Max Constraint";
	}

	@Override
	public List getTargetTypes() {
		ArrayList <String> list = new ArrayList <String> ();			
		list.add("https://w3id.org/shp#DataSet");
		return list;
	}

	@Override
	public List getViolations(String[] args, Model model) {
        String fileIRI = args [0];
		
		ArrayList <HashMap<String, String>> list = new ArrayList <HashMap<String, String>> ();
		
		String query = Constants.PREFIXES + " SELECT DISTINCT ?variableL ?constraintMax  WHERE {<"+fileIRI+">  prov:wasDerivedFrom* ?database; schema:exifData ?item. ?database a shp:Database. ?linkagePlan a shp:DataLinkagePlan; schema:exifData ?dataSource. ?dataSource shp:database ?database; shp:constraint ?constriant. ?constriant shp:targetFeature ?variable; shp:maxValue ?constraintMax.  ?item shp:targetFeature ?variable; shp:maxValue ?actualMax. ?variable rdfs:label ?variableL FILTER (?constraintMax < ?actualMax)}";

		// Execute SPARQL query
		list = SPARQLUtils.executeSparqlQuery(model, query);    	
		/*
		ArrayList <String> violations = new ArrayList <String> ();
		
		for (int i = 0; i < list.size();i++) {
			
			violations.add(list.get(i).get("variableL")+" (max"+list.get(i).get("constraintMax")+")");
		}
*/	
ArrayList <Variable> violations = new ArrayList <Variable> ();
	
		
		
		for (int i = 0; i < list.size();i++) {
			
			Variable variable = new Variable (list.get(i).get("variable")); 
			variable.setEntityL(list.get(i).get("variableL")+" (max"+list.get(i).get("constraintMax")+")");
			
			violations.add(variable);
		}
		return violations; 
	}
	

	@Override
	public JPanel getSimpleResult(String[] args, Model model) {
		String variables = "";
		
		
		
		ArrayList<Variable> variablesList =  (ArrayList<Variable>) new CheckMaxConstraintsInFile ().getViolations(args, model);
ArrayList <Entity> entities= new ArrayList <Entity> (); 
		
		if (variablesList.size()>0) {
	    	
	    	
	    	for (int i =0; i <variablesList.size();i++ ) {
	    		entities.add(variablesList.get(i));
	    	}
	    	
	    }
	    
	    
		
	    return ValidationUtils.entityResult(getName()+ " ("+variablesList.size()+")", entities);
		
	   
	}
}

