package figglewatts.MegaDriveController;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import jssc.SerialPort;
import jssc.SerialPortException;

public class MegaDriveController {

	public static int[] buttonMappings = { KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_Q };
	public static boolean[] buttonStates = { false, false, false, false, false, false, false, false };
	public static boolean[] oldButtonStates = new boolean[8];
	public static Robot VKey;
	
	public static void main(String[] args) {
		System.out.println("Mega Drive Controller Interface");
		System.out.println("Made by Figglewatts, 2015-3-31");
		System.out.println("-------------------------------");
		
		boolean encounteredError = false;
		
		try {
			VKey = new Robot();
		} catch (AWTException awte) {
			// TODO Auto-generated catch block
			awte.printStackTrace();
			encounteredError = true;
		}
		
		SerialPort ser = new SerialPort("COM4");
		try {
			ser.openPort();
			ser.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		}
		catch (SerialPortException e)
		{
			System.out.println(e);
			encounteredError = true;
		}
		
		if (!encounteredError)
		{
			System.out.println("Now listening for controller input on serial port " + ser.getPortName());
		}
		
		byte recieved = 0;
		
		while (true)
		{
			if (encounteredError)
			{
				break;
			}
			
			try {
				recieved = ser.readBytes(1)[0];
			} catch (SerialPortException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
				break;
			}
			
			SendKeyEvents(recieved);
		}
	}
	
	public static void SendKeyEvents(byte data)
	{
		oldButtonStates = buttonStates.clone();
		
		for (int i = 0; i < 8; i++)
		{
			if (isBitSet(data, i))
			{
				// if button is not already pressed
				if (buttonStates[i] == false)
				{
					buttonStates[i] = true;
					System.out.println("Button " + i + " pressed");
					VKey.keyPress(buttonMappings[i]);
				}
			}
			else
			{
				if (oldButtonStates[i] == true)
				{
					buttonStates[i] = false;
					System.out.println("Button " + i + " released");
					VKey.keyRelease(buttonMappings[i]);
				}
			}
		}
	}
	
	public static boolean isBitSet(byte b, int index)
	{
		return (b & (1 << index)) != 0;
	}
}
