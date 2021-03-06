package gov.cms.qrda.validator.web.controller;
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
POSSIBILITY OF SUCH DAMAGE.

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.cms.qrda.validator.model.FileSpec;
import gov.cms.qrda.validator.model.SchematronCategory;
import gov.cms.qrda.validator.web.form.UploadFileForm;
import gov.cms.qrda.validator.web.service.CommonUtilsImpl;
import gov.cms.qrda.validator.web.service.SchematronCategoryService;
import gov.cms.qrda.validator.xml.QRDA_URIResolver;

/**
 * Handles requests to/from the Test Files Inventory page
 * @author Dan Donahue, ESAC Inc.
 *
 */
@Controller
@RequestMapping(value = "/testFiles")
public class TestFileController extends CommonUtilsImpl{
	
	private static final Logger logger = LoggerFactory.getLogger(TestFileController.class);

	@Autowired
	public SchematronCategoryService categoryService;

	/**
	 * Default mapping.  Gathers the file specs from each sub folder in the Test Files folder of the QRDA_HOME/qrda file space on the server.
	 * Also creates a form enabling users to upload files to those same folders.
	 * 
	 * @param locale, the current Locale
	 * @param model, a org.springframework.ui.Model object
	 * @param session, the current HttpSession
	 * @return  test files inventory jsp page
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String manageTestFiles(Locale locale, Model model, HttpSession session) {
		logger.info("TEST FILE INVENTORY");

		// Load the directory specs from disc to be sure we are up to date on categories.
		List<SchematronCategory> dirSpecs =  categoryService.loadOrBuild();
		model.addAttribute(SchematronCategoryService.SCHEMATRON_CATEGORY, dirSpecs);

		// For each type of schematron we support (mapping one-to-one to the list of directory specs), gather the 
		// proper files from each subdirectory and set the directory spec's file list to that list.
		for (SchematronCategory dir : dirSpecs) {
			if (dir.isActive()) {
				String subDir = dir.getName();
				logger.debug("Getting files for subdir " + subDir);

				ArrayList<FileSpec> files = fileService.getExtRepositoryFiles(QRDA_URIResolver.REPOSITORY_TESTFILES,subDir, null);
				dir.setFiles(files);
				UploadFileForm uff = new UploadFileForm();
				uff.setSubDir(subDir);
				model.addAttribute("upload"+subDir, uff);
			}
		}
		
		return "testFileInventory";
	}

	/**
	 * This method is called by AJAX from the test files inventory page. It reads the contents of the given file
	 * (as specified by the filename and the folder name - type - of the folder where the particular test file file resides)
	 * into a string and puts that string into the response of this call.
	 * 
	 * @param locale, the current Locale
	 * @param model, a org.springframework.ui.Model object
	 * @param type, the subidrectory under the test files directory to look in for the file
	 * @param filename, the name of the file to search
	 * @return return the response string
	 */

	@RequestMapping(value = "getXML", method = RequestMethod.GET)
	public @ResponseBody String gettext (Locale locale, Model model,  @RequestParam("type") String type, @RequestParam("file") String filename) {
		logger.info("Testfile Inventory: Called via ajax getXML");
		String response = fileService.readExtFileUnparsed(QRDA_URIResolver.REPOSITORY_TESTFILES, type, filename);
		logger.debug(response);
		return response;
	}

	/**
	 * Removes the given file (as specified by the filename and the folder name - type - of the folder where the particular test file file resides)
	 * from the system.
	 * 
	 * @param filename the name of the file to remove
	 * @param subdir the subdirectory under the repository files directory where the file is located
	 * @param locale, the current Locale
	 * @param model, a org.springframework.ui.Model object
	 * @param request, the current HttpRequest object
	 * @return return to the testFiles page in the UI
	 */

	@RequestMapping(value="/remove/{filename}&{subdir}", method = RequestMethod.GET)
    public String processRemoveFile(@PathVariable String filename,  @PathVariable String subdir, Locale locale, Model model, HttpServletRequest request) {
		
		logger.info("Delete file: " + filename + " in " + QRDA_URIResolver.REPOSITORY_TESTFILES + "/" + subdir);
		fileService.deleteFile(filename,  QRDA_URIResolver.REPOSITORY_TESTFILES, subdir);
		return "redirect:/testFiles";
	}

	/**
	 * Uploads a file into the sub directory under the test file file repository.
	 * 
	 * @param uploadFileForm, the UploadFileForm populated in the UI
	 * @param result, the binding result populated when user submits the form
	 * @param locale, the current Locale
	 * @param model, a org.springframework.ui.Model object
	 * @param request, the current HttpRequest object
	 * @return return to the testFiles page in the UI
	 */

	@RequestMapping(value="/upload", method = RequestMethod.POST)
    public String processUploadFileForm(UploadFileForm uploadFileForm, BindingResult result, Locale locale, Model model, HttpServletRequest request) {
		
        // Redirect to the page with any errors
        if (result.hasErrors()) {
        	return manageTestFiles(locale, model, request.getSession());
        }
 		fileService.uploadFile(uploadFileForm.getPath(), QRDA_URIResolver.REPOSITORY_TESTFILES, uploadFileForm.getSubDir(), uploadFileForm.getName());
		return "redirect:/testFiles";
	}

}
