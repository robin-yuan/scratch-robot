package de.joeakeem.scratch.robot;

import static com.pi4j.io.gpio.RaspiPin.GPIO_00;
import static com.pi4j.io.gpio.RaspiPin.GPIO_01;
import static com.pi4j.io.gpio.RaspiPin.GPIO_02;
import static com.pi4j.io.gpio.RaspiPin.GPIO_03;
import static com.pi4j.io.gpio.RaspiPin.GPIO_04;
import static com.pi4j.io.gpio.RaspiPin.GPIO_05;
import static com.pi4j.io.gpio.RaspiPin.GPIO_06;
import static com.pi4j.io.gpio.RaspiPin.GPIO_07;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.pi4j.io.gpio.RaspiPin;

import de.joeakeem.m28BYJ48.StepperMotor28BYJ48;
import de.joeakeem.m28BYJ48.StepperMotor28BYJ48.SteppingMethod;

@Configuration
@Profile("realMotor")
public class RealMotorConfiguration {
	
	private static final String MOVE_FORWARD_BROADCAST_MESSAGE = "vor";
	private static final String TURN_LEFT_BROADCAST_MESSAGE = "links";
	private static final String TURN_RIGHT_BROADCAST_MESSAGE = "rechts";
	
	@Autowired
	private Environment env;
	
	@Bean(name="leftMotor")
	public StepperMotor28BYJ48 getLeftMotor() {
		return new StepperMotor28BYJ48(GPIO_01, GPIO_04, GPIO_05, GPIO_06, 3, SteppingMethod.FULL_STEP);
	}
	
	@Bean(name="rightMotor")
	public StepperMotor28BYJ48 getRightMotor() {
		return new StepperMotor28BYJ48(GPIO_07, GPIO_00, GPIO_02, GPIO_03, 3, SteppingMethod.FULL_STEP);
	}
	
	@Bean(name="leftMotorSensor")
	public StepperMotorSensor getLeftMotorSensor(@Qualifier("leftMotor") StepperMotor28BYJ48 leftMotor) {
		StepperMotorSensor motorSensor = new StepperMotorSensor();
		motorSensor.setName("Left");
		motorSensor.setScratchHost(env.getProperty("scratch.host", "localhost"));
		motorSensor.setScratchPort(Integer.parseInt(env.getProperty("scratch.port", "42001")));
		motorSensor.setMotor(leftMotor);
		ArrayList<String> commandsToReactOn = new ArrayList<String>(2);
		commandsToReactOn.add(MOVE_FORWARD_BROADCAST_MESSAGE);
		commandsToReactOn.add(TURN_RIGHT_BROADCAST_MESSAGE);
		motorSensor.setCommandsToReactOn(commandsToReactOn);
		motorSensor.setDirection(-1);
		return motorSensor;
	}
	
	@Bean(name="rightMotorSensor")
	public StepperMotorSensor getRightMotorSensor(@Qualifier("rightMotor") StepperMotor28BYJ48 rightMotor) {
		StepperMotorSensor motorSensor = new StepperMotorSensor();
		motorSensor.setName("Right");
		motorSensor.setScratchHost(env.getProperty("scratch.host", "localhost"));
		motorSensor.setScratchPort(Integer.parseInt(env.getProperty("scratch.port", "42001")));
		motorSensor.setMotor(rightMotor);
		ArrayList<String> commandsToReactOn = new ArrayList<String>(2);
		commandsToReactOn.add(MOVE_FORWARD_BROADCAST_MESSAGE);
		commandsToReactOn.add(TURN_LEFT_BROADCAST_MESSAGE);
		motorSensor.setCommandsToReactOn(commandsToReactOn);
		return motorSensor;
	}
	
	@Bean
	public MotionDetector getMotionDetector() {
		PirMotionDetector pir = new PirMotionDetector(RaspiPin.GPIO_12);
		pir.setScratchHost(env.getProperty("scratch.host", "localhost"));
		pir.setScratchPort(Integer.parseInt(env.getProperty("scratch.port", "42001")));
		return pir;
	}
}
