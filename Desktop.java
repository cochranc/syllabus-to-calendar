
/**
 * @author Arsema Berhane
 * @author Christa Cochran 
 * @author Nina Kouchi
 * @date Version 1, May 2020
 * @citations 
 * https://github.com/SeunMatt/morsecodetranslator/blob/master/src/main/java/com/smatt/morse/MorseCodeTranslator.java
 * https://medium.com/prodsters/how-to-build-a-desktop-application-with-java-a34ee9c18ee3
 * https://examples.javacodegeeks.com/desktop-java/swing/java-swing-boxlayout-example/
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
 * https://www.geeksforgeeks.org/java-swing-jsplitpane-examples/
 * https://www.tutorialspoint.com/swingexamples/show_open_file_dialog_multiple.htm
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Desktop {

	@SuppressWarnings("unchecked")
	public List<TreeMap<String, String>> datesArr = new ArrayList();

	public List<String> fileArr = new ArrayList();

	/**
	 * saveDates saves the name of assignment and dates as a string
	 * @param datesMap, a TreeMap
	 * @param startdate, a String 
	 * @param enddate, a String
	 * @return fullCal, a string
	 */
	public static String saveDates(TreeMap datesMap, String startdate, String enddate) {

		Iterator itr = datesMap.entrySet().iterator();

		StringBuilder fullCal = new StringBuilder();
		while(itr.hasNext()) {
			Map.Entry pair = (Map.Entry) itr.next();
			String str = pair.getKey().toString(); 
			if ((str.compareTo(startdate) >= 0) && (str.compareTo(enddate) <= 0)) {
				fullCal.append(str + ": "+ pair.getValue() + "\n");
			}
		}
		return fullCal.toString();
	}

	/**
	 * getCal gets the file
	 * @param file, a file
	 * @return dates, a TreeMap
	 */
	public static TreeMap<String, String> getCal(File file) throws IOException {
		//Loading an existing document
		PDDocument document = PDDocument.load(file);

		//Instantiate PDFTextStripper class
		PDFTextStripper pdfStripper = new PDFTextStripper();

		//Retrieving text from PDF document
		String text = pdfStripper.getText(document);
		String[] splits = text.split("\\s");

		TreeMap<String, String> dates = new TreeMap<String, String>();

		//splits the syllabus by date (and pattern matching!) 
		for(int i = 0; i < splits.length; i++) {
			if(splits[i].matches("\\d*/\\d*")){
				int j = i;
				StringBuilder assignment = new StringBuilder();
				while((j < splits.length - 1) && (!(splits[j+1].matches("\\d*/\\d*")))) {
					assignment.append(splits[j+1] + " ");
					j++;
				}
				dates.put(splits[i], assignment.toString());
			}
		}

		//Closing the document
		document.close();

		return dates;
	}

	//a brief description 
	private String info = "Assignment Calendar by Christa Cochran, Nina Kouchi, and Arsema Berhane \n" +
			"This application will output a calendar of assignments given the class syllabi, start date, and end date \n" 
			+ "When selecting multiple syllabi, make sure you click control as you select multiple syllabi :)";

	public Desktop() {

		//set up for the complete frame
		JFrame frame = new JFrame();
		frame.setTitle("Class Calendar");
		frame.setLayout(new BorderLayout()); 
		frame.setSize(new Dimension(800, 650));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setVisible(true);

		//place where the Calendar will be printed
		JTextArea calendarText = new JTextArea(20, 20); 
		calendarText.setLineWrap(true);
		calendarText.setWrapStyleWord(true);
		calendarText.setMargin(new Insets(5, 5, 5, 5));

		//label for Calendar
		JLabel calendarLabel = new JLabel("Calendar:"); 
		calendarLabel.setHorizontalAlignment(SwingConstants.CENTER);

		//complete Calendar Panel
		JPanel calendarPanel = new JPanel(new BorderLayout());
		calendarPanel.add(calendarLabel, BorderLayout.NORTH);
		calendarPanel.add(calendarText, BorderLayout.CENTER);

		//syllabus Panel to hold syllabus info, start date, end date, and calendar button
		JPanel syllabusPanel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(syllabusPanel, BoxLayout.Y_AXIS);
		syllabusPanel.setLayout(boxlayout);

		JTextArea syllabi = new JTextArea(); 
		syllabi.setAlignmentX( Component.LEFT_ALIGNMENT );
		syllabi.setLineWrap(true);
		syllabi.setWrapStyleWord(true);
		syllabi.setMargin(new Insets(5, 5, 5, 5));

		//select PDF button
		JButton selectPDF = new JButton("Select PDF");
		selectPDF.setAlignmentX( Component.LEFT_ALIGNMENT );

		JFileChooser fc = new  JFileChooser();
		fc.setMultiSelectionEnabled(true);

		//action listener for PDF and selects PDFs
		selectPDF.addActionListener((e) -> {

			int option = fc.showOpenDialog(frame);
			if(option == JFileChooser.APPROVE_OPTION){
				File[] files = fc.getSelectedFiles();
				String fileNames = "";
				int i = 0;

				//adds name of file to arraylist and output
				for(File file: files){
					fileNames += file.getName() + " ";
					fileArr.add(files[i].getName());

					try {
						datesArr.add(getCal(files[i]));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					i++;
				} 

				syllabi.setText("File(s) Selected: " + fileNames);
			}else{
				syllabi.setText("Open command cancelled");
			}
		}
				);

		syllabusPanel.add(selectPDF);
		syllabusPanel.add(syllabi);

		//Text Area to type start date
		JTextArea startDate = new JTextArea();
		startDate.setAlignmentX(Component.LEFT_ALIGNMENT );
		startDate.setText("Enter Start Date (M/DD): ");
		startDate.setLineWrap(true);
		startDate.setWrapStyleWord(true);
		startDate.setMargin(new Insets(5, 5, 5, 5));

		//Text Area to type end date
		JTextArea endDate = new JTextArea();
		endDate.setAlignmentX( Component.LEFT_ALIGNMENT );
		endDate.setText("Enter End Date (M/DD): ");
		endDate.setLineWrap(true);
		endDate.setWrapStyleWord(true);
		endDate.setMargin(new Insets(5, 5, 5, 5));

		//label for syllabus section
		JLabel syllabusLabel = new JLabel("Enter the following information:");
		syllabusLabel.setAlignmentX( Component.LEFT_ALIGNMENT );

		//label for calendar section
		JButton createCalendar = new JButton("Show me Calender");
		createCalendar.setAlignmentX( Component.LEFT_ALIGNMENT );

		//button takes start date and end date and prints out calendar
		createCalendar.addActionListener((e) -> {

			String fullStart = startDate.getText().trim();
			String[] fullString = fullStart.split(" ");
			String start = fullString[fullString.length - 1];

			String fullEnd = endDate.getText().trim();
			String[] fullString2 = fullEnd.split(" ");
			String end = fullString2[fullString2.length - 1];

			//adds strings together to output calendar
			for(int i = 0; i < datesArr.size(); i++) {
				StringBuilder buildCal = new StringBuilder();
				buildCal.append(calendarText.getText());
				buildCal.append(fileArr.get(i) + "\n");
				buildCal.append(saveDates(datesArr.get(i), start, end) + "\n");			
				calendarText.setText(buildCal.toString());
			}
		}
				);


		//add everything to the syllabusPanel
		syllabusPanel.add(syllabusLabel);
		syllabusPanel.add(startDate);
		syllabusPanel.add(endDate);
		syllabusPanel.add(createCalendar);		

		//essentially lets two panels be side by side 
		// left panel = syllabus, right panel = calendar
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, syllabusPanel, calendarPanel);

		//allows you to look at just calendar panel
		mainPanel.setOneTouchExpandable(true);

		//makes sure the split is even
		mainPanel.setDividerLocation(frame.getWidth() /4);

		JPanel infoPanel = new JPanel(new BorderLayout());
		JTextArea infoText = new JTextArea();
		infoText.setText(info);
		infoText.setEditable(false);
		infoText.setMargin(new Insets(5, 5, 5, 5));
		infoPanel.add(infoText, BorderLayout.CENTER);
		frame.add(infoPanel, BorderLayout.NORTH);
		frame.add(mainPanel, BorderLayout.CENTER);

	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			new Desktop();
		});
	}

}
