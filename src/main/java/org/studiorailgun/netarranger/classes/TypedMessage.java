package org.studiorailgun.netarranger.classes;

import java.util.HashMap;

import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.Data;
import org.studiorailgun.netarranger.model.MessageType;

/**
 * <p>
 * Represents a specific category of message that we will be parsing for
 * </p>
 * <p>
 * A very dense class, it contains:
 * </p>
 * <ul>
 * <li> Variables representing the contents of the category </li>
 * <li> Getters and setters for the above </li>
 * <li> An enum for different message subtypes </li>
 * <li> A checker function for whether a given byte stream can parse an instance of this class </li>
 * <li> Functions for parsing specific message subtypes from a byte stream </li>
 * <li> Functions for instantiation instances of this class, serialized, with specific message subtypes </li>
 * <li> A serialization function to take an instantiation of this into a series of bytes </li>
 * </ul>
*/
public class TypedMessage extends SourceGenerator {
    
    /**
     * The config file
     */
    ConfigFile config;

    /**
     * The category containing this message
     */
    Category cat;
    
    /**
     * Constructor
     * @param config The config file
     * @param cat The category this message is within
     */
    public TypedMessage(ConfigFile config, Category cat){
        this.config = config;
        this.cat = cat;
    }

    @Override
    public String generateClassSource() {
        boolean usesByteUtils = false;
        boolean hasStringData = false;
        boolean hasFixedData = false;
        //construct map of whether data is fixed size
        HashMap<String,Boolean> fixedSizeVariableMap = new HashMap<String,Boolean>();
        for(Data data : cat.getData()){
            switch(data.getType()){
                case "FIXED_BOOL": {
                    fixedSizeVariableMap.put(data.getName(), true);
                    usesByteUtils = true;
                    hasFixedData = true;
                } break;
                case "FIXED_INT": {
                    fixedSizeVariableMap.put(data.getName(), true);
                    usesByteUtils = true;
                    hasFixedData = true;
                } break;
                case "FIXED_FLOAT": {
                    fixedSizeVariableMap.put(data.getName(), true);
                    hasFixedData = true;
                } break;
                case "FIXED_LONG": {
                    fixedSizeVariableMap.put(data.getName(), true);
                    hasFixedData = true;
                } break;
                case "VAR_STRING": {
                    fixedSizeVariableMap.put(data.getName(), false);
                    usesByteUtils = true;
                    hasStringData = true;
                } break;
                case "FIXED_DOUBLE": {
                    fixedSizeVariableMap.put(data.getName(), true);
                    hasFixedData = true;
                } break;
                case "BYTE_ARRAY": {
                    fixedSizeVariableMap.put(data.getName(), false);
                    usesByteUtils = true;
                } break;
            }
        }
        //construct map of whether the message type is variable size or not
        HashMap<MessageType,Boolean> fixedSizeMessageMap = new HashMap<MessageType,Boolean>();
        for(MessageType type : cat.getMessageTypes()){
            boolean isFixedSize = true;
            for(String varName : type.getData()){
                if(!fixedSizeVariableMap.get(varName)){
                    isFixedSize = false;
                }
            }
            fixedSizeMessageMap.put(type,isFixedSize);
        }
        
        //package header
        String fullFile = "package " + config.getPackageName() + ".net.message;\n\n";
        
        //imports
        fullFile = fullFile + "import java.io.IOException;\n";
        fullFile = fullFile + "import java.io.OutputStream;\n";
        fullFile = fullFile + "import java.nio.ByteBuffer;\n";
        if(usesByteUtils){
            fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        }
        fullFile = fullFile + "import java.util.Map;\n";
        fullFile = fullFile + "import java.util.function.BiConsumer;\n\n";
        
        //add description comment if defined
        if(cat.getDescription() != null){
            fullFile = fullFile + "/**\n";
            fullFile = fullFile + " * " + cat.getDescription() + "\n";
            fullFile = fullFile + " */\n";
        }

        //class name
        fullFile = fullFile + "public class " + cat.getCategoryName() + "Message extends NetworkMessage {\n\n";
        
        //message type enum
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * The types of messages available in this category.\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    public enum " + cat.getCategoryName() + "MessageType {\n";
        for(MessageType type : cat.getMessageTypes()){
            fullFile = fullFile + "        " + type.getMessageName().toUpperCase() + ",\n";
        }
        fullFile = fullFile + "    }\n\n";
        
        //variables
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * The type of this message in particular.\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    " + cat.getCategoryName() + "MessageType messageType;\n";
        for(Data variable : cat.getData()){
            if(variable.getDescription() != null){
                fullFile = fullFile + "    /**\n";
                fullFile = fullFile + "     * " + variable.getDescription() + "\n";
                fullFile = fullFile + "     */\n";
            }
            switch(variable.getType()){
                case "FIXED_BOOL": {
                    fullFile = fullFile + "    boolean " + variable.getName() + ";\n";
                } break;
                case "FIXED_INT": {
                    fullFile = fullFile + "    int " + variable.getName() + ";\n";
                } break;
                case "FIXED_FLOAT": {
                    fullFile = fullFile + "    float " + variable.getName() + ";\n";
                } break;
                case "FIXED_LONG": {
                    fullFile = fullFile + "    long " + variable.getName() + ";\n";
                } break;
                case "VAR_STRING": {
                    fullFile = fullFile + "    String " + variable.getName() + ";\n";
                } break;
                case "BYTE_ARRAY": {
                    fullFile = fullFile + "    byte[] " + variable.getName() + ";\n";
                } break;
                case "FIXED_DOUBLE": {
                    fullFile = fullFile + "    double " + variable.getName() + ";\n";
                } break;
            }
        }
        fullFile = fullFile + "\n";
        
        //constructor
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Constructor\n";
        fullFile = fullFile + "     * @param messageType The type of this message\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    private " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType messageType){\n";
        fullFile = fullFile + "        this.type = MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE;\n";
        fullFile = fullFile + "        this.messageType = messageType;\n";
        fullFile = fullFile + "    }\n\n";

        //second constructor
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Constructor\n";
        fullFile = fullFile + "     */\n";
        fullFile = fullFile + "    protected " + cat.getCategoryName() + "Message(){\n";
        fullFile = fullFile + "        this.type = MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE;\n";
        fullFile = fullFile + "    }\n\n";
        
        //getter and setter for message subtype
        fullFile = fullFile + "    public " + cat.getCategoryName() + "MessageType getMessageSubtype(){\n";
        fullFile = fullFile + "        return this.messageType;\n";
        fullFile = fullFile + "    }\n\n";
        
        //getters and setters for each data
        for(Data variable : cat.getData()){
            switch(variable.getType()){
                case "FIXED_BOOL": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public boolean get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(boolean " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "FIXED_INT": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public int get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(int " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "FIXED_FLOAT": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public float get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(float " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "FIXED_LONG": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public long get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(long " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "VAR_STRING": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public String get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(String " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "BYTE_ARRAY": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public byte[] get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(byte[] " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
                case "FIXED_DOUBLE": {
                    //getter
                    fullFile = this.addGetterComment(fullFile,variable);
                    fullFile = fullFile + "    public double get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = this.addSetterComment(fullFile,variable);
                    fullFile = fullFile + "    public void set" + variable.getName() + "(double " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                } break;
            }
        }
        
        //parse check function
        // fullFile = fullFile + "    /**\n";
        // fullFile = fullFile + "     * Checks if this message can be parsed (ie are all bytes present)\n";
        // fullFile = fullFile + "     * @param byteBuffer The buffer\n";
        // fullFile = fullFile + "     * @param secondByte The second byte, signifying the subtype of the message\n";
        // fullFile = fullFile + "     * @return true if the message can be parsed, false otherwise\n";
        // fullFile = fullFile + "     */\n";
        // fullFile = fullFile + "    public static boolean canParseMessage(ByteBuffer byteBuffer, byte secondByte){\n";
        // fullFile = fullFile + "        switch(secondByte){\n";
        // for(MessageType type : cat.getMessageTypes()){
        //     if(fixedSizeMessageMap.get(type)){
        //         fullFile = fullFile + getFixedSizeParseCheck(cat,type);
        //     } else {
        //         fullFile = fullFile + getVariableSizeParseCheck(cat,type);
        //     }
        // }
        // fullFile = fullFile + "        }\n";
        // fullFile = fullFile + "        return false;\n";
        // fullFile = fullFile + "    }\n\n";
        
        //parse and construct functions
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap<String,String>();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            //parse function
            if(fixedSizeMessageMap.get(type)){
                fullFile = fullFile + getParseFunction(cat,type,typeMap);
            } else {
                // fullFile = fullFile + getParseCheckTypeFunction(cat,type,typeMap);
                fullFile = fullFile + getParseFunction(cat,type,typeMap);
            }
            
            //construct function
            fullFile = fullFile + "    /**\n";
            fullFile = fullFile + "     * Constructs a message of type " + type.getMessageName() + "\n";
            fullFile = fullFile + "     */\n";
            fullFile = fullFile + "    public static " + cat.getCategoryName() + "Message construct" + type.getMessageName() + "Message(";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_BOOL": {
                        fullFile = fullFile + "boolean " + data + ",";
                    } break;
                    case "FIXED_INT": {
                        fullFile = fullFile + "int " + data + ",";
                    } break;
                    case "FIXED_FLOAT": {
                        fullFile = fullFile + "float " + data + ",";
                    } break;
                    case "FIXED_LONG": {
                        fullFile = fullFile + "long " + data + ",";
                    } break;
                    case "VAR_STRING": {
                        fullFile = fullFile + "String " + data + ",";
                    } break;
                    case "BYTE_ARRAY": {
                        fullFile = fullFile + "byte[] " + data + ",";
                    } break;
                    case "FIXED_DOUBLE": {
                        fullFile = fullFile + "double " + data + ",";
                    } break;
                }
            }
            //chop off last comma
            if(type.getData().size() > 0){
                fullFile = fullFile.substring(0, fullFile.length() - 1);
            }
            fullFile = fullFile + "){\n";
            fullFile = fullFile + "        " + cat.getCategoryName() + "Message rVal = new " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ");\n";
            for(String data : type.getData()){
                fullFile = fullFile + "        rVal.set" + data + "(" + data + ");\n";
            }
            fullFile = fullFile + "        return rVal;\n";
            fullFile = fullFile + "    }\n\n";
        }
        
        
        //serialize function
        fullFile = fullFile + "    @Deprecated\n";
        fullFile = fullFile + "    @Override\n";
        fullFile = fullFile + "    void serialize(){\n";
        if(hasFixedData || hasStringData){
            fullFile = fullFile + "        byte[] intValues = new byte[8];\n";
        }
        if(hasStringData){
            fullFile = fullFile + "        byte[] stringBytes;\n";
        }
        fullFile = fullFile + "        switch(this.messageType){\n";
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap<String,String>();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            fullFile = fullFile + "            case " + type.getMessageName().toUpperCase() + ":\n";
            String packetSizeCalculation = "2"; // 2 for packet header
            for(String variable : type.getData()){
                switch(typeMap.get(variable)){
                    case "FIXED_BOOL": {
                        packetSizeCalculation = packetSizeCalculation + "+1";
                    } break;
                    case "FIXED_INT": {
                        packetSizeCalculation = packetSizeCalculation + "+4";
                    } break;
                    case "FIXED_FLOAT": {
                        packetSizeCalculation = packetSizeCalculation + "+4";
                    } break;
                    case "FIXED_LONG": {
                        packetSizeCalculation = packetSizeCalculation + "+8";
                    } break;
                    case "VAR_STRING": {
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length()"; // 4 for integer header
                    } break;
                    case "BYTE_ARRAY": {
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length"; // 4 for integer header
                    } break;
                    case "FIXED_DOUBLE": {
                        packetSizeCalculation = packetSizeCalculation + "+8";
                    } break;
                }
            }
            fullFile = fullFile + "                rawBytes = new byte[" + packetSizeCalculation + "];\n";
            fullFile = fullFile + "                //message header\n";
            fullFile = fullFile + "                rawBytes[0] = TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ";\n";
            fullFile = fullFile + "                //entity messaage header\n";
            fullFile = fullFile + "                rawBytes[1] = TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ";\n";
            int offset = 2;
            String offsetFunctions = "";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_BOOL": {
                        fullFile = fullFile + "                rawBytes[" + offset + offsetFunctions + "] = " + data + " ? (byte)1 : (byte)0;\n";
                        offset = offset + 4;
                    } break;
                    case "FIXED_INT": {
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                    } break;
                    case "FIXED_FLOAT": {
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeFloatToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }";
                        offset = offset + 4;
                    } break;
                    case "FIXED_LONG": {
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeLongToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 8; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 8;
                    } break;
                    case "VAR_STRING": {
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ".length());\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        fullFile = fullFile + "                stringBytes = " + data + ".getBytes();\n";
                        fullFile = fullFile + "                for(int i = 0; i < " + data + ".length(); i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = stringBytes[i];\n";
                        fullFile = fullFile + "                }\n";
                        offsetFunctions = offsetFunctions + "+" + data + ".length()";
                    } break;
                    case "BYTE_ARRAY": {
                        //serialize header that contains length of byte array
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ".length);\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        //serialize actual bytes
                        fullFile = fullFile + "                for(int i = 0; i < " + data + ".length; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = " + data + "[i];\n";
                        fullFile = fullFile + "                }\n";
                        offsetFunctions = offsetFunctions + "+" + data + ".length";
                    } break;
                    case "FIXED_DOUBLE": {
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeDoubleToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 8; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 8;
                    } break;
                }
            }
            fullFile = fullFile + "                break;\n";
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        serialized = true;\n";
        fullFile = fullFile + "    }\n\n";


        //write method
        fullFile = fullFile + "    @Override\n";
        fullFile = fullFile + "    public void write(OutputStream stream) throws IOException {\n";
        fullFile = fullFile + "        switch(this.messageType){\n";
        for(MessageType type : cat.getMessageTypes()){
            //get all data types
            HashMap<String,String> typeMap = new HashMap<String,String>();
            for(Data variable : cat.getData()){
                typeMap.put(variable.getName(), variable.getType());
            }
            fullFile = fullFile + "            case " + type.getMessageName().toUpperCase() + ": {\n";
            String packetSizeCalculation = "2"; // 2 for packet header
            boolean variableLengthPacket = false;
            for(String variable : type.getData()){
                switch(typeMap.get(variable)){
                    case "FIXED_BOOL": {
                        packetSizeCalculation = packetSizeCalculation + "+1";
                    } break;
                    case "FIXED_INT": {
                        packetSizeCalculation = packetSizeCalculation + "+4";
                    } break;
                    case "FIXED_FLOAT": {
                        packetSizeCalculation = packetSizeCalculation + "+4";
                    } break;
                    case "FIXED_LONG": {
                        packetSizeCalculation = packetSizeCalculation + "+8";
                    } break;
                    case "VAR_STRING": {
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length()"; // 4 for integer header
                        variableLengthPacket = true;
                    } break;
                    case "BYTE_ARRAY": {
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length"; // 4 for integer header
                        variableLengthPacket = true;
                    } break;
                    case "FIXED_DOUBLE": {
                        packetSizeCalculation = packetSizeCalculation + "+8";
                    } break;
                }
            }
            fullFile = fullFile + "                \n";
            fullFile = fullFile + "                //\n";
            fullFile = fullFile + "                //message header\n";
            fullFile = fullFile + "                stream.write(TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + ");\n";
            fullFile = fullFile + "                stream.write(TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ");\n";

            //
            //write variable length table if relevant
            if(variableLengthPacket){
                fullFile = fullFile + "                \n";
                fullFile = fullFile + "                //\n";
                fullFile = fullFile + "                //Write variable length table in packet\n";
                for(String data : type.getData()){
                    switch(typeMap.get(data)){
                        case "VAR_STRING": {
                            fullFile = fullFile + "                ByteStreamUtils.writeInt(stream, " + data + ".getBytes().length);\n";
                        } break;
                        case "BYTE_ARRAY": {
                            //serialize header that contains length of byte array
                            fullFile = fullFile + "                ByteStreamUtils.writeInt(stream, " + data + ".length);\n";
                        } break;
                    }
                }
            }

            fullFile = fullFile + "                \n";
            fullFile = fullFile + "                //\n";
            fullFile = fullFile + "                //Write body of packet\n";
            String offsetFunctions = "";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_BOOL": {
                        fullFile = fullFile + "                stream.write(" + data + " ? (byte)1 : (byte)0);\n";
                    } break;
                    case "FIXED_INT": {
                        fullFile = fullFile + "                ByteStreamUtils.writeInt(stream, " + data + ");\n";
                    } break;
                    case "FIXED_FLOAT": {
                        fullFile = fullFile + "                ByteStreamUtils.writeFloat(stream, " + data + ");\n";
                    } break;
                    case "FIXED_LONG": {
                        fullFile = fullFile + "                ByteStreamUtils.writeLong(stream, " + data + ");\n";
                    } break;
                    case "VAR_STRING": {
                        fullFile = fullFile + "                ByteStreamUtils.writeString(stream, " + data + ");\n";
                        offsetFunctions = offsetFunctions + "+" + data + ".length()";
                    } break;
                    case "BYTE_ARRAY": {
                        //serialize actual bytes
                        fullFile = fullFile + "                stream.write(" + data + ");\n";
                        offsetFunctions = offsetFunctions + "+" + data + ".length";
                    } break;
                    case "FIXED_DOUBLE": {
                        fullFile = fullFile + "                ByteStreamUtils.writeDouble(stream, " + data + ");\n";
                    } break;
                }
            }
            fullFile = fullFile + "            } break;\n";
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "    }\n\n";
        
        //end class
        fullFile = fullFile + "}\n";
        
        return fullFile;
    }
    
    /**
     * Gets the fixed size variable parse check conditional logic
     * @param cat The category of the message
     * @param type The type of the message
     * @return The conditional logic as a string
     */
    static String getFixedSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                if(byteBuffer.remaining() >= TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE){\n";
        rVal = rVal + "                    return true;\n";
        rVal = rVal + "                } else {\n";
        rVal = rVal + "                    return false;\n";
        rVal = rVal + "                }\n";
        return rVal;
    }
    
    /**
     * Gets the conditional logic for checking variable size in the parse checker method
     * @param cat The category othe message
     * @param type The type of the message
     * @return The conditional logic as a string
     */
    static String getVariableSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                return " + cat.getCategoryName() + "Message.canParse" + type.getMessageName() + "Message(byteBuffer);\n";
        return rVal;
    }
    
    /**
     * Gets the body for the method to parse a given type of message
     * @param cat The category of the message
     * @param type The type of the message
     * @param typeMap The type map for the variables
     * @return The method body as a string
     */
    static String getParseFunction(Category cat, MessageType type, HashMap<String,String> typeMap){
        String rVal = "";
        rVal = rVal + "    /**\n";
        rVal = rVal + "     * Parses a message of type " + type.getMessageName() + "\n";
        rVal = rVal + "     */\n";
        rVal = rVal + "    public static " + cat.getCategoryName() + "Message parse" + type.getMessageName() + "Message(ByteBuffer byteBuffer, MessagePool pool, Map<Short,BiConsumer<NetworkMessage,ByteBuffer>> customParserMap){\n";


        //
        //Check that we can read the message
        //
        int currentLength = 0;
        boolean hasVariableLength = false;
        //Need to keep track of the variables that themselves have variable length
        //so we can check them when accounting for packet length
        for(String currentData : type.getData()){
            switch(typeMap.get(currentData)){
                case "FIXED_BOOL": {
                    currentLength = currentLength + 4; // size of boolean
                } break;
                case "FIXED_INT": {
                    currentLength = currentLength + 4; // size of int
                } break;
                case "FIXED_FLOAT": {
                    currentLength = currentLength + 4; // size of float
                } break;
                case "FIXED_LONG": {
                    currentLength = currentLength + 8; // size of long
                } break;
                case "VAR_STRING": {
                    currentLength = currentLength + 4; // size of entry in variable length table
                    hasVariableLength = true;
                } break;
                case "BYTE_ARRAY": {
                    hasVariableLength = true;
                    currentLength = currentLength + 4; // size of entry in variable length table
                } break;
                case "FIXED_DOUBLE": {
                    currentLength = currentLength + 8; // size of long
                } break;
            }
        }
        rVal = rVal + "        if(byteBuffer.remaining() < " + currentLength +"){\n";
        rVal = rVal + "            return null;\n";
        rVal = rVal + "        }\n";

        //
        //if there are variable-length types, read the variable length table
        if(hasVariableLength){
            rVal = rVal + "        int lenAccumulator = 0;\n";
            for(String currentData : type.getData()){
                switch(typeMap.get(currentData)){
                    case "VAR_STRING": {
                        rVal = rVal + "        int " + currentData + "len = byteBuffer.getInt();\n";
                        rVal = rVal + "        lenAccumulator = lenAccumulator + " + currentData + "len;\n";
                    } break;
                    case "BYTE_ARRAY": {
                        rVal = rVal + "        int " + currentData + "len = byteBuffer.getInt();\n";
                        rVal = rVal + "        lenAccumulator = lenAccumulator + " + currentData + "len;\n";
                    } break;
                }
            }
            rVal = rVal + "        if(byteBuffer.remaining() < " + currentLength + " + lenAccumulator){\n";
            rVal = rVal + "            return null;\n";
            rVal = rVal + "        }\n";
        }



        //
        //Actually read the message
        //

        rVal = rVal + "        " + cat.getCategoryName() + "Message rVal = (" + cat.getCategoryName() + "Message)pool.get(MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE);\n";
        rVal = rVal + "        rVal.messageType = " + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ";\n";
        //check for parser type
        if(type.getCustomParser() != null && type.getCustomParser() == true){
            //custom parser provided by user per-app
            rVal = rVal + "        short pair = (short)((TypeBytes.MESSAGE_TYPE_" + cat.getCategoryName().toUpperCase() + " << 4) | TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ");\n";
            rVal = rVal + "        BiConsumer<NetworkMessage,ByteBuffer> customParser = customParserMap.get(pair);\n";
            rVal = rVal + "        if(customParser == null){\n";
            rVal = rVal + "            throw new Error(\"Custom parser undefined for message pair!\");\n";
            rVal = rVal + "        }\n";
            rVal = rVal + "        customParser.accept(rVal,byteBuffer);\n";
        } else {
            //traditional parsing
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_BOOL": {
                        rVal = rVal + "        rVal.set" + data + "(byteBuffer.get() == 1 ? true : false);\n";
                    } break;
                    case "FIXED_INT": {
                        rVal = rVal + "        rVal.set" + data + "(byteBuffer.getInt());\n";
                    } break;
                    case "FIXED_FLOAT": {
                        rVal = rVal + "        rVal.set" + data + "(byteBuffer.getFloat());\n";
                    } break;
                    case "FIXED_LONG": {
                        rVal = rVal + "        rVal.set" + data + "(byteBuffer.getLong());\n";
                    } break;
                    case "VAR_STRING": {
                        rVal = rVal + "        if(" + data + "len > 0){\n";
                        rVal = rVal + "            rVal.set" + data + "(ByteStreamUtils.popStringFromByteBuffer(byteBuffer, " + data + "len));\n";
                        rVal = rVal + "        }\n";
                    } break;
                    case "BYTE_ARRAY": {
                        rVal = rVal + "        if(" + data + "len > 0){\n";
                        rVal = rVal + "            rVal.set" + data + "(ByteStreamUtils.popByteArrayFromByteBuffer(byteBuffer, " + data + "len));\n";
                        rVal = rVal + "        }\n";
                    } break;
                    case "FIXED_DOUBLE": {
                        rVal = rVal + "        rVal.set" + data + "(byteBuffer.getDouble());\n";
                    } break;
                }
            }
        }
        rVal = rVal + "        return rVal;\n";
        rVal = rVal + "    }\n\n";
        return rVal;
    }

    /**
     * Adds the getter comment to the string
     * @param fullFile The full file string
     * @param variable The variable
     * @return The getter comment
     */
    private String addGetterComment(String fullFile, Data variable){
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Gets " + variable.getName();
        if(variable.getDescription() != null){
            fullFile = fullFile + " - " + variable.getDescription() + "\n";
        } else {
            fullFile = fullFile + "\n";
        }
        fullFile = fullFile + "     */\n";
        return fullFile;
    }

    /**
     * Adds the setter comment to the string
     * @param fullFile The full file string
     * @param variable The variable
     * @return The setter comment
     */
    private String addSetterComment(String fullFile, Data variable){
        fullFile = fullFile + "    /**\n";
        fullFile = fullFile + "     * Sets " + variable.getName();
        if(variable.getDescription() != null){
            fullFile = fullFile + " - " + variable.getDescription() + "\n";
        } else {
            fullFile = fullFile + "\n";
        }
        fullFile = fullFile + "     */\n";
        return fullFile;
    }
    
}
