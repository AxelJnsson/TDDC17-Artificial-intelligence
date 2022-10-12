public class StateAndReward {

	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */
		String state = "OneStateToRuleThemAll";
		int stateInt = discretize(angle, 16, -3.14, 3.14);
		switch (stateInt) {
		case 0:
			state = "dl1";
			break;
		case 1:
			state = "dl2";
			break;
		case 2:
			state = "dl3";
			break;
		case 3:
			state = "dl4";
			break;
		case 4:
			state = "ul1";
			break;
		case 5:
			state = "ul2";
		case 6:
			state = "ul3";
			break;
		case 7:
			state = "ul4";
			break;
		case 8:
			state = "ur1";
			break;
		case 9:
			state = "ur2";
			break;
		case 10:
			state = "ur3";
			break;
		case 11:
			state = "ur4";
			break;
		case 12:
			state = "dr1";
			break;
		case 13:
			state = "dr2";
			break;
		case 14:
			state = "dr3";
			break;
		case 15:
			state = "dr4";
			break;

		}

		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		double reward = 0;
		/* TODO: IMPLEMENT THIS FUNCTION */
		int stateInt = discretize(angle, 16, -3.14, 3.14);
		switch (stateInt) {
		case 0:
			reward = -2;
			break;
		case 1:
			reward = -1.5;
			break;
		case 2:
			reward = -1;
			break;
		case 3:
			reward = -0.5;
			break;
		case 4:
			reward = 0.5;
			break;
		case 5:
			reward = 1;
			break;
		case 6:
			reward = 1.5;
			break;
		case 7:
			reward = 2;
			break;
		case 8:
			reward = 2;
			break;
		case 9:
			reward = 1.5;
			break;
		case 10:
			reward = 1;
			break;
		case 11:
			reward = 0.5;
			break;
		case 12:
			reward = -0.5;
			break;
		case 13:
			reward = -1;
			break;
		case 14:
			reward = -1.5;
			break;
		case 15:
			reward = -2;
			break;
		}

		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */

		String state_angle = String.valueOf(discretize(angle, 16, -3.14, 3.14));
		String state_vx = String.valueOf(discretize(vx, 5, -0.5, 0.5));
		String state_vy = String.valueOf(discretize(vy, 5, -0.5, 0.5));
		return (state_angle + state_vx + state_vy);
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {

		/* TODO: IMPLEMENT THIS FUNCTION */

		double reward = 0;
		double reward_x = 0;
		double reward_y = 0;
		// int state_angle = (discretize(angle, 16, -3.14, 3.14));
		int state_vx = (discretize(vx, 5, -0.5, 0.5));
		int state_vy = (discretize(vy, 5, -0.5, 0.5));
		
		switch (state_vx) {
		case 0:
			reward_x = -3;
			break;
		case 1:
			reward_x = -1.5;
			break;
		case 2:
			reward_x = 3;
			break;
		case 3:
			reward_x = -1.5;
			break;
		case 4:
			reward_x = -3;
			break;
		}

		switch (state_vy) {
		case 0:
			reward_y = -3;
			break;
		case 1:
			reward_y = -1.5;
			break;
		case 2:
			reward_y = 3;
			break;
		case 3:
			reward_y = -1.5;
			break;
		case 4:
			reward_y = -3;
			break;
		}

		if (vx == 0) {
			reward_x = 6;
			
		}
		if (vy==0) {
			reward_y = 6;
		}
		
		reward = 1.2*reward_x + reward_y + 3*getRewardAngle(angle,vx,vy);
		
		return reward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min, double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min, double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {

			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;
		return (int) (ratio * nrValues);
	}

}