package org.studiorailgun.netarranger.classes;

import java.util.HashMap;

import org.studiorailgun.netarranger.model.Category;
import org.studiorailgun.netarranger.model.ConfigFile;
import org.studiorailgun.netarranger.model.Data;
import org.studiorailgun.netarranger.model.MessageType;

/*

Represents a specific category of message that we will be parsing for

A very dense class, it contains:

variables representing the contents of the category
getters and setters for the above
an enum for different message subtypes
a checker function for whether a given byte stream can parse an instance of this class
functions for parsing specific message subtypes from a byte stream
functions for instantiation instances of this class, serialized, with specific message subtypes
a serialization function to take an instantiation of this into a series of bytes

*/

public class TypedMessage extends SourceGenerator {
    
    ConfigFile config;
    Category cat;
    
    public TypedMessage(ConfigFile config, Category cat){
        this.config = config;
        this.cat = cat;
    }

    @Override
    public String generateClassSource() {
        //construct map of whether data is fixed size
        HashMap<String,Boolean> fixedSizeVariableMap = new HashMap<String,Boolean>();
        for(Data data : cat.getData()){
            switch(data.getType()){
                case "FIXED_INT":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "FIXED_FLOAT":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "FIXED_LONG":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "VAR_STRING":
                    fixedSizeVariableMap.put(data.getName(), false);
                    break;
                case "FIXED_DOUBLE":
                    fixedSizeVariableMap.put(data.getName(), true);
                    break;
                case "BYTE_ARRAY":
                    fixedSizeVariableMap.put(data.getName(), false);
                    break;
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
        //attach ByteUtils
        fullFile = fullFile + "import " + config.getPackageName() + ".util.ByteStreamUtils;\n";
        fullFile = fullFile + "import " + config.getPackageName() + ".net.raw.CircularByteBuffer;\n";
        fullFile = fullFile + "import java.util.LinkedList;\n";
        fullFile = fullFile + "import java.util.List;\n\n";
        
        //class name
        fullFile = fullFile + "public class " + cat.getCategoryName() + "Message extends NetworkMessage {\n\n";
        
        //message type enum
        fullFile = fullFile + "    public enum " + cat.getCategoryName() + "MessageType {\n";
        for(MessageType type : cat.getMessageTypes()){
            fullFile = fullFile + "        " + type.getMessageName().toUpperCase() + ",\n";
        }
        fullFile = fullFile + "    }\n\n";
        
        //variables
        fullFile = fullFile + "    " + cat.getCategoryName() + "MessageType messageType;\n";
        for(Data variable : cat.getData()){
            switch(variable.getType()){
                case "FIXED_INT":
                    fullFile = fullFile + "    int " + variable.getName() + ";\n";
                    break;
                case "FIXED_FLOAT":
                    fullFile = fullFile + "    float " + variable.getName() + ";\n";
                    break;
                case "FIXED_LONG":
                    fullFile = fullFile + "    long " + variable.getName() + ";\n";
                    break;
                case "VAR_STRING":
                    fullFile = fullFile + "    String " + variable.getName() + ";\n";
                    break;
                case "BYTE_ARRAY":
                    fullFile = fullFile + "    byte[] " + variable.getName() + ";\n";
                    break;
                case "FIXED_DOUBLE":
                    fullFile = fullFile + "    double " + variable.getName() + ";\n";
                    break;
            }
        }
        fullFile = fullFile + "\n";
        
        //constructor
        fullFile = fullFile + "    " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType messageType){\n";
        fullFile = fullFile + "        this.type = MessageType." + cat.getCategoryName().toUpperCase() + "_MESSAGE;\n";
        fullFile = fullFile + "        this.messageType = messageType;\n";
        fullFile = fullFile + "    }\n\n";
        
        //getter and setter for message subtype
        fullFile = fullFile + "    public " + cat.getCategoryName() + "MessageType getMessageSubtype(){\n";
        fullFile = fullFile + "        return this.messageType;\n";
        fullFile = fullFile + "    }\n\n";
        
        //getters and setters for each data
        for(Data variable : cat.getData()){
            switch(variable.getType()){
                case "FIXED_INT":
                    //getter
                    fullFile = fullFile + "    public int get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(int " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "FIXED_FLOAT":
                    //getter
                    fullFile = fullFile + "    public float get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(float " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "FIXED_LONG":
                    //getter
                    fullFile = fullFile + "    public long get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(long " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "VAR_STRING":
                    //getter
                    fullFile = fullFile + "    public String get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(String " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "BYTE_ARRAY":
                    //getter
                    fullFile = fullFile + "    public byte[] get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(byte[] " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
                case "FIXED_DOUBLE":
                    //getter
                    fullFile = fullFile + "    public double get" + variable.getName() + "() {\n";
                    fullFile = fullFile + "        return " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    //setter
                    fullFile = fullFile + "    public void set" + variable.getName() + "(double " + variable.getName() + ") {\n";
                    fullFile = fullFile + "        this." + variable.getName() + " = " + variable.getName() + ";\n";
                    fullFile = fullFile + "    }\n\n";
                    break;
            }
        }
        
        //strip packet header
        fullFile = fullFile + "    static void stripPacketHeader(CircularByteBuffer byteBuffer){\n";
        fullFile = fullFile + "        byteBuffer.read(2);\n";
        fullFile = fullFile + "    }\n\n";
        
        //parse check function
        fullFile = fullFile + "    public static boolean canParseMessage(CircularByteBuffer byteBuffer, byte secondByte){\n";
        fullFile = fullFile + "        switch(secondByte){\n";
        for(MessageType type : cat.getMessageTypes()){
            if(fixedSizeMessageMap.get(type)){
                fullFile = fullFile + getFixedSizeParseCheck(cat,type);
            } else {
                fullFile = fullFile + getVariableSizeParseCheck(cat,type);
            }
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        return false;\n";
        fullFile = fullFile + "    }\n\n";
        
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
                fullFile = fullFile + getParseCheckTypeFunction(cat,type,typeMap);
                fullFile = fullFile + getParseFunction(cat,type,typeMap);
            }
            
            //construct function
            fullFile = fullFile + "    public static " + cat.getCategoryName() + "Message construct" + type.getMessageName() + "Message(";
            for(String data : type.getData()){
                switch(typeMap.get(data)){
                    case "FIXED_INT":
                        fullFile = fullFile + "int " + data + ",";
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "float " + data + ",";
                        break;
                    case "FIXED_LONG":
                        fullFile = fullFile + "long " + data + ",";
                        break;
                    case "VAR_STRING":
                        fullFile = fullFile + "String " + data + ",";
                        break;
                    case "BYTE_ARRAY":
                        fullFile = fullFile + "byte[] " + data + ",";
                        break;
                    case "FIXED_DOUBLE":
                        fullFile = fullFile + "double " + data + ",";
                        break;
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
            fullFile = fullFile + "        rVal.serialize();\n";
            fullFile = fullFile + "        return rVal;\n";
            fullFile = fullFile + "    }\n\n";
        }
        
        
        //serialize function
        fullFile = fullFile + "    @Override\n";
        fullFile = fullFile + "    void serialize(){\n";
        fullFile = fullFile + "        byte[] intValues = new byte[8];\n";
        fullFile = fullFile + "        byte[] stringBytes;\n";
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
                    case "FIXED_INT":
                        packetSizeCalculation = packetSizeCalculation + "+4";
                        break;
                    case "FIXED_FLOAT":
                        packetSizeCalculation = packetSizeCalculation + "+4";
                        break;
                    case "FIXED_LONG":
                        packetSizeCalculation = packetSizeCalculation + "+8";
                        break;
                    case "VAR_STRING":
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length()"; // 4 for integer header
                        break;
                    case "BYTE_ARRAY":
                        packetSizeCalculation = packetSizeCalculation + "+4+" + variable + ".length"; // 4 for integer header
                        break;
                    case "FIXED_DOUBLE":
                        packetSizeCalculation = packetSizeCalculation + "+8";
                        break;
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
                    case "FIXED_INT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeIntToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 4;
                        break;
                    case "FIXED_FLOAT":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeFloatToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 4; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }";
                        offset = offset + 4;
                        break;
                    case "FIXED_LONG":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeLongToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 8; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 8;
                        break;
                    case "VAR_STRING":
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
                        break;
                    case "BYTE_ARRAY":
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
                        break;
                    case "FIXED_DOUBLE":
                        fullFile = fullFile + "                intValues = ByteStreamUtils.serializeDoubleToBytes(" + data + ");\n";
                        fullFile = fullFile + "                for(int i = 0; i < 8; i++){\n";
                        fullFile = fullFile + "                    rawBytes[" + offset + offsetFunctions + "+i] = intValues[i];\n";
                        fullFile = fullFile + "                }\n";
                        offset = offset + 8;
                        break;
                }
            }
            fullFile = fullFile + "                break;\n";
        }
        fullFile = fullFile + "        }\n";
        fullFile = fullFile + "        serialized = true;\n";
        fullFile = fullFile + "    }\n\n";
        
        //end class
        fullFile = fullFile + "}\n";
        
        return fullFile;
    }
    
    static String getFixedSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                if(byteBuffer.getRemaining() >= TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + "_SIZE){\n";
        rVal = rVal + "                    return true;\n";
        rVal = rVal + "                } else {\n";
        rVal = rVal + "                    return false;\n";
        rVal = rVal + "                }\n";
        return rVal;
    }
    
    static String getVariableSizeParseCheck(Category cat, MessageType type){
        String rVal = "";
        rVal = rVal + "            case TypeBytes." + cat.getCategoryName().toUpperCase() + "_MESSAGE_TYPE_" + type.getMessageName().toUpperCase() + ":\n";
        rVal = rVal + "                return " + cat.getCategoryName() + "Message.canParse" + type.getMessageName() + "Message(byteBuffer);\n";
        return rVal;
    }
    
    static String getParseFunction(Category cat, MessageType type, HashMap<String,String> typeMap){
        String rVal = "";
        rVal = rVal + "    public static " + cat.getCategoryName() + "Message parse" + type.getMessageName() + "Message(CircularByteBuffer byteBuffer){\n";
        rVal = rVal + "        " + cat.getCategoryName() + "Message rVal = new " + cat.getCategoryName() + "Message(" + cat.getCategoryName() + "MessageType." + type.getMessageName().toUpperCase() + ");\n";
        rVal = rVal + "        stripPacketHeader(byteBuffer);\n";
        for(String data : type.getData()){
            switch(typeMap.get(data)){
                case "FIXED_INT":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popIntFromByteQueue(byteBuffer));\n";
                    break;
                case "FIXED_FLOAT":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popFloatFromByteQueue(byteBuffer));\n";
                    break;
                case "FIXED_LONG":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popLongFromByteQueue(byteBuffer));\n";
                    break;
                case "VAR_STRING":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popStringFromByteQueue(byteBuffer));\n";
                    break;
                case "BYTE_ARRAY":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popByteArrayFromByteQueue(byteBuffer));\n";
                    break;
                case "FIXED_DOUBLE":
                    rVal = rVal + "        rVal.set" + data + "(ByteStreamUtils.popDoubleFromByteQueue(byteBuffer));\n";
                    break;
            }
        }
        rVal = rVal + "        return rVal;\n";
        rVal = rVal + "    }\n\n";
        return rVal;
    }
    
    static String getParseCheckTypeFunction(Category cat, MessageType type, HashMap<String,String> typeMap){
        String rVal = "";
        rVal = rVal + "    public static boolean canParse" + type.getMessageName() + "Message(CircularByteBuffer byteBuffer){\n";
        rVal = rVal + "        int currentStreamLength = byteBuffer.getRemaining();\n";
        rVal = rVal + "        List<Byte> temporaryByteQueue = new LinkedList<Byte>();\n";
        int currentLength = 2;
        //Need to keep track of the variables that themselves have variable length
        //so we can check them when accounting for packet length
        String variableLengthVars = "";
        for(String currentData : type.getData()){
            switch(typeMap.get(currentData)){
                case "FIXED_INT":
                    currentLength = currentLength + 4; // size of int
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    } else {
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    }
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "FIXED_FLOAT":
                    currentLength = currentLength + 4; // size of float
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    } else {
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    }
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "FIXED_LONG":
                    currentLength = currentLength + 8; // size of long
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    } else {
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    }
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "VAR_STRING":
                    currentLength = currentLength + 4; // size of int
                    rVal = rVal + "        int " + currentData + "Size = 0;\n";
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n"; // have to account for integer header
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        } else {\n";
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 0));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 1));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 2));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 3));\n";
                    } else {
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 0));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 1));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 2));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 3));\n";
                    }
                    rVal = rVal + "            " + currentData + "Size = ByteStreamUtils.popIntFromByteQueue(temporaryByteQueue);\n";
                    rVal = rVal + "        }\n";
                    if(variableLengthVars.length() > 0){
                        variableLengthVars = variableLengthVars + " + ";
                    }
                    variableLengthVars = variableLengthVars + currentData + "Size";
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "BYTE_ARRAY":
                    currentLength = currentLength + 4; // size of int
                    rVal = rVal + "        int " + currentData + "Size = 0;\n";
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n"; // have to account for integer header
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        } else {\n";
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 0));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 1));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 2));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + " + variableLengthVars + " + 3));\n";
                    } else {
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 0));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 1));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 2));\n";
                        rVal = rVal + "            temporaryByteQueue.add(byteBuffer.peek(" + (currentLength - 4) + " + 3));\n";
                    }
                    rVal = rVal + "            " + currentData + "Size = ByteStreamUtils.popIntFromByteQueue(temporaryByteQueue);\n";
                    rVal = rVal + "        }\n";
                    if(variableLengthVars.length() > 0){
                        variableLengthVars = variableLengthVars + " + ";
                    }
                    variableLengthVars = variableLengthVars + currentData + "Size";
                    rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
                case "FIXED_DOUBLE":
                    currentLength = currentLength + 8; // size of long
                    if(variableLengthVars.length() > 0){
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + " + " + variableLengthVars + "){\n";
                    } else {
                        rVal = rVal + "        if(currentStreamLength < " + currentLength + "){\n";
                    }
                    rVal = rVal + "            return false;\n";
                    rVal = rVal + "        }\n";
                    break;
            }
        }
        rVal = rVal + "        return true;\n";
        rVal = rVal + "    }\n\n";
        return rVal;
    }
    
}
