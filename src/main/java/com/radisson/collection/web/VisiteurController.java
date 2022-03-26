package com.radisson.collection.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.radisson.collection.entities.CurrentLicense;
import com.radisson.collection.entities.OldLicense;
import com.radisson.collection.entities.Visiteur;
import com.radisson.collection.repositories.CurrentLicenseRepositories;
import com.radisson.collection.repositories.OldLicenseRepositories;
import com.radisson.collection.repositories.VisiteurRepository;
import com.radisson.collection.security.ConfigClass;


@Controller

public class VisiteurController {
	@Autowired
	VisiteurRepository visiteurRepository ;
	@Autowired
	OldLicenseRepositories oldLicenseRepositories ;
	@Autowired
	CurrentLicenseRepositories currentLicenseRepositories ;


	
	Visiteur visiteurVide ;
	Visiteur v1,v2, visiteurModifAndDelete ;
	Visiteur visiteurProfile ;
	Collection<Visiteur> visiteursList = new ArrayList<>() ;
	String [] strValues = { "Cle", "Valeur" };
	private Boolean existedLicense = false ;
	private Boolean expiredLicense = false ;
	private Boolean oldLicense = false ;
	private Boolean validLicense = false ;
	private CurrentLicense myCurrentLicense ;
	private String[] listLicense = ConfigClass.listLicenseConfig ;
	private String[] configFilesValues = {"G:\\","Visitors-Database.sql","C:\\Program Files\\MariaDB 10.5\\bin\\mysqldump.exe -hlocalhost -P3307 -uRC -pAdminRC visitors-db","C:\\Visiteurs\\","08:00:00","16:00:00","","",""}  ;
	
	/* Recup System date */
	
	Calendar c = Calendar.getInstance() ;
	int year = c.get(Calendar.YEAR) ;
	int mouth = c.get(Calendar.MONTH) + 1 ;
	int day = c.get(Calendar.DAY_OF_MONTH) ;
	String today = "" + year + "-" + mouth + "-" + day ;
	
	
	@Bean
	public void start() {
		
		//servlet code
		  
		
		/* Matching System format to HTML format for search by date*/
		
		// System.out.println(today);
		int test = day / 10 ;
		int testMouth = mouth / 10 ;
		if (test == 0) {
			if (testMouth == 0) {
				today = year + "-0" + mouth + "-" + "0" + day ;
			} else {
				today = year + "-" + mouth + "-" + "0" + day ;
			}
			
		} else {
			if (testMouth == 0) {
				today = year + "-0" + mouth + "-" + day ;
			} else {
				today = year + "-" + mouth + "-" + day ;
			}
			
		}
		// System.out.println(today);
		
		
		// Test License 
		// currentLicenseRepositories.save(new CurrentLicense(null, "1234", "2021-08-31", "2021-12-31", true)) ;
		currentLicenseRepositories.findAll().forEach(License ->{
			myCurrentLicense = License ;
		}); ;
		// System.out.println(myCurrentLicense.toString());
		

		
		/* Test for Expiration date, 1 if not expired, 0 if same date and -1 if expired */
		
		if (myCurrentLicense != null ) {
			
			/* Test Existed License  */
			
			for (int i = 0; i < listLicense.length; i++) {
				if (myCurrentLicense.getLicenseKey().equals(listLicense[i])) {
						existedLicense = true ;							
				}
			}
			
			/* Test Old License */
			
			oldLicenseRepositories.findAll().forEach(oldLic -> {
				if (myCurrentLicense.getLicenseKey().equals(oldLic.getLicenseKey())) {
					oldLicense = true ;
				} else {
					oldLicense = false ;
				} 
			}) ;
			
			Date systemDateNow = new Date() ;
			int expirationTest = myCurrentLicense.getLicenseExpirationDate().compareTo(systemDateNow) ;
			// System.out.println("VC = " + expirationTest);
			
			if (expirationTest > -1 ) {
				
				if (existedLicense && !oldLicense) {
					validLicense = true ;
				}
			} else {
				myCurrentLicense.setIsExpired(true);
				OldLicense interLic =  new OldLicense() ;
				// interLic.setId(myCurrentLicense.getId());
				interLic.setLicenseKey(myCurrentLicense.getLicenseKey());
				oldLicenseRepositories.save(interLic) ;
				currentLicenseRepositories.delete(myCurrentLicense);
				validLicense = false ;
			}
		}
		
		/* Lecture des valeur des Variables Externes dans le fchier de config */ 
		 try {
		      File myObj = new File("C:\\ConfigFile.txt");
		      Scanner myReader = new Scanner(myObj);
		      int j = 0 ; 
		      while (myReader.hasNextLine()) {
		        configFilesValues[j] = myReader.nextLine();
		        // System.out.println(configFilesValues[j]);
		        j++ ;
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
//		      configFilesValues[0] = "G:\\" ; 
//		      configFilesValues[1] = "Visitors-Database.sql"; 
//		      configFilesValues[2] = "C:\\Program Files\\MariaDB 10.5\\bin\\mysqldump.exe -hlocalhost -P3307 -uRC -pAdminRC visitors-db" ;
//		      configFilesValues[3] = "C:\\Visiteurs\\" ;
//		      configFilesValues[4] = "08:00:00" ;
//		      configFilesValues[5] = "16:00:00"  ;
//		  	
		      e.printStackTrace();
		    }
		
		
	}
	
	@PostMapping(path = "/LicenseAdd")
	public String LicenseAdd(@ModelAttribute CurrentLicense formLicense,Model model) {
		CurrentLicense license = formLicense ; 

		/* Test Existed License  */
		
		for (int i = 0; i < listLicense.length; i++) {
			if (license.getLicenseKey().equals(listLicense[i])) {
					existedLicense = true ;							
			}
		}
		
		/* Test Old License */
		
		oldLicenseRepositories.findAll().forEach(oldLic -> {
			if (license.getLicenseKey().equals(oldLic.getLicenseKey())) {
				oldLicense = true ;
			} else {
				oldLicense = false ;
			} 
		}) ;
		
		/* Add Activation & Expiration Date */
		
		Date activationDate = new Date() ;
		Date expirationDate = activationDate ;
		expirationDate.setMonth(activationDate.getMonth()+3) ;
		
		if (existedLicense && !oldLicense) {
			
			license.setLicenseActivationDate(new Date()) ; 
			System.out.println(activationDate);
						
			license.setLicenseExpirationDate(expirationDate) ;
			System.out.println(expirationDate);
			
			license.setIsExpired(false) ;
			currentLicenseRepositories.save(license) ;
			
			validLicense = true ;
		}
		
		if (!validLicense) {
			model.addAttribute("LicenseModel", new CurrentLicense()) ;
			return "LicensePageAdvise" ;
		} else  {

		//--------------------------------------------------------
		System.out.println("License VALIDE !!!");
		visiteurVide = new Visiteur() ;
		
		visiteurVide.setDate(today) ;
		// visiteurVide.setHeureEnregistrement(c.getTime()) ;
		// System.out.println(visiteurVide.getHeureEnregistrement());
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "indexAfterLicenseAdd" ; 
		
		//--------------------------------------------------
		}
	}
	
	@GetMapping(path = {"", "/index"})
	public String index(Model model) {
		
//		if (!validLicense) {
//			model.addAttribute("LicenseModel", new CurrentLicense()) ;
//			return "LicensePage" ;
//			
//		} else {

		visiteurVide = new Visiteur() ;
		visiteurVide.setDate(today) ;
		// visiteurVide.setHeureEnregistrement(c.getTime()) ;
		// System.out.println(visiteurVide.getHeureEnregistrement());
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "index" ; 
		
		// } // Fin Else
	}
	
	@GetMapping(path = "/SaveDatabase")
	public String saveDatabse(Model model) throws IOException, ClassNotFoundException, SQLException {
		
		
		
		String savePath = configFilesValues[0] ;
		String fileName = configFilesValues[1] ;
		File saveFile = new File(savePath);
		if (!saveFile.exists()) {// If the directory does not exist
			saveFile.mkdirs();// create folder
		}
		if(!savePath.endsWith(File.separator)){
			savePath = savePath + File.separator;
		}

		PrintWriter printWriter = null;
		BufferedReader bufferedReader = null;
		try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(savePath + fileName), "utf8"));
			Process process = Runtime.getRuntime().exec(configFilesValues[2]);
			InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "utf8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while((line = bufferedReader.readLine())!= null){
				printWriter.println(line);
			}
			printWriter.flush();
			/*if(process.waitFor() == 0){//0 Indicates that the thread is terminated normally.
				return true;
			}*/
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (printWriter != null) {
					printWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		visiteurVide = new Visiteur() ;
		visiteurVide.setDate(today) ;
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "indexAfterSave" ;
	}
	
//	@GetMapping(path = "/logout")
//	public String logout(HttpServletRequest request) throws Exception {
//		request.logout() ;
//        return "redirect:/index" ;
//	}
	
	@PostMapping(path = "/VisiteurFormReceive")
	public String VisiteurFormReceive(@ModelAttribute Visiteur visiteurAvecDonnees, Model model,@RequestParam("file")MultipartFile file) throws IOException {
		v1 = visiteurAvecDonnees ;
		v1.setImage(Base64.getEncoder().encodeToString(file.getBytes())) ;
		  if ( StringUtils.cleanPath(v1.getFile().getOriginalFilename()).contains(".") ) { 
			  v2 = v1 ;
		  }
		  else if (v2 != null) { 
			  v1.setImage(v2.getImage()) ;
		  } 
		model.addAttribute("visiteurAvecDonnees", v1) ;
		return "showVisiteur" ;
	}

	
	@GetMapping(path = "/modif")
	public String modifVisiteur(Model model) {
		model.addAttribute("visiteurVide", v1) ;
		return "indexModif" ;
	}
	
	
	@GetMapping(path = "/save")
	public String saveVisiteur(Model model) {
		visiteurVide = new Visiteur() ;
		visiteurVide.setDate(today) ;
		v1.setHeureEnregistrement(c.getTime()) ;
		// System.out.println(v1.getHeureEnregistrement());
		visiteurRepository.save(v1) ;
		
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "index" ;
	}
	
	@GetMapping(path = "/modifHeureSortie")
	public String modifHeureSortie(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteurProfile = visiteurRepository.findById(visiteurModel.getId()).get() ;
		visiteurProfile.setId(visiteurModel.getId()) ;
		visiteurVide = new Visiteur() ;
		Date now = new Date() ;
		visiteurVide.setDate(today) ;
		visiteurProfile.setSortie(""+now) ;
		visiteurRepository.save(visiteurProfile) ;
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "index" ;
	}
	
	@GetMapping(path = "/visiteurs")
	public String visiteurs(Model model) {
//		if (!validLicense) {
//			model.addAttribute("LicenseModel", new CurrentLicense()) ;
//			return "LicensePage" ;
//			
//		} else {
		visiteursList = visiteurRepository.findByDate(today) ;
		strValues[0] = "Visiteurs d'aujourdhui" ;
		strValues[1] = "( " + today + " )" ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
		
		// } // Fin Else
	}
	
	@GetMapping(path = "/anciens")
	public String anciens(Model model) {
//		if (!validLicense) {
//			model.addAttribute("LicenseModel", new CurrentLicense()) ;
//			return "LicensePage" ;
//			
//		} else {
			
		visiteursList = visiteurRepository.findAll(Sort.by(Sort.Direction.DESC, "id")) ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "anciens" ;
		// } // Fin Else 
	}
	
	@PostMapping(path = "/visiteurProfile")
	public String VisiteurProfile(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteurProfile = visiteurRepository.findById(visiteurModel.getId()).get() ;
		visiteurModifAndDelete = visiteurProfile ;
		visiteurModifAndDelete.setId(visiteurModel.getId()) ;
		model.addAttribute("visiteurAvecDonnees", visiteurProfile) ;
		return "showVisiteurProfile" ;
	}
	
	@PostMapping(path = "/searchPiece")
	public String searchPiece(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteurProfile = visiteurRepository.findByPieceNumero(visiteurModel.getPieceNumero()) ;
		visiteurModifAndDelete = visiteurProfile ;
		model.addAttribute("visiteurAvecDonnees", visiteurProfile) ;
		return "showVisiteurProfile" ;
	}
	
	@PostMapping(path = "/searchDate")
	public String searchDate(@ModelAttribute Visiteur visiteurModel, Model model) {
		// System.out.println(visiteurModel.getDate());
		visiteursList = visiteurRepository.findByDate(visiteurModel.getDate()) ;
		strValues[0] = "Visiteurs de Date" ;
		strValues[1] = visiteurModel.getDate() ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
	}
	
	@PostMapping(path = "/searchBetween2Dates")
	public String searchBetween2Dates(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteursList = visiteurRepository.findByDateBetween(visiteurModel.getEntree(), visiteurModel.getSortie()) ;
		strValues[0] = "Visiteurs Entre les Dates" ;
		strValues[1] = visiteurModel.getEntree() + " et " + visiteurModel.getSortie() ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
	}
	
	@PostMapping(path = "/searchEntreprise")
	public String searchEntreprise(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteursList = visiteurRepository.findByEntreprise(visiteurModel.getEntreprise()) ;
		strValues[0] = "Visiteurs de L'Entreprise" ;
		strValues[1] = visiteurModel.getEntreprise() ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
	}
	
	@PostMapping(path = "/searchImmatriculation")
	public String searchImmatriculation(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteursList = visiteurRepository.findByImmatriculation(visiteurModel.getImmatriculation()) ;
		strValues[0] = "Visiteurs Avec pour Immatriculation du Vehicule" ;
		strValues[1] = visiteurModel.getImmatriculation() ;
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
	}
	
	@GetMapping(path = "/delete")
	public String delete(Model model) {
		model.addAttribute("visiteurAvecDonnees", visiteurModifAndDelete) ;
		return "showVisiteurProfileToDelete" ;
	}
	
	@GetMapping(path = "/deleteConfirm")
	public String deleteConfirm(Model model) {
		visiteurVide = new Visiteur() ;
		visiteurRepository.deleteById(visiteurModifAndDelete.getId());
		model.addAttribute("visiteurVide", visiteurVide) ;
		return "index" ;
	}
	
	@GetMapping(path = "/modifProfile")
	public String modifProfile(Model model) {
		model.addAttribute("visiteurVide", visiteurModifAndDelete) ;
		return "indexModifUpdate" ;
	}
	
	@PostMapping(path = "/visiteurSearch")
	public String VisiteurSearch(@ModelAttribute Visiteur visiteurModel, Model model) {
		visiteursList = visiteurRepository.findByNomOrPrenomContains(visiteurModel.getNom(), visiteurModel.getNom()) ;
		strValues[0] = "Visiteurs Avec Le Nom ou Prenom Contenant" ;
		strValues[1] = visiteurModel.getNom() ;
		// System.out.println(visiteurModel.getNom());
		model.addAttribute("visiteursList", visiteursList) ;
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "visiteurs" ;
	}
	
	@GetMapping(path = "/searchZone")
	public String searchZone(Model model) {
//if (validLicense == false) {
//	model.addAttribute("LicenseModel", new CurrentLicense()) ;
//			return "LicensePage" ;
//			
//		} else {
		model.addAttribute("visiteurModel", new Visiteur()) ;
		return "searchZonePage";
		// } // Fin Else 
	}
	
	@GetMapping(path = "/exportOne")
	public String exportOne(Model model) {
		    try {
		    	//Instantiation d'un objet de type document qui servira de conteneur
		    	Document doc = new Document(PageSize.A4);
                String chemin = configFilesValues[3] + visiteurModifAndDelete.getNom() + "-" + visiteurModifAndDelete.getPrenom() + ".pdf"  ;
                
                String radisson = "                                            Radisson Collection" ;
                String espace = "                          " ;
                PdfWriter.getInstance(doc, new FileOutputStream(chemin));
                //ouverture du document
                doc.open();
                //insertion de l'entete
                Paragraph p = new Paragraph("**** Informations de " + visiteurModifAndDelete.getPrenom() + " " + visiteurModifAndDelete.getNom() + " ****");
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(20f);
                p.setLeading(20f);
                doc.add(p);

                PdfPTable tab = new PdfPTable(1);
                PdfPCell c1 = new PdfPCell(new Phrase(radisson));
                tab.addCell(c1);
                doc.add(tab);
                
                // Insertion des Paragraph
                Paragraph pId = new Paragraph(espace + "ID   :   " + visiteurModifAndDelete.getId() );
                doc.add(pId);
                
                Paragraph pName = new Paragraph(espace + "Nom   :   " + visiteurModifAndDelete.getNom() );
                doc.add(pName);
                
                Paragraph pPrenom = new Paragraph(espace + "Prenom   :   " + visiteurModifAndDelete.getPrenom() );
                doc.add(pPrenom);
                
                Paragraph pPiece = new Paragraph(espace + "Piece d'Identite   :   " + visiteurModifAndDelete.getPiece_identite() );
                doc.add(pPiece);
                
                Paragraph pNumeroPiece = new Paragraph(espace + "Numero de Piece   :   " + visiteurModifAndDelete.getPieceNumero() );
                doc.add(pNumeroPiece);
                
                Paragraph pDepartement = new Paragraph(espace + "Departement   :   " + visiteurModifAndDelete.getDepartement() );
                doc.add(pDepartement);
                
                Paragraph pPersonneVisite = new Paragraph(espace + "Personne Visité   :   " + visiteurModifAndDelete.getPersonne_visite() );
                doc.add(pPersonneVisite);
                
                Paragraph pBadge = new Paragraph(espace + "Badge   :   " + visiteurModifAndDelete.getBadge() );
                doc.add(pBadge);
                
                Paragraph telParagraph = new Paragraph(espace + "Telephone   :   " + visiteurModifAndDelete.getTelephone() );
                doc.add(telParagraph);
                
                Paragraph dateParagraph = new Paragraph(espace + "Date   :   " + visiteurModifAndDelete.getDate() );
                doc.add(dateParagraph);
                
                Paragraph entreeParagraph = new Paragraph(espace + "Heure d'Entrée   :   " + visiteurModifAndDelete.getEntree() );
                doc.add(entreeParagraph);
                
                Paragraph sortieParagraph = new Paragraph(espace + "Heure de Sortie   :   " + visiteurModifAndDelete.getSortie() );
                doc.add(sortieParagraph);
                
                Paragraph statusParagraph = new Paragraph(espace + "Status   :   " + visiteurModifAndDelete.getStatus() );
                doc.add(statusParagraph);
                
                Paragraph fournisseurParagraph = new Paragraph(espace + "Fournisseur   :   " + visiteurModifAndDelete.getFournisseur() );
                doc.add(fournisseurParagraph);
                
                Paragraph entrepriseParagraph = new Paragraph(espace + "ENTREPRISE   :   " + visiteurModifAndDelete.getEntreprise() );
                doc.add(entrepriseParagraph);
                
                Paragraph typeVihiculeParagraph = new Paragraph(espace + "Type de Vehicule   :   " + visiteurModifAndDelete.getType_vehicule() );
                doc.add(typeVihiculeParagraph);
                
                Paragraph immatriculationParagraph = new Paragraph(espace + "Immatriculation   :   " + visiteurModifAndDelete.getImmatriculation() );
                doc.add(immatriculationParagraph);
                
                Paragraph agentParagraph = new Paragraph(espace + "Agents   :   " + visiteurModifAndDelete.getAgent() );
                doc.add(agentParagraph);
                
                Paragraph nomAgentParagraph = new Paragraph(espace + "nom Agent   :   " + visiteurModifAndDelete.getNom_agent() );
                doc.add(nomAgentParagraph);
                
                Paragraph espaceParagraph = new Paragraph(" "
                		+ " ");
                doc.add(espaceParagraph);
                
                PdfPTable tab2 = new PdfPTable(1);
                PdfPCell c2 = new PdfPCell(new Phrase(radisson));
                tab2.addCell(c2);
                doc.add(tab2);
                //fermetture du document
                doc.close();
 
		    } catch (Exception de) {
		      de.printStackTrace();

		  }	    
		    model.addAttribute("visiteurVide", new Visiteur()) ;
			return "index" ;
	}
	
	
	@GetMapping(path = "/exportList")
	public String exportList(Model model) {
			try {
		    	//Instantiation d'un objet de type document qui servira de conteneur
		    	Document doc = new Document(PageSize.A4);
                String chemin = configFilesValues[3] + "Liste des " + strValues[0] + "-" + strValues[1] + ".pdf"  ;
                // System.out.println(chemin);
                String radisson = "                                            Radisson Collection" ;
                String espace = "                          " ;
                PdfWriter.getInstance(doc, new FileOutputStream(chemin));
                //ouverture du document
                doc.open();
                //insertion de l'entete
                Paragraph p = new Paragraph("*** Liste des " + strValues[0] + " : " + strValues[1] +" ***");
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(20f);
                p.setLeading(20f);
                doc.add(p);

                // Boucle pour chaque element de la liste
                
                visiteursList.forEach(visiteur -> {
                	PdfPTable tab = new PdfPTable(1);
                    PdfPCell c1 = new PdfPCell(new Phrase(radisson));
                    tab.addCell(c1);
                    try {
						doc.add(tab);
					
                    
                    // Insertion des Paragraph
                    Paragraph pId = new Paragraph(espace + "ID   :   " + visiteur.getId() );
                    doc.add(pId);
                    
                    Paragraph pName = new Paragraph(espace + "Nom   :   " + visiteur.getNom() );
                    doc.add(pName);
                    
                    Paragraph pPrenom = new Paragraph(espace + "Prenom   :   " + visiteur.getPrenom() );
                    doc.add(pPrenom);
                    
                    Paragraph pPiece = new Paragraph(espace + "Piece d'Identite   :   " + visiteur.getPiece_identite() );
                    doc.add(pPiece);
                    
                    Paragraph pNumeroPiece = new Paragraph(espace + "Numero de Piece   :   " + visiteur.getPieceNumero() );
                    doc.add(pNumeroPiece);
                    
                    Paragraph pDepartement = new Paragraph(espace + "Departement   :   " + visiteur.getDepartement() );
                    doc.add(pDepartement);
                    
                    Paragraph pPersonneVisite = new Paragraph(espace + "Personne Visité   :   " + visiteur.getPersonne_visite() );
                    doc.add(pPersonneVisite);
                    
                    Paragraph pBadge = new Paragraph(espace + "Badge   :   " + visiteur.getBadge() );
                    doc.add(pBadge);
                    
                    Paragraph telParagraph = new Paragraph(espace + "Telephone   :   " + visiteur.getTelephone() );
                    doc.add(telParagraph);
                    
                    Paragraph dateParagraph = new Paragraph(espace + "Date   :   " + visiteur.getDate() );
                    doc.add(dateParagraph);
                    
                    Paragraph entreeParagraph = new Paragraph(espace + "Heure d'Entrée   :   " + visiteur.getEntree() );
                    doc.add(entreeParagraph);
                    
                    Paragraph sortieParagraph = new Paragraph(espace + "Heure de Sortie   :   " + visiteur.getSortie() );
                    doc.add(sortieParagraph);
                    
                    Paragraph statusParagraph = new Paragraph(espace + "Status   :   " + visiteur.getStatus() );
                    doc.add(statusParagraph);
                    
                    Paragraph fournisseurParagraph = new Paragraph(espace + "Fournisseur   :   " + visiteur.getFournisseur() );
                    doc.add(fournisseurParagraph);
                    
                    Paragraph entrepriseParagraph = new Paragraph(espace + "ENTREPRISE   :   " + visiteur.getEntreprise() );
                    doc.add(entrepriseParagraph);
                    
                    Paragraph typeVihiculeParagraph = new Paragraph(espace + "Type de Vehicule   :   " + visiteur.getType_vehicule() );
                    doc.add(typeVihiculeParagraph);
                    
                    Paragraph immatriculationParagraph = new Paragraph(espace + "Immatriculation   :   " + visiteur.getImmatriculation() );
                    doc.add(immatriculationParagraph);
                    
                    Paragraph agentParagraph = new Paragraph(espace + "Agents   :   " + visiteur.getAgent() );
                    doc.add(agentParagraph);
                    
                    Paragraph nomAgentParagraph = new Paragraph(espace + "nom Agent   :   " + visiteur.getNom_agent() );
                    doc.add(nomAgentParagraph);
                    
                    Paragraph espaceParagraph = new Paragraph(" "
                    		+ " ");
                    doc.add(espaceParagraph);
                    
                    } catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }) ;
                
                //fermetture du document
                doc.close();
  
		    } catch (Exception de) {
		      de.printStackTrace();
		  }	    
		
		model.addAttribute("visiteurVide", new Visiteur()) ;
		return "index" ;		    
	}
	
} // Fin Class Controleur


