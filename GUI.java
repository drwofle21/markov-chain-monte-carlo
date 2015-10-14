import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.*;

public class GUI {
	DecimalFormat numberFormat = new DecimalFormat("####.##"); // An object that sets a double to two decimal places.
// Declaring and initializing variables.
	StockPrice stockPrice;
	Option option;
// Libraries for the creation of the XY Chart.
	JFreeChart chart;
	XYSeries series;
	XYSeriesCollection dataset;
	ChartPanel chartPanel;


	JFrame frame;// creates a frame object
	JPanel panel = new JPanel(new GridBagLayout());// creates a panel and sets the layout type to GridBag.
	JPanel plot = new JPanel(); // crates anothe panel where the chart will be displayed
	JButton compute = new JButton("COMPUTE"); //Creates a button called COMPUTE.
	JRadioButton callButton = new JRadioButton("CALL"); // Creates a call button
	JRadioButton putButton = new JRadioButton("PUT");
// These lines creates labels with the names in the quotation marks
	LabelWithText startingPrice = new LabelWithText("Starting Price");
	LabelWithText rate = new LabelWithText("Rate");
	LabelWithText volatility = new LabelWithText("Volatility");
	LabelWithText time = new LabelWithText("Time to Maturity");
	LabelWithText simulations = new LabelWithText("Simulations");
	LabelWithText strikePrice = new LabelWithText("Strike Price");
	LabelWithText steps = new LabelWithText("Steps");

//These lines refer to a class that creates a label and assigns it a default text of "0.00"		
	DoubleLabel maturityLabel = new DoubleLabel("Avg. Price at Maturity");
	DoubleLabel optionPrice = new DoubleLabel("Option Price");
	DoubleLabel sampleVariance = new DoubleLabel("Sample Variance");
	DoubleLabel sampleStandardDeviation = new DoubleLabel("Sample S.D.");
	DoubleLabel estimateStandardDeviation = new DoubleLabel("Estimate S.D.");
	DoubleLabel estimateVariance = new DoubleLabel("Estimate Variance");
	DoubleLabel upperLevel = new DoubleLabel("Upper Bound");
	DoubleLabel lowerLevel = new DoubleLabel("Lower Bound");

	GridBagConstraints bag = new GridBagConstraints();// An object that spells out the design parameters of the layout

// The method that creates the GUI. Declares the various methods that are called from within itself
	GUI() {
		// assigns the label in the quotation marks to the frame created
		frame = new JFrame("Monte-Carlo Option Pricer");
		frame.setLayout(new BorderLayout());// Sets the fram layout to type BorderLayout
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Makes the frame exit when the close button is pressed
// Gets everything that is in the panel and adds to the frame		
		frame.getContentPane().add(panel, BorderLayout.CENTER);
//Fixes the size of the frame so that resizing is not possible.
		frame.setResizable(false);

// The GUI methods
		initializeTopLabels();// creates the default contents of the top(North) part of the GUI
		initializeplotGraph(dataset);// creates the panel on which the graph will be ploted
		initializeButtons();// Creates the panel for the action buttons 
		initializeBottom();// Creates the default contents of the Bottom pane
	}
// This class creates customized labels with textfields which are then added into a panel. 
	class LabelWithText {
		JLabel label;
		JPanel holder = new JPanel();
		JTextField text = new JTextField(5);

		public LabelWithText(String s) {
			holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
			label = new JLabel(s);
			text.setFont(new Font("Arial", 1, 14));
			holder.add(label);
			holder.add(text);
		}

		JPanel getPanel() {
			return holder;
		}
	}
// This class cretes customised labels with default text"0.00" which are then added to a panel.

	class DoubleLabel {
		JLabel label;
		JLabel text = new JLabel("0.00");
		JPanel holder = new JPanel();

		public DoubleLabel(String s) {
			holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
			label = new JLabel(s);
			holder.add(label);
			holder.add(text);
		}

		JPanel getPanel() {
			return holder;
		}
	}

//This method creates smaller panels which are set to type flowLayout and positioned at grid (0,0) of the GridBag Layout frame.
	void initializeTopLabels() {
		JPanel topLabels = new JPanel();
		topLabels.setLayout(new FlowLayout());

		topLabels.add(startingPrice.getPanel());
		topLabels.add(strikePrice.getPanel());
		topLabels.add(rate.getPanel());
		topLabels.add(volatility.getPanel());
		topLabels.add(time.getPanel());
		topLabels.add(simulations.getPanel());
		topLabels.add(steps.getPanel());
		topLabels.add(maturityLabel.getPanel());

		bag.gridx = 0;
		bag.gridy = 0;
		bag.insets = new Insets(0, 5, 0, 5);
		panel.add(topLabels, bag);
		frame.pack();

	}

/* 
*  This method creates a panel onto which the acton buttons are added. This panel is then positioned at grid(1,1) of the 
*  Flowlayout frame.
*/  
	void initializeButtons() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.add(compute);
		callButton.setSelected(true);// Makes the PutButton deselected when the Callbutton is selected
		rightPanel.add(callButton);
		rightPanel.add(putButton);
		ButtonGroup group = new ButtonGroup();
		group.add(callButton);
		group.add(putButton);
		bag.gridx = 1;
		bag.gridy = 1;
		panel.add(rightPanel, bag);
// This is the action listner for the COMPUTE button.

		compute.addActionListener(new ActionListener() {
			double s;
			double r;
			double v;
			double t;
			double k;
			int st;
			int sims;
			boolean failed = false;
/*
*  The list of exceptions to be handled  when the compute button is clicked.
*  An exception will be thrown if any of the conditions below is not satisfied.
*/

			void checkValues() {
				if (s <= 0)
					throw new IllegalArgumentException(
							"The stock price can not be less than or equal zero.");
				if (k < 0)
					throw new IllegalArgumentException(
							"The strike price can not be less than zero.");
				if (r < 0)
					throw new IllegalArgumentException(
							"The rate can not be less than zero.");
				if (v < 0 || v > 1)
					throw new IllegalArgumentException(
							"The volatility can not be less than zero or greater than one.");
				if (t < 0 || t > 10)
					throw new IllegalArgumentException(
							"The time to maturity can not be less than zero or greater than ten.");
				if (st < 1 || st > 500)
					throw new IllegalArgumentException(
							"The amount of steps must be a positive integer greater than zero but less than or equal to 500.");
				if (sims < 0 || sims > 20000)
					throw new IllegalArgumentException(
							"The amount of simulations must be a positive number less than or equal to 20000");
			}
// The program proceeds to perform the computations if the above conditions are fulfilled.

			void setValues() {

				if (!failed) {
					plot.removeAll();// Cleans the plot panel and clears everything on it.
					stockPrice = new StockPrice(s, r, v, t, st, sims); // Calculates the final stockPrice.
					double price = stockPrice.monteCarlo(); // Returns the monte carlo estimate of the stock price.
					maturityLabel.text.setText(String.valueOf(numberFormat
							.format(price))); // Sets the monte carlo price to two decimal places.
					option = new Option(stockPrice, k, putButton.isSelected());// Calculates the value of the option selected.


// These lines takes the values of the computed statistics and sets the result to  two decimal places
					optionPrice.text.setText(String.valueOf(numberFormat
							.format(option.price)));
					sampleVariance.text.setText(String.valueOf(numberFormat
							.format(option.sampleVariance)));
					sampleStandardDeviation.text.setText(String
							.valueOf(numberFormat
									.format(option.sampleStandardDeviation)));
					estimateStandardDeviation.text.setText(String
							.valueOf(numberFormat
									.format(option.estimateStandardDeviation)));
					estimateVariance.text.setText(String.valueOf(numberFormat
							.format(option.estimateVariance)));
					upperLevel.text.setText(String.valueOf(numberFormat
							.format(option.upperLevel)));
					lowerLevel.text.setText(String.valueOf(numberFormat
							.format(option.lowerLevel)));
// This method uses dataset created in the method StockPrice to plot the graph
					initializeplotGraph(stockPrice.dataset);
				} else {
					failed = false; // updates the condition to false.
				}
			}

			public void actionPerformed(ActionEvent evt) {
// The boolean condition checks if the values enetered are of the correct type
// If the values entered by the user are of the correct data type the program continues just fine
				boolean valuesOk = true;
				if (!failed) {
					try {
						r = Double.parseDouble(rate.text.getText());
						v = Double.parseDouble(volatility.text.getText());
						t = Double.parseDouble(time.text.getText());
						st = Integer.parseInt(steps.text.getText());
						sims = Integer.parseInt(simulations.text.getText());
						k = Double.parseDouble(strikePrice.text.getText());
						s = Double.parseDouble(startingPrice.text.getText());
					} catch (Exception e) {
// if the values are of the wrong data type, a windo pops up with the message in the quotation marks.
						valuesOk = false;
						JOptionPane
								.showMessageDialog(
										frame,
										"There must only be positive integers for the amount of simulations and steps. There can only be positive numbers for the other inputs.");
					}
				}

				if (valuesOk) {
					try {
						checkValues();
					} catch (IllegalArgumentException e) {
						failed = true;
						JOptionPane.showMessageDialog(frame, e.getMessage(),
								"Warning!", JOptionPane.ERROR_MESSAGE);
					} finally {
						setValues();
					}
				}

			}
		});
		frame.pack();

	}
// This method returns a default chart area with no graph when the dataset is empty.
// It sets the chart area to a predefined size and positions it at grid(0,1) of the frame.


	void initializeplotGraph(XYSeriesCollection c) {
		if (c == null) {
			plot.setLayout(new BorderLayout());
			series = new XYSeries("Empty Graph");
			c = new XYSeriesCollection();
			series.add(0, 0);
			c.addSeries(series);
		}
		
		bag.fill = GridBagConstraints.HORIZONTAL;
		bag.weightx = 1.0;
		bag.gridwidth = 1;
		bag.gridx = 0;
		bag.gridy = 1;
		chart = ChartFactory.createXYLineChart("Stock Price Simulation",
				"Time to Maturity", "Stock Price", c, PlotOrientation.VERTICAL,
				false, true, false);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(575, 300));
		plot.add(chartPanel, BorderLayout.NORTH);
		panel.add(plot, bag);
		frame.pack();

	}

/* 
*  This method creates panels at the bottom of the frame by making use of the DoubleLabel class.
*  The panels are them arranged in the BoxLayout format.
*  These are then positon at grid (0,2) of the frame.
*  These panels are the ones that display the statistics of the option 
*/
	void initializeBottom() {

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(optionPrice.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(sampleVariance.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(sampleStandardDeviation.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(estimateStandardDeviation.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(estimateVariance.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(lowerLevel.getPanel());
		bottomPanel.add((Box.createRigidArea(new Dimension(25, 0))));
		bottomPanel.add(upperLevel.getPanel());

		bag.fill = GridBagConstraints.HORIZONTAL;
		bag.weightx = 1.0;
		bag.gridwidth = 1;
		bag.gridx = 0;
		bag.gridy = 2;
		panel.add(bottomPanel, bag);
		frame.pack();

	}
/* 
*  This being the main method, it calls the GUI method that implements all the releted methods.
*  to bring about the expected result. 
*/
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			//@Override
			public void run() {
				try {
					GUI runApp = new GUI();
					runApp.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
