import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.*;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dataproc.Dataproc;
import com.google.api.services.dataproc.model.HadoopJob;
import com.google.api.services.dataproc.model.Job;
import com.google.api.services.dataproc.model.JobPlacement;
import com.google.api.services.dataproc.model.JobReference;
import com.google.api.services.dataproc.model.JobStatus;
import com.google.api.services.dataproc.model.SubmitJobRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GUI extends JPanel implements ActionListener{

	
	
	
	
	String selected;
	JPanel mp;
	String term;
	HashMap<String, String[]> result;
	boolean computed = false;

	public void makeGui(Container guif) {
		gui1();
		guif.add(mp);
	}

	public void gui1() {

		selected = "";
		
		JTextField input;
		JButton restart;
		JButton submit2;
		JButton submit;
		JPanel cards;

		mp = new JPanel();
		mp.setLayout(new GridLayout(3,1));

		JPanel fileCard = new JPanel();

		JLabel pLabel = new JLabel("Files:");
		//JComboBox files = new JComboBox(fileOptions);
		fileCard.add(pLabel);
		//filePanel.add(files);
		
		final DefaultComboBoxModel<String> files = new DefaultComboBoxModel<String>();
		
		files.addElement("All's Well That Ends Well");
		files.addElement("As You Like It");
		files.addElement("Comedy of Errors");
		files.addElement("Cymbeline");
		files.addElement("Love labours Lost");
		files.addElement("Measure Fore Measure");
		files.addElement("Merchant of Venice");
		files.addElement("Merry Wives of Windsor");
		files.addElement("Midsummer's Night's Dream");
		files.addElement("Much Ado About Nothing");
		files.addElement("Pericles Prince of Tyre");
		files.addElement("Taming of the Shrew");
		files.addElement("Tempest");
		files.addElement("Troilus and Cressida");
		files.addElement("Twelfth Night");
		files.addElement("Two Gentlemen of Verona");
		files.addElement("Winter's Tale");
		files.addElement("1 King Henry IV");
		files.addElement("1 King Henry VI");
		files.addElement("2 King Henry IV");
		files.addElement("2 King Henry VI");
		files.addElement("3 King Henry VI");
		files.addElement("King Henry V");
		files.addElement("King Henry VIII");
		files.addElement("King John");
		files.addElement("King Richard II");
		files.addElement("King Richard III");
		files.addElement("Les Miserables");
		files.addElement("Notre Dame De Paris");
		files.addElement("Lover's Complaint");
		files.addElement("Rape of Lucrece");
		files.addElement("Sonnets");
		files.addElement("Various");
		files.addElement("Venus and Adonis");
		files.addElement("Anna Karenhina");
		files.addElement("War and Peace");
		files.addElement("Antony and Cleopatra");
		files.addElement("Coriolanus");
		files.addElement("Hamlet");
		files.addElement("Julius Caesar");
		files.addElement("King Lear");
		files.addElement("Macbeth");
		files.addElement("Othello");
		files.addElement("Romeo and Juliet");
		files.addElement("Timon of Athens");
		files.addElement("Titusandronicus");

		JRadioButton r1 = new JRadioButton("All Files");
		JRadioButton r2 = new JRadioButton("Single File");
		JComboBox<String> b1 = new JComboBox<String>(files);
		ButtonGroup g1 = new ButtonGroup();
		g1.add(r1);
		g1.add(r2);

		r1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				int state = event.getStateChange();
				if (state == ItemEvent.SELECTED) {
					selected = "All Files";
					b1.setEnabled(false);
				} else if (state == ItemEvent.DESELECTED) {
					selected = "";
				}
			}
		});

		r2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				int state = event.getStateChange();
				if (state == ItemEvent.SELECTED) {
					selected = "Single File";
					b1.setEnabled(true);
				} else if (state == ItemEvent.DESELECTED) {
					selected = "";
				}
			}
		});


		fileCard.add(r1);
		fileCard.add(r2);
		fileCard.add(b1);


		JPanel searchCard = new JPanel(new FlowLayout());
		JLabel inputL = new JLabel("Please Enter Term to Search");
		input = new JTextField(20);
		searchCard.add(inputL);
		searchCard.add(input);

		JPanel resultsCard = new JPanel(new GridLayout(3,1));
		final JLabel message = new JLabel("Files Selected: ");
		final JLabel searched = new JLabel("Term: ");
		JLabel results = new JLabel("Results go here");
		resultsCard.add(message);
		resultsCard.add(searched);
		resultsCard.add(results);



		cards = new JPanel(new CardLayout());
		cards.add(fileCard, "File");
		cards.add(searchCard, "Search");
		cards.add(resultsCard, "Results");

		submit = new JButton("submit");
		
		submit2 = new JButton("submit");
		submit2.setVisible(false);

		restart = new JButton("Search again");
		restart.setVisible(false);

		mp.add(cards);
		mp.add(submit);
		mp.add(submit2);
		mp.add(restart);

		submit.setActionCommand("Input");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!computed) {
					result = calcInvertedIndexes();
				}
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Search");
				submit.setVisible(false);
				submit2.setVisible(true);
			}
		});
		

		submit2.setActionCommand("Search");
		submit2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				term = input.getText();
				if(r1.isSelected()) {
					message.setText("All Files Selected");
					selected = "All";
				}
				else {
					message.setText("Files Selected: "+b1.getSelectedItem());
					selected = (String) b1.getSelectedItem();
				}
				searched.setText("Term: " + term);
				
				searchForTerm(results, term, selected);
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "Results");
				submit2.setVisible(false);
				restart.setVisible(true);
				
			}
		});
		
		restart.setActionCommand("Restart");
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout)(cards.getLayout());
				cl.show(cards, "File");
				submit.setVisible(true);
				restart.setVisible(false);
			}
		});
	}

	public static void showGui() {
		JFrame guif = new JFrame();
		guif.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guif.setSize(500, 300);

		GUI g = new GUI();
		g.makeGui(guif.getContentPane());

		guif.pack();
		guif.setVisible(true);
	}
	
	
	public void searchForTerm(JLabel label, String term, String file) {
		Job response;
		String curJobId = "job"+UUID.randomUUID().toString();
		
		//hashmap to look up DocId of file
		HashMap<String, String> convertedNames = new HashMap<String, String>();
		convertedNames.put("All's Well That Ends Well", "comedies/allswellthatendswell");
		convertedNames.put("As You Like It", "comedies/asyoulikeit");
		convertedNames.put("Comedy of Errors", "comedies/comedyoferrors");
		convertedNames.put("Cymbeline", "comedies/cymbeline");
		convertedNames.put("Love labours Lost", "comedies/lovelabourslost");
		convertedNames.put("Measure Fore Measure", "comedies/measureforemeasure");
		convertedNames.put("Merchant of Venice", "comedies/merchantofvenice");
		convertedNames.put("Merry Wives of Windsor", "comedies/merrywivesofwindsor");
		convertedNames.put("Midsummer's Night's Dream", "comedies/midsummersnightsdream");
		convertedNames.put("Much Ado About Nothing", "comedies/muchadoaboutnothing");
		convertedNames.put("Pericles Prince of Tyre", "comedies/periclesprinceoftyre");
		convertedNames.put("Taming of the Shrew", "comedies/tamingoftheshrew");
		convertedNames.put("Tempest", "comedies/tempest");
		convertedNames.put("Troilus and Cressida", "comedies/troliusandcressida");
		convertedNames.put("Twelfth Night", "comedies/twelthnight");
		convertedNames.put("Two Gentlemen of Verona", "comedies/twogentlemenofverona");
		convertedNames.put("Winter's Tale", "comedies/winterstale");
		convertedNames.put("1 King Henry IV", "histories/1kinghenryiv");
		convertedNames.put("1 King Henry VI", "histories/1kinghenryvi");
		convertedNames.put("2 King Henry IV", "histories/2kinghenryiv");
		convertedNames.put("3 King Henry VI", "histories/3kinghenryvi");
		convertedNames.put("King Henry V", "histories/kinghenryv");
		convertedNames.put("King Henry VIII", "histories/kinghenryviii");
		convertedNames.put("King John", "histories/kingjohn");
		convertedNames.put("King Richard II", "histories/kingrichardii");
		convertedNames.put("King Richard III", "histories/kingrichardiii");
		convertedNames.put("Les Miserables", "Hugo/Miserables");
		convertedNames.put("Notre Dame De Paris", "Hugo/NotreDame_De_Paris");
		convertedNames.put("Lover's Complaint", "poetry/loverscomplaint");
		convertedNames.put("Rape of Lucrece", "poetry/rapeoflucrece");
		convertedNames.put("Sonnets", "poetry/sonnets");
		convertedNames.put("Various", "poetry/various");
		convertedNames.put("Venus and Adonis", "poetry/venusandadonis");
		convertedNames.put("Anna Karenhina", "Tolstoy/anna_karenhina");
		convertedNames.put("War and Peace", "Tolstoy/war_and_peace");
		convertedNames.put("Antony and Cleopatra", "tragedies/antonyandcleopatra");
		convertedNames.put("Coriolanus", "tragedies/coriolanus");
		convertedNames.put("Hamlet", "tragedies/hamlet");
		convertedNames.put("Julius Caesar", "tragedies/juliuscaesar");
		convertedNames.put("King Lear", "tragedies/kinglear");
		convertedNames.put("Macbeth", "tragedies/macbeth");
		convertedNames.put("Othello", "tragedies/othello");
		convertedNames.put("Romeo and Juliet", "tragedies/romeoandjuliet");
		convertedNames.put("Timon of Athens", "tragedies/timonofathens");
		convertedNames.put("Titusandronicus", "tragedies/titusandronicus");
		
		
		if(!selected.contentEquals("All")) {
			selected = convertedNames.get(selected);
		}
		
		try {
			InputStream cred = new ByteArrayInputStream(new String("{\r\n" + 
					"  \"type\": \"service_account\",\r\n" + 
					"  \"project_id\": \"cs-1660-project-arie-dash\",\r\n" + 
					"  \"private_key_id\": \"6be0869ae7428b8877bedb35b95aa365c1cfec45\",\r\n" + 
					"  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCTUQEcyXegXgKU\\nhoCwKxaQFPPsrg9/D+1iNw3R/j9b/Je85ih/zBBEkH6bRWZ/wfo2s+P6MHpobwnz\\nYqd9ZmdJZEZ01i+FuAmrBLeWfqA8MH/xAE7gzBtlbHwlcO0w1CJHJ7X7GtHoMEJi\\n1/TD8q2/ulex+uSM9j9OpO57aPCNjAccCI6gDp1Ie/2ffL7jx2Se2P6GRaxMNNGA\\nnLpBlo9BRYMSjIC5dbMp4n+h5lxT/rK+PkuGPp/5FkMHowR7AcMN69aAraN9aLcT\\nxn0AcuWo7xriCAE0ukWZ64EKme7Is9O92k6UpOXIaCHGzIxlaHxXEKlXm640Wvj8\\ne4iYQUqhAgMBAAECggEABZrNe2hFVTw6ZibzXrB2+mqXqKurIoMLPaM5UwHUrIXg\\nyoKprAoKHOev722do5LwV3Qy4l4FjDNcSi6Yz3YtzA6k+RU8h957fK6QYwkEhOG1\\n/mfr+FS/npVY6E5CcesCNlmSDICF16r9XZgYg3t4RYz8AOUaHKxSNoZjHd4dV887\\nH4AvS65SvKvv5DvB/Q1Xu9yoYoi+TJE4c4E8iMTpys+/N2QtUs4jb9a8ZCi0kuA8\\nCvhOFYDdkFZa7C6DZZN4iR1sIMSfw5m6Y8BMnn4iCAg8z3KJTM6X/14mJmcbpWOP\\nw2Lz6ByPP1MtdYLKd23sCw1nNXHb5MMVRn2bI3Vy4QKBgQDDzM2P0X7opBiMyXED\\nXGwBARGNWzFq6gkMHDdw/NKQuSJq96q7V2meYQU+0HcB71LX3efAG0FyfwkbEZfF\\nMgNTg5V2+ZmPmCZEwuXvdo2xTwS3DtRpR4RugJzTcHGi1/KDk6u7lOrMf3cQKzyT\\nQNMW5SMzSr6pOrnrmHjKigshcQKBgQDAnBzyN7lPICrcqa0Y8PxCfxQTAtgbT8PW\\n3+da2cLR76o/CsKwz9iXijQngeSHhalcVnB9S2WWl5c8d1COPl466YGCTRTRnElD\\nLcG54Dd30MT4JDm9wG425VcYbVph/VY5LzQZ6pEvgzCrr6+mXdj4W8tfc8xJozHH\\nEMgQnCwkMQKBgQCZgRMX0bKhSSVGGxfS/r5d+yx7micgHuOA7w44Mr4SYFKvcgQU\\nY++WD5JBMMZcafiU7JAoeDXTQe0tn5lRLGiXLO4dHIpbnXlDJ1mFZ3imrcCQk7vL\\n3hnhWEubKOiuNocWyMs2gQWj1brOnlcbSxMzO+lg6RSZMEVag2sPvzICwQKBgESb\\nVvQwRSGaI6pWBgJMVI6bFVtwN+oCj4+KUa5awrpk1A+AlbjFUDEqWUl5Sx26zojF\\ngWwf3x+0/3naRidkr8vsQOVY74F6Wd5D/i1nYFqdUovQ24Bretay+kMJMJiW1I+p\\n8LUKiIyT/lIpUjfAVfihOd/WL35LUv/FSn4gItkBAoGARqwRyoUIB+Rw3AnBTwi5\\nrA15T09ChuEcQLeA/sIYsW6l1msNONNRWVgxpia/k9250OK9A1JQZFSUNIWTG9z+\\nToV4F9uscd9e3gfc05Ul+Rgtnpbsq5ssbu/hIYb9eh/OojRH9uRCtVFwzRQeT7Tj\\nFeice13oIeDormj9cqME7GE=\\n-----END PRIVATE KEY-----\\n\",\r\n" + 
					"  \"client_email\": \"cs1660project@cs-1660-project-arie-dash.iam.gserviceaccount.com\",\r\n" + 
					"  \"client_id\": \"109336766852354944731\",\r\n" + 
					"  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\r\n" + 
					"  \"token_uri\": \"https://oauth2.googleapis.com/token\",\r\n" + 
					"  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\r\n" + 
					"  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/cs1660project%40cs-1660-project-arie-dash.iam.gserviceaccount.com\"\r\n" + 
					"}\r\n" + 
					"").getBytes("UTF-8"));
			
			GoogleCredentials credentials = GoogleCredentials.fromStream(cred)
					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			credentials.refreshIfExpired();
			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
			Dataproc dataproc = new Dataproc.Builder(new NetHttpTransport(),new JacksonFactory(), requestInitializer).setApplicationName("apd project").build();

			try {
				dataproc.projects().regions().jobs().submit(
						"cs-1660-project-arie-dash", "us-west1", new SubmitJobRequest()
						.setJob(new Job()
								.setReference(new JobReference()
										.setJobId(curJobId))
								.setPlacement(new JobPlacement()
										.setClusterName("cluster-apd"))
								.setHadoopJob(new HadoopJob()
										.setMainClass("SearchTerm")
										.setJarFileUris(ImmutableList.of("gs://dataproc-staging-us-west1-109069267149-nric6xzk/JAR/invertedindex.jar"))
										.setArgs(ImmutableList.of(
												term, selected, "gs://dataproc-staging-us-west1-109069267149-nric6xzk/output", "gs://dataproc-staging-us-west1-109069267149-nric6xzk/searchOutput")))))
				.execute();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				response = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute();
				System.out.println("Job submitted");
			}
			catch(IOException e2){
				e2.printStackTrace();
			}
			try {
				JobStatus stat = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute().getStatus();
				while(stat.getState().contentEquals("RUNNING")) {
					Thread.sleep(1000);
					stat = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute().getStatus();
				}
				if(stat.getState().equals("ERROR")) {
					System.out.println("Job Failed");
				}
				else {
					System.out.println("Job Completed");
					//get results from cluster and set passed JLabel equal to formatted results
				}
				
			}
			catch(IOException | InterruptedException e3) {
				e3.printStackTrace();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public HashMap<String, String[]> calcInvertedIndexes() {
		Job response;
		String curJobId = "job"+UUID.randomUUID().toString();
		
		
		
		
		try {
			InputStream cred = new ByteArrayInputStream(new String("{\r\n" + 
					"  \"type\": \"service_account\",\r\n" + 
					"  \"project_id\": \"cs-1660-project-arie-dash\",\r\n" + 
					"  \"private_key_id\": \"6be0869ae7428b8877bedb35b95aa365c1cfec45\",\r\n" + 
					"  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCTUQEcyXegXgKU\\nhoCwKxaQFPPsrg9/D+1iNw3R/j9b/Je85ih/zBBEkH6bRWZ/wfo2s+P6MHpobwnz\\nYqd9ZmdJZEZ01i+FuAmrBLeWfqA8MH/xAE7gzBtlbHwlcO0w1CJHJ7X7GtHoMEJi\\n1/TD8q2/ulex+uSM9j9OpO57aPCNjAccCI6gDp1Ie/2ffL7jx2Se2P6GRaxMNNGA\\nnLpBlo9BRYMSjIC5dbMp4n+h5lxT/rK+PkuGPp/5FkMHowR7AcMN69aAraN9aLcT\\nxn0AcuWo7xriCAE0ukWZ64EKme7Is9O92k6UpOXIaCHGzIxlaHxXEKlXm640Wvj8\\ne4iYQUqhAgMBAAECggEABZrNe2hFVTw6ZibzXrB2+mqXqKurIoMLPaM5UwHUrIXg\\nyoKprAoKHOev722do5LwV3Qy4l4FjDNcSi6Yz3YtzA6k+RU8h957fK6QYwkEhOG1\\n/mfr+FS/npVY6E5CcesCNlmSDICF16r9XZgYg3t4RYz8AOUaHKxSNoZjHd4dV887\\nH4AvS65SvKvv5DvB/Q1Xu9yoYoi+TJE4c4E8iMTpys+/N2QtUs4jb9a8ZCi0kuA8\\nCvhOFYDdkFZa7C6DZZN4iR1sIMSfw5m6Y8BMnn4iCAg8z3KJTM6X/14mJmcbpWOP\\nw2Lz6ByPP1MtdYLKd23sCw1nNXHb5MMVRn2bI3Vy4QKBgQDDzM2P0X7opBiMyXED\\nXGwBARGNWzFq6gkMHDdw/NKQuSJq96q7V2meYQU+0HcB71LX3efAG0FyfwkbEZfF\\nMgNTg5V2+ZmPmCZEwuXvdo2xTwS3DtRpR4RugJzTcHGi1/KDk6u7lOrMf3cQKzyT\\nQNMW5SMzSr6pOrnrmHjKigshcQKBgQDAnBzyN7lPICrcqa0Y8PxCfxQTAtgbT8PW\\n3+da2cLR76o/CsKwz9iXijQngeSHhalcVnB9S2WWl5c8d1COPl466YGCTRTRnElD\\nLcG54Dd30MT4JDm9wG425VcYbVph/VY5LzQZ6pEvgzCrr6+mXdj4W8tfc8xJozHH\\nEMgQnCwkMQKBgQCZgRMX0bKhSSVGGxfS/r5d+yx7micgHuOA7w44Mr4SYFKvcgQU\\nY++WD5JBMMZcafiU7JAoeDXTQe0tn5lRLGiXLO4dHIpbnXlDJ1mFZ3imrcCQk7vL\\n3hnhWEubKOiuNocWyMs2gQWj1brOnlcbSxMzO+lg6RSZMEVag2sPvzICwQKBgESb\\nVvQwRSGaI6pWBgJMVI6bFVtwN+oCj4+KUa5awrpk1A+AlbjFUDEqWUl5Sx26zojF\\ngWwf3x+0/3naRidkr8vsQOVY74F6Wd5D/i1nYFqdUovQ24Bretay+kMJMJiW1I+p\\n8LUKiIyT/lIpUjfAVfihOd/WL35LUv/FSn4gItkBAoGARqwRyoUIB+Rw3AnBTwi5\\nrA15T09ChuEcQLeA/sIYsW6l1msNONNRWVgxpia/k9250OK9A1JQZFSUNIWTG9z+\\nToV4F9uscd9e3gfc05Ul+Rgtnpbsq5ssbu/hIYb9eh/OojRH9uRCtVFwzRQeT7Tj\\nFeice13oIeDormj9cqME7GE=\\n-----END PRIVATE KEY-----\\n\",\r\n" + 
					"  \"client_email\": \"cs1660project@cs-1660-project-arie-dash.iam.gserviceaccount.com\",\r\n" + 
					"  \"client_id\": \"109336766852354944731\",\r\n" + 
					"  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\r\n" + 
					"  \"token_uri\": \"https://oauth2.googleapis.com/token\",\r\n" + 
					"  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\r\n" + 
					"  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/cs1660project%40cs-1660-project-arie-dash.iam.gserviceaccount.com\"\r\n" + 
					"}\r\n" + 
					"").getBytes("UTF-8"));
			
			//GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("./src/credentials.json"))
			GoogleCredentials credentials = GoogleCredentials.fromStream(cred)
					.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
			credentials.refreshIfExpired();
			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
			Dataproc dataproc = new Dataproc.Builder(new NetHttpTransport(),new JacksonFactory(), requestInitializer).setApplicationName("apd project").build();

			try {
				dataproc.projects().regions().jobs().submit(
						"cs-1660-project-arie-dash", "us-west1", new SubmitJobRequest()
						.setJob(new Job()
								.setReference(new JobReference()
										.setJobId(curJobId))
								.setPlacement(new JobPlacement()
										.setClusterName("cluster-apd"))
								.setHadoopJob(new HadoopJob()
										.setMainClass("InvertedIndex")
										.setJarFileUris(ImmutableList.of("gs://dataproc-staging-us-west1-109069267149-nric6xzk/JAR/invertedindex.jar"))
										.setArgs(ImmutableList.of(
												"gs://dataproc-staging-us-west1-109069267149-nric6xzk/data", "gs://dataproc-staging-us-west1-109069267149-nric6xzk/output")))))
				.execute();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				response = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute();
				System.out.println("Job submitted");
			}
			catch(IOException e2){
				e2.printStackTrace();
			}
			try {
				JobStatus stat = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute().getStatus();
				while(stat.getState().contentEquals("RUNNING")) {
					Thread.sleep(1000);
					stat = dataproc.projects().regions().jobs().get("cs-1660-project-arie-dash", "us-west1", curJobId).execute().getStatus();
				}
				if(stat.getState().equals("ERROR")) {
					System.out.println("Job Failed");
				}
				else {
					System.out.println("Job Completed");
					//get results from cluster and return;
					computed = true;
					return null;
				}
				
			}
			catch(IOException | InterruptedException e3) {
				e3.printStackTrace();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		showGui();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}