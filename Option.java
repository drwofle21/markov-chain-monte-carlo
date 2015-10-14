public class Option {

// Declaring variables.

	StockPrice stockPrice;
	double strikePrice;
	double price;
	double sampleVariance;
	double sampleStandardDeviation;
	double estimateStandardDeviation;
	double estimateVariance;
	double upperLevel;
	double lowerLevel;
	boolean isPut;
// A constructor for the class option
	public Option(StockPrice S, double K, boolean t) {
		stockPrice = S;
		strikePrice = K;
		isPut = t;
// A method that returns the call or put option price
		getPrice();
	}

	private void getPrice() {
// Declaring and initializing of variables
		double value;// Value of the option.
		double priceSum = 0;// the accumulated option value.
		double priceSquareSum = 0;// Accumulated value of the squared value of the option.

		if (!stockPrice.isSimulated) {// Checks if the boolean condition is false and returns the montecarlo price
			stockPrice.monteCarlo();// Method that returns the montecarlo price.
		}

		if (isPut) { // Checks if the GUI button selected is a put and calculates the value of a put.
			for (int i = 0; i < stockPrice.simulations; i++) { // A loop that creates a series of put option values.
				value = strikePrice
						- stockPrice.priceArray[i][stockPrice.steps];
				priceSum += Math.max(value, 0);
				priceSquareSum += value * value;

			}
			price = Math.exp(-stockPrice.rate * stockPrice.timeHorizon) // The discounted put option value.
					* priceSum / stockPrice.simulations; 
// Option statistics
			sampleVariance = (priceSquareSum - 2 * price * priceSum + stockPrice.simulations
					* price * price)
					/ (stockPrice.simulations - 1);
			sampleStandardDeviation = Math.sqrt(sampleVariance);
			estimateStandardDeviation = sampleStandardDeviation
					/ Math.sqrt(stockPrice.simulations);
			estimateVariance = estimateStandardDeviation
					* estimateStandardDeviation;
			upperLevel = price + 1.96 * estimateStandardDeviation;
			lowerLevel = price - 1.96 * estimateStandardDeviation;
		} else { // If the button selected is not a put, then the value of a call option is computed.
			for (int i = 0; i < stockPrice.simulations; i++) {
				value = stockPrice.priceArray[i][stockPrice.steps]
						- strikePrice;
				priceSum += Math.max(value, 0);
				priceSquareSum += value * value;
			}
			price = Math.exp(-stockPrice.rate * stockPrice.timeHorizon)// The discounted put option value.
					* priceSum / stockPrice.simulations;
// Option statistics
			sampleVariance = (priceSquareSum - 2 * price * priceSum + stockPrice.simulations
					* price * price)
					/ (stockPrice.simulations - 1);
			sampleStandardDeviation = Math.sqrt(sampleVariance);
			estimateStandardDeviation = sampleStandardDeviation
					/ Math.sqrt(stockPrice.simulations);
			estimateVariance = estimateStandardDeviation
					* estimateStandardDeviation;
			upperLevel = price + 1.96 * estimateStandardDeviation;
			lowerLevel = price - 1.96 * estimateStandardDeviation;

		}
	}
}
