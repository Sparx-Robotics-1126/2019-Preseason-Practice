package frc.controllers;

import edu.wpi.first.wpilibj.Joystick;

public class AxisButton extends Button{
	
	private Axis axis;
	private final double SENSITIVITY;
	private boolean buttonPreviouslyPressed;
	
	public AxisButton(Joystick joystick, int axisNum, ButtonType type) {
		super(joystick, axisNum, type);
		SENSITIVITY = 0.25;
		axis = new Axis(joystick, axisNum, false);
	}
	
	@Override
	public boolean get() {
		double value = axis.get();
		boolean currentlyPressed = value > SENSITIVITY ? true : false;
		boolean trigger = getTriggered(currentlyPressed, buttonPreviouslyPressed);
		buttonPreviouslyPressed = currentlyPressed;
		return trigger;
	}

}
