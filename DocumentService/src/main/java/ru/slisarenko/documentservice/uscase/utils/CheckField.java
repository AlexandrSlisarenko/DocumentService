package ru.slisarenko.documentservice.uscase.utils;

import java.util.Arrays;
import java.util.UUID;
import ru.slisarenko.documentservice.enums.Command;
import ru.slisarenko.documentservice.enums.Status;

public final class CheckField {
    private CheckField() {}

    public static boolean checkEmptyAndLength(String field){
        return !field.isEmpty() && field.length() < 64;
    }

    public static boolean checkUUID(String strUuid){
        if(checkEmpty(strUuid)){
            return false;
        }
        try{
             UUID.fromString(strUuid);
        }catch(IllegalArgumentException e){
            return false;
        }
        return true;
    }

    public static boolean checkCommand(String str){
        return Arrays.stream(Command.values()).map(Command::toString).toList().contains(str);
    }

    public static boolean checkStatus(String str){
        return Arrays.stream(Status.values()).map(Status::toString).toList().contains(str);
    }

    public static boolean checkEmpty(String str){
        return !str.isEmpty();
    }
}
