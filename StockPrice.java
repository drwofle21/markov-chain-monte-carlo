import java.util.Random;
import org.jfree.data.xy.*;

class StockPrice {
// Declaring variables
	double initialStockPrice;
	double rate;
	double volatility;
	double timeHorizon;
	int steps;
	private int simulation_number = 0;
	double monteCarloPrice = 0;
	int simulations;
	double[][] priceArray;
	boolean isSimulated;

	XYSeries series = new XYSeries("Simualted Stock Price 1");
	XYSeriesCollection dataset = new XYSeriesCollection();
// StockPrice constructor
	public StockPrice(double setInitialStockPrice, double setRate,
			double setVolatility, double setHorizon, int setSteps,
			int setSimulations) {
// Initializing the variables

		initialStockPrice = setInitialStockPrice;
		rate = setRate;
		volatility = setVolatility;
		timeHorizon = setHorizon;
		steps = setSteps;
		simulations = setSimulations;

		buildArray(simulations, steps);
	}
// Methods that return no value. They set the values of the parameters to those entered by the user.

	void setInitialStockPrice(double S) {
		initialStockPrice = S;
	}

	void setRate(double r) {
		rate = r;
	}

	void setVolatility(double v) {
		volatility = v;
	}

	void setHorizon(double T) {
		timeHorizon = T;
	}

	void setSteps(int x) {
		steps = x;
	}

	void setSimulations(int x) {
		simulations = x;
	}

	void buildArray(int x, int y) {
		priceArray = new double[x][y + 1];
	}

// A method that returns the final stock price.
	double simulateStockPrice() {
// Initializing method variables.
		double dt = timeHorizon / steps;
		double time = 0;
		double stockPriceCopy = initialStockPrice;
		int count = 1;
		Random W = new Random();
		double finalStockPrice = 0;
		priceArray[simulation_number][0] = initialStockPrice;// Sets the first element of the array to the initial
// stock price entered by the user in the GUI.

		if (simulation_number < 20) {
			series.add(time, priceArray[simulation_number][0]); // takes the first 20 elements of the price array with
// together with the corresponding time steps and forms a data set to be used to make the XY Chart.
		}
		while (count != steps + 1) {
// This line generates the price process(trajectory) as long as the variable count is less than the number of steps.
			finalStockPrice = initialStockPrice
					* Math.exp((rate - 0.5 * volatility * volatility) * dt
							+ volatility * Math.sqrt(dt) * W.nextGaussian()); 
			this.setInitialStockPrice(finalStockPrice);// sets the initial stock price to the last price calculated.
// This line stores the final values computed into the price array for the corresponding time step and simulation.
			priceArray[simulation_number][count] = finalStockPrice;
// This line updates the time by incrementing the last value with dt.
			time = time + dt;

			if (simulation_number < 20) {
				series.add(time, priceArray[simulation_number][count]);
			}
			count++;
		}
		this.setInitialStockPrice(stockPriceCopy);
		if (simulation_number < 20) {
// Sets up the dataset that will be used to create the graph by use of JFreechart library.
			dataset.addSeries(series);
		}
		return finalStockPrice;
	}

	double monteCarlo() {
		int count = 2;
		for (int i = 0; i < simulations; i++) {
//This line sums up the final stock price for each simulation and assigns to the variable montecarloPrice.
			monteCarloPrice = monteCarloPrice + simulateStockPrice();
			series = new XYSeries("Simualted Stock Price " + count);
			count++; // increments the variable count by 1 each time the condition is fulfilled.
			simulation_number++;// Increments the number of simulations until the condition is fulfilled
		}
		monteCarloPrice = monteCarloPrice / simulations; // Calculates the average stock price.
		isSimulated = true;
		return monteCarloPrice; // The value that the method returns when called.
	}
}
