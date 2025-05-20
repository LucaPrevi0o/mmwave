package ethernet;

public class DataPacket {

    public byte syncByte;
    public short opcode; // uint16_t
    public byte ackCode;
    public short dataLength; // uint16_t
    public byte devSelection;
    public byte ackType;
    public byte[] data; // variable length
    public short crc; // uint16_t

    /**
     * Create a byte array from the given parameters.
     * @return The byte array representing the DataPacket.
     */
    public byte[] serialize() {

        var len = 1+2+1+2+1+1+data.length+2; // 1 + 2 + 1 + 2 + 1 + 1 + data.length + 2
        var buffer = new byte[len]; // Create a new byte array with the calculated length

        buffer[0] = syncByte; // Set the sync byte
        buffer[1] = (byte) ((opcode >> 8) & 0xFF); // Set the high byte of the opcode
        buffer[2] = (byte) (opcode & 0xFF); // Set the low byte of the opcode
        buffer[3] = ackCode; // Set the ack code
        buffer[4] = (byte) ((dataLength >> 8) & 0xFF); // Set the high byte of the data length
        buffer[5] = (byte) (dataLength & 0xFF); // Set the low byte of the data length
        buffer[6] = devSelection; // Set the device selection byte
        buffer[7] = ackType; // Set the ack type byte
        System.arraycopy(data, 0, buffer, 8, data.length); // Copy the data into the buffer starting from index 8
        buffer[len - 2] = (byte) ((crc >> 8) & 0xFF); // Set the high byte of the CRC
        buffer[len - 1] = (byte) (crc & 0xFF); // Set the low byte of the CRC
        return buffer; // Return the serialized byte array
    }

    /**
     * Set the data byte array of the DataPacket. 
     * @param data The data byte array to set.
     */
    public void setData(byte[] data) {

        this.data = data; // Set the data byte array
        this.dataLength = (short) data.length; // Set the data length
        this.crc = computeCRC(); // Compute and set the CRC
    }

    /**
     * Computes the CRC-16-CCITT checksum over the data array.
     * Polynomial: 0x1021, Initial value: 0xFFFF, No reflection, No final XOR.
     * @return The computed CRC value.
     */
    private short computeCRC() {
        
        var crc = 0xFFFF; // Initial value
        for (var b : data) {

            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {

                if ((crc & 0x8000) != 0) crc = (crc << 1) ^ 0x1021;
                else crc <<= 1;
                crc &= 0xFFFF; // Trim to 16 bits
            }
        }
        return (short)crc;
    }

    public DataPacket(byte syncByte, short opcode, byte ackCode, short dataLength, byte devSelection, byte ackType) {

        this.syncByte = syncByte; // Set the sync byte
        this.opcode = opcode; // Set the opcode
        this.ackCode = ackCode; // Set the ack code
        this.dataLength = dataLength; // Set the data length
        this.devSelection = devSelection; // Set the device selection byte
        this.ackType = ackType; // Set the ack type byte
    }
}
