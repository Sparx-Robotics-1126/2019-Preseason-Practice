package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.climbing.ClimbingCommand;
import frc.climbing.ClimbingOutput;
import frc.climbing.ClimingSensors;
import frc.climbing.commands.ClimbingRetract;
import frc.climbing.commands.ExtendScissorLift;
import frc.climbing.commands.StartWinch;
import frc.climbing.commands.StopWinch;
import frc.climbing.commands.UnwindWinch;
import frc.health.HealthReport;
import frc.robot.IO;

public class Climbing extends Subsystem {

	public CANSparkMax winch;
	public TalonSRX scissorlift;
	private ClimbingCommand extendingCommand;
	private ClimbingCommand winchingCommand;

	private ClimingSensors sensors;

	public Climbing() {
		winch = new CANSparkMax(IO.CLIMBING_WINCH_MOTOR, MotorType.kBrushless);
		scissorlift = new TalonSRX(IO.CLIMBING_SCISSORLIFT_MOTOR);
		scissorlift.configFactoryDefault();
		scissorlift.setInverted(true);
		sensors = new ClimingSensors(winch);
		winchingCommand = null;
		extendingCommand = null;
	}

	@Override
	void execute() {
		if (extendingCommand != null) {
			ClimbingOutput output = extendingCommand.execute();
			scissorlift.set(ControlMode.PercentOutput, output.getOutput());
			if (output.isFinished()) {
				extendingCommand = null;
				scissorlift.set(ControlMode.PercentOutput, 0);
			}
		}
		if (winchingCommand != null) {
			ClimbingOutput output = winchingCommand.execute();
			winch.set(output.getOutput());
			if (output.isFinished()) {
				winchingCommand = null;
				winch.set(0);
			}
		}
	}

	public void startWinch() {
		winchingCommand = new StartWinch(sensors, 60);
	}

	public void extendScissorLift() {
		extendingCommand = new ExtendScissorLift(sensors, 6);
	}

	public void retractScissorLift() {
		extendingCommand = new ClimbingRetract(sensors);
	}

	public void stopWinch() {
		winchingCommand = new StopWinch(sensors);
	}

	public void unwindWinch() {
		winchingCommand = new UnwindWinch(sensors, 0.5);
	}

	@Override
	public boolean isDone() {
		return extendingCommand == null && winchingCommand == null;
	}

	@Override
	public HealthReport getHealthCheck() {
		HealthReport winch;
		HealthReport scissor;
		if(extendingCommand!=null){
			scissor = extendingCommand.checkHealth();	
		}else{
			scissor = new HealthReport();
		}
		if(winchingCommand != null){
			winch = winchingCommand.checkHealth();
		}else{
			winch = new HealthReport();
		}
		if(scissor.isError()){
			return scissor;
		}
		return winch;
	}
	
}
