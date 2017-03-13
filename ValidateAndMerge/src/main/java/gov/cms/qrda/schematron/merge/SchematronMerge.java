/*
Copyright (c) 2016+, ESAC, Inc.
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this 
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, 
   this list of conditions and the following disclaimer in the documentation 
   and/or other materials provided with the distribution.
 * Neither the name of HL7 nor the names of its contributors may be used to 
   endorse or promote products derived from this software without specific 
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE
 */


// This file implements 
// 
package gov.cms.qrda.schematron.merge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gov.cms.qrda.schematron.validate.Validator;


public class SchematronMerge {

	static protected SAXBuilder builder = new SAXBuilder();

	public static void main(String[] namesOfPropertyFiles) {

		SchematronMerge mergerTool = new SchematronMerge();
		
		ArrayList<String> mergeInstructionFiles = MergeInstructions.getInstructionFiles("mergeInstructionsFile.txt");
		
		// Run the merge process using each merge instructions file found in mergeInstructionsFile.txt 
		for (String file : mergeInstructionFiles) {
			System.out.println("");
			System.out.println("PROCESSING MERGE INSTRUCTIONS : " + file);
			boolean continueOk = true;
			MergeInstructions mergeInstructions = new MergeInstructions();
			mergeInstructions.open(file);  // Opens the instructions and processes them in preparation for the merge.
			if (!mergeInstructions.getGlobalStop()) {  // Continue as long as there was no error during .open() that dictates a stoppage in processing.
				List<String> namesOfFilesToMerge = mergeInstructions.getSchematronsOnly();
				if (namesOfFilesToMerge == null || namesOfFilesToMerge.isEmpty()) {
					mergeInstructions.addResult(MergeInstructions.INDENT1 +"ERROR:" + " NO files were identified for merging");
					continueOk = false;
				}
				else {
					// If we are to validate each template, do that now for all of the templates. Take note of whether there were any validation discrepancies in any of the templates.
					continueOk = mergerTool.validateTemplates(mergeInstructions);
				}
	
					// If it is ok to continue, then do the merge
				if (continueOk) {
					mergeInstructions.addResult(MergeInstructions.INDENT1 +" ");
					mergeInstructions.addResult(MergeInstructions.INDENT1 +"Begin Merge Process....");
	
					// Retrieve the list of schematron template files we are to merge. (The mergeInstructions gathered this list during the "open" process)
					namesOfFilesToMerge = mergeInstructions.getSchematronsOnly();
					String mergedFileName  = mergeInstructions.getMergeFilename();
					mergeInstructions.addResult(String.format(MergeInstructions.INDENT2 +"Merging files into file \"%s\"." , mergedFileName));
					Comment comment = new Comment(mergeInstructions.getHeaderText());  // Create an xml comment for the merged file.
					Document mergedDocument = mergerTool.merge(namesOfFilesToMerge, comment); // Do the big merge
					Element theTop = mergedDocument.getRootElement();
					
					// Write the mergedDocument to the merged file specified in the instructions
					// Remember the console
					// Not sure why the write was done in this convoluted manner, but won't change it now as it seems to work.
					PrintStream console = System.out;
					// Redirect stdout to the File named by mergedFileName
					OutputStream output = null;
					try {
						output = new FileOutputStream(mergedFileName);
					} catch (FileNotFoundException e1) {
						String fileNotFoundMsg = String.format(MergeInstructions.INDENT2 +"Fatal Error: Merged File (i.e. \"%s\" can't be created. Exception message: %s" , mergedFileName, e1.getMessage());
						mergeInstructions.addResult(fileNotFoundMsg);
						if (mergeInstructions.getVerbose()) {
							System.err.println(fileNotFoundMsg);
							e1.printStackTrace();
						}
					}
					PrintStream printOut = new PrintStream(output);
					// Now actually print out the merged file
					//First stdout must be rerdirected since the underlying routines print to stdout
					System.setOut(printOut);
					//	Next Print out any Header information
					// Nextrint out that merged document 
					mergerTool.printMergedDocument(mergedDocument);
					try {
						output.close();
					}
					catch (Exception e) {}
					//  Redirect stdout back to the console
					System.setOut(console);
		
					//System.out.println(mergeMsg);
					mergeInstructions.addResult(String.format(MergeInstructions.INDENT2 +"Wrote merged Schematron file to \"%s\".\n" , mergedFileName));
					
					// Now validate the merged file using the test file specified in the instructions.
					if (!mergeInstructions.getGlobalStop() && mergeInstructions.getDoValidation()) {
						String testFilename = mergeInstructions.getFinalTestFilename();
						mergeInstructions.addResult(MergeInstructions.INDENT1 +"Testing merged file " + mergedFileName + " using: " + testFilename);
						if (testFilename == null || testFilename.isEmpty()) {
							mergeInstructions.addResult(MergeInstructions.INDENT2 + "Test Filename not specified.");
						}
						else {
							mergeInstructions.addResult(MergeInstructions.INDENT2 +"XML Validation:");
							mergeInstructions.getValidator().validateXML(mergeInstructions);
							mergeInstructions.addResult(MergeInstructions.INDENT2 +"Schematron Validation:");
							ArrayList<String> testFiles = new ArrayList<String>();
							testFiles.add(testFilename);		
							if (mergeInstructions.getValidator().validate(mergedFileName, testFiles, mergeInstructions) == 0) {
								mergeInstructions.addResult(MergeInstructions.INDENT2 + "Schematron validation of test file results as expected (or no expected error count provided).");
							};
						}
					}
				}
				mergeInstructions.addResult("**************************************** end merge report ************************************************************************");
				mergeInstructions.addResult(" ");
				
			}
			mergeInstructions.writeResults();
			System.out.println("Merge Processing Complete");

		}
	}	



	// Prints out the body of the resultant merged Document
	// note that the output goes to stdout so stdout must be redirected first (see above)  if you want to send to a file and not stdout

	private void printMergedDocument(Document mergedDoc) {

		// Actually output the merged file by redirecting stdout redirected from prinout
		try {

			new XMLOutputter(Format.getPrettyFormat()).output(mergedDoc, System.out);
		}
		catch (IOException e) {
			System.err.println(e);
		}

	}



	// Creates the files associated with the Schematrons to be merged
	// The method exits if all files are not valid - otherwise returns its an array of references to files

	private File[]  createFiles(String[] files){
		File xmlFiles[] = new File[files.length];
		for (int i = 0 ; i < files.length; i++) {
			File xFile = new File(files[i]);
			xmlFiles[i]  = xFile;
			//Check that the  two schematron files are valid names
			if(!xFile.exists() ) { 
				System.err.println (MergeInstructions.INDENT1 +"\nFatal Error: File " + files[i]+ " is invalid - schematron does not exist");
				//stem.exit(1);

			}

			if(xFile.isDirectory() ) { 
				System.err.println (MergeInstructions.INDENT1 +"\nFatal Error: File " + files[i]+ " is invalid - names a directory not a file");
				//stem.exit(1);

			}

		}
		return (xmlFiles);
	}

	// Transforms an array of File instance (for files that contained the XML code of the ".sch" file
	// to an array of Document references
	// If 
	private Document[]  createDocuments(File xmlFiles[] ){
		Document[]  docs = new Document[xmlFiles.length];

		for (int i = 0 ; i < xmlFiles.length; i++) {
			// Build the internal representations
			File file= xmlFiles[i] ;

			try {
				docs[i]= builder.build(file);

			} catch (IOException io) {
				System.err.println (MergeInstructions.INDENT1 +"Could not build file " + file );
				System.err.println(MergeInstructions.INDENT1 +io.getMessage());
				io.printStackTrace();
				//System.exit(1);
			} catch (JDOMException jdomex) {
				jdomex.printStackTrace();
				System.err.println (MergeInstructions.INDENT1 +"Could not build file " + file );
				System.err.println(MergeInstructions.INDENT1 +jdomex.getMessage());			
				//System.exit(1);
			}

		}
		return (docs);
	}

	// Transform an array of XML Document references to an array of Schmetron refereces
	private Schematron[] createSchematronRep(Document[] documents){
		Schematron[] sReps = new Schematron[documents.length];
		for(int i = 0; i < documents.length; i++){
			if (documents[i] != null) {
				sReps[i] = new Schematron(documents[i]);
				//System.out.println("Created internal schematron rep for  " + sReps[i]);
			}
		}
		return (sReps);
	}

	// Find the namespaces that are added to in the root of each schematron
	private List<Namespace> findNamespacesInScope(Schematron[] sReps){
		List<Namespace> nsl = new ArrayList<Namespace>();
		Hashtable <String,String> nsSeenSoFar = new Hashtable <String,String>();

		// For each schematron
		for (Schematron s : sReps ){
			// Find the namespaces alluded to in the root
			Element root = s.getRoot();
			List<Namespace> nsElems = root.getNamespacesInScope();

			// Record the namespaces that have not been seenso far
			for (Namespace ns : nsElems ){

				if (nsSeenSoFar.get(ns.getPrefix()) == null) {
					nsl.add(ns);
					//System.out.println("Added " + ns);
					nsSeenSoFar.put(ns.getPrefix(),ns.getURI());


				}
			}
		}

		return(nsl)	;
	}


	private List<Element> constructMergedSpaces(Schematron[] sReps){
		Hashtable<String,Element> nsSeenSoFar = new Hashtable<String,Element>();
		List<Element> mergedSpaces = new ArrayList<Element> ();

		for (Schematron s : sReps) {
			Hashtable<String,Element> ns = s.makeNamespaces();
			for (String nsKey : ns.keySet()) {
				if (nsSeenSoFar.get(nsKey) == null) {
					Element value = ns.get(nsKey);
					nsSeenSoFar.put(nsKey, value);
					mergedSpaces.add(value.clone());
				}
			}

		}
		return (mergedSpaces);

	}



	private List<Element> constructMergedErrors(Schematron[] sReps){
		Hashtable<String,Element> errorsSoFar = new Hashtable<String,Element>();
		List<Element> mergedErrors = new ArrayList<Element> ();

		for (Schematron s : sReps) {
			Hashtable<String,Element> errs = s.makeErrors();
			for (String errKey : errs.keySet()) {
				if (! errorsSoFar.contains(errKey)) {
					Element elemValue = errs.get(errKey);
					mergedErrors.add(elemValue.clone());
					errorsSoFar.put(errKey, elemValue);

				}
			}

		}
		return (mergedErrors );

	}

	private List<Element> constructMergedWarnings(Schematron[] sReps){
		Hashtable<String,Element> warningsSoFar = new Hashtable<String,Element>();
		List<Element> mergedWarnings = new ArrayList<Element> ();

		for (Schematron s : sReps) {
			Hashtable<String,Element> warnings = s.makeWarnings();
			for (String warnKey : warnings.keySet()) {
				if (! warningsSoFar.contains(warnKey)) {
					Element elemValue = warnings .get(warnKey);
					mergedWarnings.add(elemValue.clone());
					warningsSoFar.put(warnKey, elemValue);

				}
			}

		}
		return (mergedWarnings);

	}




	public Document merge(List <String> listOfiles, Comment header){
		String[] files = listOfiles.toArray(new String[0]);
		return(merge(files, header));
	}

	public Document merge(String[] files, Comment header) {


		File xmlFiles[] = createFiles(files);
		Document documents[] = createDocuments(xmlFiles);

		Schematron[] sReps = createSchematronRep(documents);
		Schematron firstSchematron = sReps[0];
		Element root = firstSchematron.getRoot();
		Element top = new Element(root.getName(), root.getNamespace());


		List<Namespace> nsl = findNamespacesInScope(sReps);
		for (Namespace n : nsl) top.addNamespaceDeclaration(n);


		List<Element> mergedSpaces = constructMergedSpaces(sReps);
		List<Element> mergedErrors = constructMergedErrors(sReps);
		List<Element> mergedWarnings = constructMergedWarnings(sReps);



		// Underlying JDom Tool requires that every item added must have only one parent
		// so you need to detach all the parents before completing
		// the assembly into the merged representation

		mergedWarnings = Schematron.detachAll(mergedWarnings);
		mergedErrors   = Schematron.detachAll(mergedErrors);
		mergedSpaces   = Schematron.detachAll(mergedSpaces);

		// Construct the error and warning representations

		Element pe1 = firstSchematron.makePhaseElement("errors" );
		Element pe2 = firstSchematron.makePhaseElement("warnings" );

		pe1.addContent(mergedErrors);
		pe2.addContent(mergedWarnings);


		// Add the Namespaces used in each of the merged file
		top.addContent(mergedSpaces);

		// Construct the new document that will hold the merge
		Document mergedDoc = new Document(top); 
		
		if (header!= null) mergedDoc.getContent().add(0,header);

		// Add the merged content
		mergedDoc.getRootElement().addContent(pe1);
		mergedDoc.getRootElement().addContent(pe2);

		// Add the patterns (w/ rules, assertions, etc)
		// from each of the files to be merged
		for (Schematron s :sReps) {
			Hashtable<String,Element> patterns = s.makePatterns();
			mergedDoc.getRootElement().addContent(patterns.values());
		}


		return (mergedDoc);
	}

	// Validate all of the schematron templates described by the merge instructions.
	// Note how many validations did not proceed as expected. If that count is non-zero, then
	// indicate whether it is ok to continue with the merge process based on the stopOnError value in the instructions.
	private boolean validateTemplates(MergeInstructions mergeInstructions) {
		boolean continueOk = true;
		int totalInconsistentSchematrons = 0;
		int noTestsCount = 0;
		if (mergeInstructions.getDoValidation()) {
			mergeInstructions.addResult(MergeInstructions.INDENT1+"");
			mergeInstructions.addResult(MergeInstructions.INDENT1+"Begin Template validations....");
			Validator validator= mergeInstructions.getValidator();
			for (SchematronTemplate schematronTemplate : mergeInstructions.getSchematrons()) {
				if (schematronTemplate.getTestFiles().isEmpty()) {
					noTestsCount++;
				}
				int failCount = validator.validate(schematronTemplate.getSchematronPath(), schematronTemplate.getTestFiles(), mergeInstructions);
				totalInconsistentSchematrons += failCount;
			}
			if (totalInconsistentSchematrons  > 0) {
					continueOk = !mergeInstructions.getStopOnError();
			}
		}
		if (mergeInstructions.getVerbose()) {
			mergeInstructions.addResult(MergeInstructions.INDENT3 + "_______________________________________________________________");
		}

		mergeInstructions.addResult(MergeInstructions.INDENT2 + "");
		mergeInstructions.addResult(MergeInstructions.INDENT1 + "Found " + totalInconsistentSchematrons + " schematron template(s) that did not process test files as expected");
		mergeInstructions.addResult(MergeInstructions.INDENT1 + noTestsCount + " schematron template(s) had no associated test files.");
		if (!continueOk) {
			mergeInstructions.addResult(MergeInstructions.INDENT1 +"Inconsistencies found in validations. Merge process aborting.");
		}
		return continueOk;
	}

		

}

