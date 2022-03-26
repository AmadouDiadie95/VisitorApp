package com.radisson.collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VisitorAppApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(VisitorAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		String[] configFilesValues = {"G:\\","Visitors-Database.sql","C:\\Program Files\\MariaDB 10.5\\bin\\mysqldump.exe -hlocalhost -P3307 -uRC -pAdminRC visitors-db","C:\\Visiteurs\\","08:00:00","16:00:00","","",""}  ;
		/* Lecture des valeur des Variables Externes dans le fchier de config */ 
		 try {
		      File myObj = new File("C:\\ConfigFile.txt");
		      Scanner myReader = new Scanner(myObj);
		      int j = 0 ; 
		      System.out.println("/---------------------------------------  READ ME	------------------------------------------------------/\r\n"
			      		+ "Le Fichier de config : ConfigFile.txt contient les valeurs suivantes respectivement \r\n"
			      		+ "dans l'ordre : \r\n"
			      		+ "    1 - The savePath for Database save ( just the path without fileName) like : G:\\\\        ( WITH THE \\\\ )\r\n"
			      		+ "	2 - The fileName like : Visitors-Database.sql ;\r\n"
			      		+ "    3 - The full path for command MySQLDump to save Database, like : C:\\\\Program Files\\\\MariaDB 10.5\\\\bin\\\\mysqldump.exe -hlocalhost -P3307 -uroot visitors-db\r\n"
			      		+ "    4- The Path for Expoted PDF and Information ( juste the pat without fileName ) like : C:\\\\Visiteurs\\\\\r\n"
			      		+ "    5- First Time Attempting to Automaticaly save Database, like : 08:00:00       for 8h00\r\n"
			      		+ "    6- Second Time Attempting to Automaticaly save Database, like : 16:00:00       for 16h00");
			      System.out.println("/--------------------------------------------------------------------------------------------------------/");
			      System.out.println("Les Données de ConfigFile.txt sont donc : ");
			      System.out.println("--------------------------------------  CONFIG FILE  --------------------------------------");
		      while (myReader.hasNextLine()) {
		        configFilesValues[j] = myReader.nextLine();
		        System.out.println(configFilesValues[j]);
		        j++ ;
		      }
		      System.out.println("/---------------------------------------------------------------------------------------------/");
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("File Not Found !!!, Searching in C:\\ConfigFile.txt");
		      e.printStackTrace();
		    }

		Calendar c1 = Calendar.getInstance() ;
		System.out.println("Le Back-Up aura lieu à " + configFilesValues[4] + " et " + configFilesValues[5] + " dans G:\\Visitors-Database");
		System.out.println("Temps Actuel : " + c1.getTime());
		
		Timer t = new Timer();  
		TimerTask tt = new TimerTask() {  
		    @Override  
		    public void run() {  
		    	Calendar c = Calendar.getInstance() ;
				// System.out.println(c.getTime());
				String heure = "" + c.getTime() ;
		    	if (heure.contains(configFilesValues[4]) || heure.contains(configFilesValues[5])) {
		    		
		    		try {
						saveDatabase() ;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Le Back-Up a été efectué avec Success à :" + c.getTime() + " dans " + configFilesValues[0] + configFilesValues[1] );
				} else {
//					System.out.println("Le Back-Up aura lieu à 8h00 et 16h00 dans G:\\Visitors-Database");
//					System.out.println("Temps Actuel : " + c.getTime());
				} 
		          
		    }

			private void saveDatabase() throws Exception {
				// TODO Auto-generated method stub
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
			};  
		};  
		t.schedule(tt, new Date(),1000); ;  
		
		

		 
	}  // Fin fonction run 
	
	
	
	
	
	

	
		
} // fin Classe 
	
