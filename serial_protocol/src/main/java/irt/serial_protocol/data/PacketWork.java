package irt.serial_protocol.data;

import irt.serial_protocol.data.packet.Packet;

public interface PacketWork extends Comparable<PacketWork>{

	public static final short PACKET_UNNECESSARY = 0;

	public static final short PACKET_DEVICE_INFO = 1;

	public static final short PACKET_ID_MEASUREMENT_ALL 					= 9;
	public static final short PACKET_ID_MEASUREMENT_STATUS 					= 10;
	public static final short PACKET_ID_MEASUREMENT_INPUT_POWER				= 11;
	public static final short PACKET_ID_MEASUREMENT_BAIAS_25W_OUTPUT_POWER 	= 12;
	public static final short PACKET_ID_MEASUREMENT_BIAS_25W_TEMPERATURE 	= 13;
	public static final short PACKET_ID_MEASUREMENT_UNIT_TEMPERATURE		= 14;
	public static final short PACKET_ID_MEASUREMENT_CPU_TEMPERATURE			= 15;
	public static final short PACKET_ID_MEASUREMENT_5V5 					= 16;
	public static final short PACKET_ID_MEASUREMENT_13V2 					= 17;
	public static final short PACKET_ID_MEASUREMENT_13V2_NEG				= 18;
	public static final short PACKET_ID_MEASUREMENT_OUTPUT_POWER 			= 19;

	public static final short 	PACKET_ID_CONFIGURATION_FCM_LO_BIAS_BOARD		= 20,
								PACKET_ID_CONFIGURATION_FCM_LO_FREQUENCIES 		= 21,
								PACKET_ID_CONFIGURATION_FCM_BAIAS_25W_MUTE 		= 22,
								PACKET_ID_CONFIGURATION_FCM_GAIN				= 23,
								PACKET_ID_CONFIGURATION_FCM_GAIN_RANGE 			= 24,
								PACKET_ID_CONFIGURATION_FCM_ATTENUATION			= 25,
								PACKET_ID_CONFIGURATION_FCM_ATTENUATION_RANGE 	= 26,
								PACKET_ID_CONFIGURATION_FCM_FREQUENCY 			= 27,
								PACKET_ID_CONFIGURATION_FCM_FREQUENCY_RANGE 	= 28,
								PACKET_ID_CONFIGURATION_FCM_STORE_CONFIG		= 29,
								PACKET_ID_CONFIGURATION_FCM_LNB					= 30,
								PACKET_ID_CONFIGURATION_FCM_GAIN_OFFSET			= 31,
								PACKET_ID_CONFIGURATION_FCM_FLAGS 				= 32,
								PACKET_ID_CONFIGURATION_FCM_ALL 				= 33;

	public static final short PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N1 		= 40,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N1_SET 	= 41,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N2 		= 42,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N2_SET 	= 43,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N3 		= 44,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N3_SET	= 45,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N4 		= 46,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N4_SET	= 47,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N5 		= 48,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N5_SET	= 49,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTENTIOMETER_N6 		= 50,
								PACKET_BIAS_25W_DEVICE_DEBAG_POTRNTIOMETER_N6_SET	= 51,
								PACKET_BIAS_25W_DEVICE_DEBAG_SWITCH_N1 				= 52,
								PACKET_BIAS_25W_DEVICE_DEBAG_SWITCH_N2 				= 53,
								PACKET_BIAS_25W_DEVICE_DEBAG_HS1_CURRENT 			= 54,
								PACKET_BIAS_25W_DEVICE_DEBAG_HS2_CURRENT 			= 55,
								PACKET_BIAS_25W_DEVICE_DEBAG_OUTPUT_POWER 			= 56,
								PACKET_BIAS_25W_DEVICE_DEBAG_TEMPERATURE 			= 57,
								PACKET_BIAS_25W_DEVICE_DEBAG_NGLOBAL 				= 58,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_DAC1 		= 59,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_DAC2 		= 60,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_DAC3 		= 61,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_DAC4 		= 62,
								PACKET_BIAS_DEVICE_DEBAG_CALIBRATION_MODE 			= 63,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_PLL_1 		= 64,
								PACKET_BIAS_25W_DEVICE_DEBAG_CONVERTER_PLL_2 		= 65,
								PACKET_ID_DEVICE_DEBAG_DEVICE_INFO					= 66;

	public static final short 	PACKET_ID_FCM_DEVICE_DEBAG_PLL_REG 	= 70;

	public static final short 	PACKET_ID_FCM_ADC_INPUT_POWER 	= 80,
								PACKET_ID_FCM_ADC_OUTPUT_POWER	= 81,
								PACKET_ID_FCM_ADC_TEMPERATURE 	= 82,
								PACKET_ID_FCM_ADC_CURRENT 		= 83,
								PACKET_ID_FCM_ADC_5V5 			= 84,
								PACKET_ID_FCM_ADC_13v2 			= 85,
								PACKET_ID_FCM_ADC_13V2_NEG		= 86;

//Dumps to file
	public static final short	PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_0	= 90,//Index 0.  Board info, like: CPU frequency, CPU reset type, CPU clock source, Local temperature
								PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_1	= 91,//Index 1.  Board error info
								PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_2	= 92,//Index 2.  Threshold info, like:  Zero current, Over-current HSS1, Over-current HSS2, Over-temperature mute, Over-temperature unmute
								PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_3	= 93,//Index 3.  I2C error statistics
								PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_4	= 94,//Index 4.  Mute state machine flags in format: Flag_name: Current_flag (History_flag)
								PACKET_ID_DUMP_DEVICE_DEBAG_DEVICE_INFO_10	= 99,//Index 10.  FCM info
								PACKET_ID_DUMP_REGISTER_100					= 100,
								PACKET_ID_DUMP_REGISTER_1					= 101,
								PACKET_ID_DUMP_REGISTER_2					= 102,
								PACKET_ID_DUMP_REGISTER_3					= 103,
								PACKET_ID_DUMP_REGISTER_4					= 104,
								PACKET_ID_DUMP_REGISTER_5					= 105,
								PACKET_ID_DUMP_REGISTER_6					= 106,
								PACKET_ID_DUMP_REGISTER_7					= 107,
								PACKET_ID_DUMP_REGISTER_10					= 110;

	public static final short PACKET_ID_PRODUCTION_GENERIC_SET_1_INITIALIZE = 120;

	public static final short PACKET_NETWORK_ADDRESS = 130;


	public static final short 	PACKET_ID_ALARMS = 140,
								PACKET_ID_ALARMS_OWER_CURRENT		= 141,
								PACKET_ID_ALARMS_UNDER_CURRENT		= 142,
								PACKET_ID_ALARMS_OWER_TEMPERATURE	= 143,
								PACKET_ID_ALARMS_PLL_OUT_OF_LOCK	= 144,
								PACKET_ID_ALARMS_HARDWARE_FAULT		= 145,
								PACKET_ID_ALARMS_SUMMARY			= 146;

	public Integer getPriority();
//	public PacketThread getPacketThread();
//	public void addVlueChangeListener(ValueChangeListener valueChangeListener);
//	public void removeVlueChangeListener(ValueChangeListener valuechangelistener);
	public boolean set(Packet packet);
	public void clear();
}