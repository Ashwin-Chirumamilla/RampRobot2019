package frc.robot;
//import edu.wpi.first.wpilibj.SerialPort;
//import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
//Warning: if the pixy is plugged in through mini usb, this code WILL NOT WORK b/c the pixy is smart and detects where it should send data
public class Pixy {
    //SerialPort pixy;
    int e=1;
    I2C pixy; 
    Port port = Port.kOnboard;
    PixyPacket[] packets;
    PixyException pExc;
    String print;
    public Pixy() {
        
        //pixy = new SerialPort(19200, port);
        //pixy.setReadBufferSize(14);
        pixy = new I2C(port, 0x54);
        packets = new PixyPacket[7];
        pExc = new PixyException(print);
}
//This method parses raw data from the pixy into readable integers
public int cvt(byte upper, byte lower) {
    return (((int)upper & 0xff) << 8) | ((int)lower & 0xff);
}



//This method gathers data, then parses that data, and assigns the ints to global variables
    public PixyPacket readPacket(int Signature) throws PixyException {
        
        //getversion();
        
        byte[] rawData = new byte[20];
        byte[] requestRawData = {(byte)0xae, (byte)0xc1, 0x20, 0x2, (byte)0xff, 0x5};
        
        // PixyPacket pkt = null;
        try{
            //rawData = pixy.read(32);
           Boolean pixyReturn = pixy.transaction(requestRawData, requestRawData.length, rawData, rawData.length);
           System.out.println(pixyReturn);
            
        } 
        catch (RuntimeException e){
        } 

        if(rawData.length < 32){
            System.out.println("byte array length is broken");
            return null;
        }
  
 		
		System.out.println("block number: " + e);
		System.out.println(
			"byte b0 " + String.format("%d ", (int)rawData[0] & 0xff) + 
			"\nbyte b1 " + String.format("%d ", (int)rawData[1] & 0xff) +
			"\nbyte b2 " + String.format("%d ", (int)rawData[2] & 0xff) +
			"\nbyte b3 " + String.format("%d ", (int)rawData[3] & 0xff) +
			"\nbyte b4 " + String.format("%d ", (int)rawData[4] & 0xff) +
			"\nbyte b5 " + String.format("%d ", (int)rawData[5] & 0xff) +
			"\nbyte b6 " + String.format("%d ", (int)rawData[6] & 0xff)+
			"\nbyte b6hex " + String.format("%02X ", rawData[6] & 0xff) + 
			"\nbyte b7 " + String.format("%d ", (int)rawData[7] & 0xff) +
			"\nbyte b7hex " + String.format("%02X ", rawData[7] & 0xff) + 
			"\nbyte b6-7 " + String.format("%d ", cvt(rawData[6], rawData[7])) +
			"\nbyte b8 " + String.format("%d ", (int)rawData[8] & 0xff) +
			"\nbyte b9 " + String.format("%d ", (int)rawData[9] & 0xff) +
			"\nbyte b10 " + String.format("%d ", (int)rawData[10] & 0xff) +
			"\nbyte b11 " + String.format("%d ", (int)rawData[11] & 0xff) +
			"\nbyte b12 " + String.format("%d ", (int)rawData[12] & 0xff) +
			"\nbyte b13 " + String.format("%d ", (int)rawData[13] & 0xff) +
			"\nbyte b14 " + String.format("%d ", (int)rawData[14] & 0xff) +
			"\nbyte b15 " + String.format("%d ", (int)rawData[15] & 0xff) +
			"\nbyte b16 " + String.format("%d ", (int)rawData[16] & 0xff) +
            "\nbyte b17 " + String.format("%d ", (int)rawData[17] & 0xff) + 
            "\nbyte x: " + String.format("%d", cvt(rawData[8], rawData[9])) +
            "\nbyte y: " + String.format("%d", cvt(rawData[10], rawData[11])));
		
		
		//Assigns our packet to a temp packet, then deletes data so that we dont return old data
        PixyPacket pkt = packets[Signature - 1];
        packets[Signature - 1] = null;
        return pkt;
        //return null;
    }

    public void getversion()
    {
		
		//https://docs.pixycam.com/wiki/doku.php?id=wiki:v2:protocol_reference
		byte[] getfirmwarerequest=
			{
				(byte)0xae, (byte)0xc1, //16 bit sync bytes
				0xe, 					//Type of packet
				0x0						//Length of payload - in this case we are note sending any data
			};
        
        // write data to pixy to get firmware
        byte[] bytes = new byte[12];
        try{
            //rawData = pixy.read(32);
           Boolean pixyReturn = pixy.transaction(getfirmwarerequest, getfirmwarerequest.length, bytes, bytes.length);
           System.out.println(pixyReturn);
            
        } 
        catch (RuntimeException e){
        } 
        
        
        
		System.out.println(
			"firmware byte b1 " + String.format("%02X ", bytes[0] & 0xff) + 
			"\nbyte b2 " + String.format("%02X ", bytes[1] & 0xff) +
			"\nbyte b3 " + String.format("%02X ", bytes[2] & 0xff) +
			"\nbyte b4 " + String.format("%02X ", bytes[3] & 0xff) +
			"\nbyte b5 " + String.format("%02X ", bytes[4] & 0xff) +
			"\nbyte b6 " + String.format("%02X ", bytes[5] & 0xff) +
			"\nbyte b7 " + String.format("%02X ", bytes[6] & 0xff) +
			"\nbyte b8 " + String.format("%02X ", bytes[7] & 0xff) +
			"\nbyte b9 " + String.format("%02X ", bytes[8] & 0xff) +
			"\nbyte b10 " + String.format("%02X ", bytes[9] & 0xff) +
			"\nbyte b11 " + String.format("%02X ", bytes[10] & 0xff) +
			"\nbyte b12 " + String.format("%02X ", bytes[11] & 0xff)
			);
    }
    
    

}
